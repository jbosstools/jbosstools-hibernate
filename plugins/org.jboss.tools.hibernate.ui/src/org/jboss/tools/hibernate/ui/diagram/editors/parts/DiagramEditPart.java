/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.rulers.RulerProvider;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.AutoLayout;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.IItemInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.ILinkInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.IDiagramInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Connection;
import org.jboss.tools.hibernate.ui.diagram.editors.model.ModelElement;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.SpecialOrmShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.SpecialRootClass;
import org.jboss.tools.hibernate.ui.view.HibernateUtils;

/**
 *
 */
class DiagramEditPart extends OrmEditPart implements PropertyChangeListener {

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, 
			new ShapesXYLayoutEditPolicy((XYLayout)getContentPane().getLayoutManager()));
	}

	protected IFigure createFigure() {
		Figure f = new FreeformLayer();
		f.setBorder(new MarginBorder(3));
		f.setLayoutManager(new FreeformLayout());

		ConnectionLayer connLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
		connLayer.setConnectionRouter(new ManhattanConnectionRouter());

		return f;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (OrmDiagram.REFRESH.equals(prop)) {
			refresh();
			autolayout();
			// restore();
		} else if (OrmDiagram.DIRTY.equals(prop)) {
			((DiagramViewer) ((DefaultEditDomain) getViewer().getEditDomain())
					.getEditorPart()).refreshDirty();
		}
	}

	public void restore() {
		boolean dirty = getOrmDiagram().isDirty();
		HashMap<String, OrmShape> hashMap = getOrmDiagram().getCloneElements();
		String childrenLocations[] = getOrmDiagram().getChildrenLocations();
		int tempPoint = 1;
		OrmShape ormShape;
		int point = 1;
		int pointX = calculateTableLocation();
		String string, xy[];
		for (int i = 0; i < childrenLocations.length; i++) {
			if (childrenLocations[i].indexOf('@') != -1
					&& childrenLocations[i].indexOf(';') != -1) {
				string = childrenLocations[i].substring(0, childrenLocations[i]
						.indexOf('@'));
				ormShape = hashMap.remove(string);
				if (ormShape != null) {
					string = childrenLocations[i]
							.substring(childrenLocations[i].indexOf('@') + 1);
					xy = string.split(";"); //$NON-NLS-1$
					if (xy.length > 1) {
						try {
							ormShape.setLocation(new Point(Integer
									.parseInt(xy[0]), Integer.parseInt(xy[1])));
						} catch (NumberFormatException e) {
							HibernateConsolePlugin.getDefault().logErrorMessage("NumberFormatException: ", e); //$NON-NLS-1$
						}
					}
					if (xy.length > 2) {
						if ((Boolean.valueOf(xy[2]))) {
							ormShape.refreshHiden();
						}
					}
					tempPoint = ormShape.getLocation().y
							+ getChildrenFigurePreferredHeight(ormShape) + 20;
					if (tempPoint > point) {
						point = tempPoint;
					}
				}
			}
		}
		RootClass[] ormElements = getOrmDiagram().getOrmElements();
		for (int i = 0; i < childrenLocations.length; i++) {
			RootClass persistentClass = ormElements[i];
			ormShape = hashMap.remove(persistentClass.getEntityName());
			if (ormShape != null) {
				ormShape.setLocation(new Point(20, 20));
				tempPoint = 40 + getChildrenFigurePreferredHeight(ormShape);
			}
			Table table = persistentClass.getTable();
			ormShape = hashMap.remove(HibernateUtils.getTableName(table));
			if (ormShape != null) {
				ormShape.setLocation(new Point(pointX, 20));
				point = 40 + getChildrenFigurePreferredHeight(ormShape);
			}
			if (tempPoint > point) {
				point = tempPoint;
			}
		}
		Object objects[] = hashMap.keySet().toArray();
		for (int i = 0; i < objects.length; i++) {
			ormShape = hashMap.get(objects[i]);
			if (ormShape != null
					&& (ormShape.getOrmElement() instanceof RootClass || ormShape
							.getOrmElement() instanceof SpecialOrmShape)) {
				ormShape.setLocation(new Point(20, point));
				tempPoint = point + getChildrenFigurePreferredHeight(ormShape)
						+ 20;
				// if (ormShape.getOrmElement() instanceof SpecialRootClass) {
				Component component = (Component) ((Collection) ((SpecialRootClass) (ormShape
						.getOrmElement())).getProperty().getValue())
						.getElement();
				Table ownerTable = component.getOwner().getTable();
				ormShape = hashMap.remove(HibernateUtils.getTableName(ownerTable));
				// }
				// if (ormShape != null ) {
				// ormShape.setLocation(new Point(pointX,point));
				// point = point + getChildrenFigurePreferredHeight(ormShape) +
				// 20;
				// }
				if (tempPoint > point) {
					point = tempPoint;
				}
			}
		}
		for (OrmShape shape : hashMap.values()) {
			if (shape.getOrmElement() instanceof Table) {
				shape.setLocation(new Point(pointX, point));
				point = point + getChildrenFigurePreferredHeight(shape) + 20;
			}
		}
		getOrmDiagram().setDirty(dirty);
	}

	private OrmDiagram getOrmDiagram() {
		return (OrmDiagram) getModel();
	}

	private int getChildrenFigurePreferredHeight(OrmShape ormShape) {
		GraphicalEditPart part;
		for (int i = 0; i < getChildren().size(); i++) {
			part = (GraphicalEditPart) getChildren().get(i);
			if (ormShape.equals(part.getModel())) {
				return part.getFigure().getPreferredSize().height;
			}
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	private int calculateTableLocation() {
		int j = 0;
		List<IFigure> children = getFigure().getChildren();
		for (int i = 0; i < children.size(); i++) {
			IFigure figure = children.get(i);
			if (figure.getPreferredSize().width > j) {
				j = figure.getPreferredSize().width;
			}
		}
		return j + 120;
	}

	/**
	 * Returns a <code>List</code> containing the children model objects.
	 * @return the List of children
	 */
	@Override
	protected List<Shape> getModelChildren() {
		List<Shape> res = new ArrayList<Shape>();
		Iterator<Shape> it = getOrmDiagram().getChildrenIterator();
		while (it.hasNext()) {
			res.add(it.next());
		}
		return res;
	}

	public void activate() {
		if (!isActive()) {
			super.activate();
			((ModelElement) getModel()).addPropertyChangeListener(this);
			if (!getOrmDiagram().isLoadSuccessfull()) {
				autolayout();
				getOrmDiagram().setDirty(false);
			}
			// restore();
		}
	}

	public void autolayout() {
		IDiagramInfo process = new DiagramInfo(getOrmDiagram());
		AutoLayout layout = new AutoLayout();
		layout.setGridStep("" + 5); //$NON-NLS-1$
		layout.setOverride(true);
		layout.setProcess(process);
	}

	public void setToFront(EditPart ep) {
		int index = getChildren().indexOf(ep);
		if (index == -1) {
			return;
		}
		if (index != getChildren().size() - 1) {
			reorderChild(ep, getChildren().size() - 1);
		}
	}

	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((ModelElement) getModel()).removePropertyChangeListener(this);
		}
	}

	class DiagramInfo implements IDiagramInfo {

		List<IItemInfo> items = new ArrayList<IItemInfo>();
		OrmDiagram diagram;

		public DiagramInfo(OrmDiagram diagram) {
			IItemInfo item;
			this.diagram = diagram;
			OrmShapeEditPart part;

			Iterator<Shape> it = diagram.getChildrenIterator();
			while (it.hasNext()) {
				Shape child = it.next();
				part = (OrmShapeEditPart) getViewer().getEditPartRegistry().get(child);
				if (part != null && part.getFigure().isVisible()) {
					item = new DiagramElementInfo((OrmShape)child);
					addItem(item);
				}
			}
		}

		/**
		 * 
		 */
		public IItemInfo[] getItems() {
			return items.toArray(new IItemInfo[0]);
		}

		/**
		 * 
		 * @param item
		 */
		public void addItem(IItemInfo item) {
			items.add(item);
		}
	}

	class DiagramElementInfo implements IItemInfo {
		OrmShape element;

		List<ILinkInfo> links = new ArrayList<ILinkInfo>();

		/**
		 * 
		 * @param element
		 */
		public DiagramElementInfo(OrmShape element) {
			ILinkInfo link;
			this.element = element;
			for (Connection connection : element.getSourceConnections()) {
				link = new LinkInfo(connection);
				addLink(link);
			}
			Iterator<Shape> it = element.getChildrenIterator();
			while (it.hasNext()) {
				Shape child = it.next();
				if (child.getSourceConnections().size() == 0) {
					link = new LinkInfo(getID());
					addLink(link);
				}
				for (int i = 0; i < child.getSourceConnections().size(); i++) {
					link = new LinkInfo(child.getSourceConnections().get(i));
					addLink(link);
				}
			}
		}

		/**
		 * 
		 */
		public String getID() {
			return element.toString();
		}

		/**
		 * 
		 */
		public boolean isComment() {
			return false;
		}

		/**
		 * 
		 */
		public int[] getShape() {
			int[] shape = new int[4];
			shape[0] = element.getLocation().x;
			shape[1] = element.getLocation().y;
			OrmShapeEditPart part = (OrmShapeEditPart) getViewer()
					.getEditPartRegistry().get(element);
			if (part != null) {
				IFigure fig = part.getFigure();
				shape[2] = fig.getSize().width;//fig.getPreferredSize().width;
				shape[3] = fig.getSize().height;//fig.getPreferredSize().height;
			} else {
				shape[2] = 6000;
				shape[3] = 1000;
			}
			return shape;
		}

		/**
		 * 
		 */
		public ILinkInfo[] getLinks() {
			return links.toArray(new ILinkInfo[0]);
		}

		/**
		 * 
		 * @param link
		 */
		public void addLink(ILinkInfo link) {
			links.add(link);
		}

		/**
		 * 
		 */
		public void setShape(int[] s) {
			element.setLocation(new Point(s[0], s[1]));
		}

	}

	class LinkInfo implements ILinkInfo {
		Connection link = null;

		String id = null;

		/**
		 * 
		 * @param link
		 */
		public LinkInfo(Connection link) {
			this.link = link;
		}

		/**
		 * 
		 * @param id
		 */
		public LinkInfo(String id) {
			this.id = id;
		}

		public String getTargetID() {
			if (id != null) {
				return id;
			}
			if (link.getTarget() != null) {
				return link.getTarget().toString();
			}
			return ""; //$NON-NLS-1$
		}

		/**
		 * 
		 */
		public void setLinkShape(int[] vs) {
		}
	}


	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == SnapToHelper.class) {
			List<SnapToHelper> snapStrategies = new ArrayList<SnapToHelper>();
			Boolean val = (Boolean)getViewer().getProperty(RulerProvider.PROPERTY_RULER_VISIBILITY);
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGuides(this));
			}
			val = (Boolean)getViewer().getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED);
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGeometry(this));
			}
			val = (Boolean)getViewer().getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGrid(this));
			}
			if (snapStrategies.size() == 0) {
				return null;
			}
			if (snapStrategies.size() == 1) {
				return snapStrategies.get(0);
			}
			SnapToHelper ss[] = new SnapToHelper[snapStrategies.size()];
			for (int i = 0; i < snapStrategies.size(); i++) {
				ss[i] = (SnapToHelper)snapStrategies.get(i);
			}
			return new CompoundSnapToHelper(ss);
		}
		return super.getAdapter(adapter);
	}
}
