/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
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
import java.util.ArrayList;
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
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.rulers.RulerProvider;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.IItemInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.ILinkInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.IDiagramInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.impl.AutoLayoutImpl;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Connection;
import org.jboss.tools.hibernate.ui.diagram.editors.model.BaseElement;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;

/**
 * @author some modifications from Vitali
 */
class DiagramEditPart extends OrmEditPart {

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

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (OrmDiagram.AUTOLAYOUT.equals(prop)) {
			refresh();
			autolayout();
		} else if (BaseElement.REFRESH.equals(prop)) {
			refresh();
		} else if (OrmDiagram.DIRTY.equals(prop)) {
			((DiagramViewer) ((DefaultEditDomain) getViewer().getEditDomain())
					.getEditorPart()).refreshDirty();
		}
		refresh();
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
			((BaseElement) getModel()).addPropertyChangeListener(this);
			if (!getOrmDiagram().isFileLoadSuccessfull()) {
				refresh();
				autolayout();
				refresh();
				getOrmDiagram().setDirty(false);
			}
		}
	}

	public void autolayout() {
		IDiagramInfo process = new DiagramInfo(getOrmDiagram());
		AutoLayoutImpl layout = new AutoLayoutImpl();
		layout.setGridStep(5);
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
			((BaseElement) getModel()).removePropertyChangeListener(this);
		}
	}

	class DiagramInfo implements IDiagramInfo {

		List<IItemInfo> items = new ArrayList<IItemInfo>();
		OrmDiagram diagram;

		public DiagramInfo(OrmDiagram diagram) {
			this.diagram = diagram;
			Iterator<Shape> it = diagram.getChildrenIterator();
			while (it.hasNext()) {
				Shape child = it.next();
				if (child.isVisible() && (child instanceof OrmShape)) {
					IItemInfo item = new DiagramElementInfo((OrmShape)child);
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
				if (connection.isVisible()) {
					link = new LinkInfo(connection);
					addLink(link);
				}
			}
			Iterator<Shape> it = element.getChildrenIterator();
			while (it.hasNext()) {
				Shape child = it.next();
				if (!child.isVisible()) {
					continue;
				}
				final List<Connection> sourceConnections = child.getSourceConnections(); 
				if (sourceConnections.size() == 0) {
					link = new LinkInfo(getID());
					addLink(link);
				}
				for (Connection connection : sourceConnections) {
					if (connection.isVisible()) {
						link = new LinkInfo(connection);
						addLink(link);
					}
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
		 * gets shape vertices
		 */
		public int[] getShape() {
			int[] shape = new int[4];
			shape[0] = element.getLocation().x;
			shape[1] = element.getLocation().y;
			OrmShapeEditPart part = (OrmShapeEditPart) getViewer()
					.getEditPartRegistry().get(element);
			if (part != null) {
				IFigure fig = part.getFigure();
				//shape[2] = fig.getSize().width;
				//shape[3] = fig.getSize().height;
				// use preferred size for correct autolayout
				shape[2] = fig.getPreferredSize().width;
				shape[3] = fig.getPreferredSize().height;
			} else {
				// indicate refresh model error
				shape[2] = 6000;
				shape[3] = 1000;
			}
			return shape;
		}

		/**
		 * setup OrmShape up-left point location, using s[0] and s[1]
		 */
		public void setShape(int[] s) {
			element.setLocation(new Point(s[0], s[1]));
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
