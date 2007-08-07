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
package org.jboss.tools.hibernate.ui.veditor.editors.parts;

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
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.ui.veditor.editors.VisualEditor;
import org.jboss.tools.hibernate.ui.veditor.editors.autolayout.AutoLayout;
import org.jboss.tools.hibernate.ui.veditor.editors.autolayout.IItemInfo;
import org.jboss.tools.hibernate.ui.veditor.editors.autolayout.ILinkInfo;
import org.jboss.tools.hibernate.ui.veditor.editors.autolayout.IDiagramInfo;
import org.jboss.tools.hibernate.ui.veditor.editors.command.ShapeSetConstraintCommand;
import org.jboss.tools.hibernate.ui.veditor.editors.model.Connection;
import org.jboss.tools.hibernate.ui.veditor.editors.model.ModelElement;
import org.jboss.tools.hibernate.ui.veditor.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.veditor.editors.model.OrmShape;
import org.jboss.tools.hibernate.ui.veditor.editors.model.Shape;
import org.jboss.tools.hibernate.ui.veditor.editors.model.SpecialOrmShape;
import org.jboss.tools.hibernate.ui.veditor.editors.model.SpecialRootClass;
import org.jboss.tools.hibernate.ui.view.views.HibernateUtils;

class DiagramEditPart extends OrmEditPart implements PropertyChangeListener {

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new ShapesXYLayoutEditPolicy());
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
		} else if (OrmDiagram.DIRTY.equals(prop))
			((VisualEditor) ((DefaultEditDomain) getViewer().getEditDomain())
					.getEditorPart()).refreshDirty();
	}

	public void restore() {
		boolean dirty = getCastedModel().isDirty();
		HashMap hashMap = getCastedModel().getCloneElements();
		String childrenLocations[] = getCastedModel().getChildrenLocations();
		int tempPoint = 1;
		OrmShape ormShape;
		int point = 1;
		int pointX = calculateTableLocation();
		String string, xy[];
		for (int i = 0; i < childrenLocations.length; i++)
			if (childrenLocations[i].indexOf('@') != -1
					&& childrenLocations[i].indexOf(';') != -1) {
				string = childrenLocations[i].substring(0, childrenLocations[i]
						.indexOf('@'));
				ormShape = (OrmShape) hashMap.remove(string);
				if (ormShape != null) {
					string = childrenLocations[i]
							.substring(childrenLocations[i].indexOf('@') + 1);
					xy = string.split(";");
					if (xy.length > 1)
						try {
							ormShape.setLocation(new Point(Integer
									.parseInt(xy[0]), Integer.parseInt(xy[1])));
						} catch (NumberFormatException e) {
						}
					if (xy.length > 2)
						if ((new Boolean(xy[2])).booleanValue())
							ormShape.refreshHiden();
					tempPoint = ormShape.getLocation().y
							+ getChildrenFigurePreferredHeight(ormShape) + 20;
					if (tempPoint > point)
						point = tempPoint;
				}
			}
		if (getCastedModel().getOrmElement() instanceof RootClass) {
			RootClass persistentClass = (RootClass) getCastedModel()
					.getOrmElement();
			ormShape = (OrmShape) hashMap.remove(persistentClass
					.getEntityName());
			if (ormShape != null) {
				ormShape.setLocation(new Point(20, 20));
				tempPoint = 40 + getChildrenFigurePreferredHeight(ormShape);
			}
			Table table = persistentClass.getTable();
			ormShape = (OrmShape) hashMap.remove(HibernateUtils.getTableName(table));
			if (ormShape != null) {
				ormShape.setLocation(new Point(pointX, 20));
				point = 40 + getChildrenFigurePreferredHeight(ormShape);
			}
			if (tempPoint > point)
				point = tempPoint;

		}
		Object objects[] = hashMap.keySet().toArray();
		for (int i = 0; i < objects.length; i++) {
			ormShape = (OrmShape) hashMap.get(objects[i]);
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
				ormShape = (OrmShape) hashMap.remove(HibernateUtils.getTableName(ownerTable));
				// }
				// if (ormShape != null ) {
				// ormShape.setLocation(new Point(pointX,point));
				// point = point + getChildrenFigurePreferredHeight(ormShape) +
				// 20;
				// }
				if (tempPoint > point)
					point = tempPoint;
			}
		}
		Iterator iterator = hashMap.values().iterator();
		while (iterator.hasNext()) {
			ormShape = (OrmShape) iterator.next();
			if (ormShape.getOrmElement() instanceof Table) {
				ormShape.setLocation(new Point(pointX, point));
				point = point + getChildrenFigurePreferredHeight(ormShape) + 20;
			}
		}
		getCastedModel().setDirty(dirty);
	}

	private OrmDiagram getCastedModel() {
		return (OrmDiagram) getModel();
	}

	private int getChildrenFigurePreferredHeight(OrmShape ormShape) {
		GraphicalEditPart part;
		for (int i = 0; i < getChildren().size(); i++) {
			part = (GraphicalEditPart) getChildren().get(i);
			if (ormShape.equals(part.getModel()))
				return part.getFigure().getPreferredSize().height;
		}
		return 0;
	}

	private int calculateTableLocation() {
		int j = 0;
		IFigure figure;
		for (int i = 0; i < getFigure().getChildren().size(); i++) {
			figure = (IFigure) getFigure().getChildren().get(i);
			if (figure.getPreferredSize().width > j)
				j = figure.getPreferredSize().width;
		}
		return j + 120;
	}

	protected List getModelChildren() {
		return getCastedModel().getChildren();
	}

	public void activate() {
		if (!isActive()) {
			super.activate();
			((ModelElement) getModel()).addPropertyChangeListener(this);
			if(!getCastedModel().isLoadSuccessfull()){
				autolayout();
				getCastedModel().setDirty(false);
			}
			// restore();
		}
	}

	public void autolayout() {
		IDiagramInfo process = new DiagramInfo(getCastedModel());
		AutoLayout layout = new AutoLayout();
		layout.setGridStep("" + 5);
		layout.setOverride(true);
		layout.setProcess(process);
	}

	public void setToFront(EditPart ep) {
		int index = getChildren().indexOf(ep);
		if (index == -1)
			return;
		if (index != getChildren().size() - 1)
			reorderChild(ep, getChildren().size() - 1);
	}

	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((ModelElement) getModel()).removePropertyChangeListener(this);
		}
	}

	private static class ShapesXYLayoutEditPolicy extends XYLayoutEditPolicy {

		protected Command createChangeConstraintCommand(
				ChangeBoundsRequest request, EditPart child, Object constraint) {
			if (child instanceof OrmShapeEditPart
					&& constraint instanceof Rectangle) {
				return new ShapeSetConstraintCommand((OrmShape) child
						.getModel(), request, ((Rectangle) constraint)
						.getLocation());
			}
			return super.createChangeConstraintCommand(request, child,
					constraint);
		}

		protected Command createAddCommand(EditPart child, Object constraint) {
			return null;
		}

		protected Command createChangeConstraintCommand(EditPart child,
				Object constraint) {
			return null;
		}

		protected Command getCreateCommand(CreateRequest request) {
			return null;
		}

		protected Command getDeleteDependantCommand(Request request) {
			return null;
		}

		protected EditPolicy createChildEditPolicy(EditPart child) {
			return new NonResizableEditPolicy();
		}
	}

	class DiagramInfo implements IDiagramInfo {

		ArrayList items = new ArrayList();
		OrmDiagram diagram;

		public DiagramInfo(OrmDiagram diagram) {
			IItemInfo item;
			this.diagram = diagram;
			OrmShapeEditPart part;

			for (int i = 0; i < diagram.getChildren().size(); i++) {
				part = (OrmShapeEditPart) getViewer().getEditPartRegistry()
						.get(diagram.getChildren().get(i));
				if (part != null && part.getFigure().isVisible()) {
					item = new DiagramElementInfo((OrmShape) diagram
							.getChildren().get(i));
					addItem(item);
				}
			}
		}

		/**
		 * 
		 */
		public IItemInfo[] getItems() {
			return (IItemInfo[]) items.toArray(new IItemInfo[0]);
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

		ArrayList links = new ArrayList();

		/**
		 * 
		 * @param element
		 */
		public DiagramElementInfo(OrmShape element) {
			ILinkInfo link;
			this.element = element;
			for (int i = 0; i < element.getSourceConnections().size(); i++) {
				link = new LinkInfo((Connection) element.getSourceConnections()
						.get(i));
				addLink(link);
			}
			Shape child;
			for (int j = 0; j < element.getChildren().size(); j++) {
				child = (Shape) element.getChildren().get(j);
				if (child.getSourceConnections().size() == 0) {
					link = new LinkInfo(getID());
					addLink(link);
				}
				for (int i = 0; i < child.getSourceConnections().size(); i++) {
					link = new LinkInfo((Connection) child
							.getSourceConnections().get(i));
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
				shape[2] = fig.getPreferredSize().width;
				shape[3] = fig.getPreferredSize().height;
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
			return (ILinkInfo[]) links.toArray(new ILinkInfo[0]);
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

		/**
		 * 
		 */
		public String getTargetID() {
			if (id != null)
				return id;
			if (link.getTarget() != null)
				return link.getTarget().toString();
			else
				return "";
		}

		/**
		 * 
		 */
		public void setLinkShape(int[] vs) {
		}
	}
}
