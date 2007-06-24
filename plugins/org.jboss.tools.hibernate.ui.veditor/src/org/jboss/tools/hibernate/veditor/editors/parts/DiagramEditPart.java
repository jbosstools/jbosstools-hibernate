/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.veditor.editors.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.veditor.editors.VizualEditor;
import org.jboss.tools.hibernate.veditor.editors.command.ShapeSetConstraintCommand;
import org.jboss.tools.hibernate.veditor.editors.model.ModelElement;
import org.jboss.tools.hibernate.veditor.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.veditor.editors.model.OrmShape;
import org.jboss.tools.hibernate.veditor.editors.model.SpecialOrmShape;
import org.jboss.tools.hibernate.veditor.editors.model.SpecialRootClass;


/**
 * @author Konstantin Mishin
 *
 */
class DiagramEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener{
	
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE,  new ShapesXYLayoutEditPolicy());
	}
	
	protected IFigure createFigure() {
		Figure f = new FreeformLayer();
		f.setBorder(new MarginBorder(3));
		f.setLayoutManager(new FreeformLayout());
		
		ConnectionLayer connLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
		connLayer.setConnectionRouter(new ManhattanConnectionRouter());
		
		return f;
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (OrmDiagram.REFRESH.equals(prop)) {
			refresh();
			restore();
		} else if (OrmDiagram.DIRTY.equals(prop))
		((VizualEditor)((DefaultEditDomain)getViewer().getEditDomain()).getEditorPart()).refreshDirty();
	}
	
	public void restore() {
		boolean dirty = getCastedModel().isDirty();
		HashMap hashMap = getCastedModel().getCloneElements();
		String childrenLocations[] =  getCastedModel().getChildrenLocations();
		int tempPoint = 1;
		OrmShape ormShape;
		int point = 1;
		int pointX = calculateTableLocation();
		String string, xy[];
		for (int i = 0; i < childrenLocations.length; i++)
			if (childrenLocations[i].indexOf('@') != -1 && childrenLocations[i].indexOf(';') != -1){
				string = childrenLocations[i].substring(0,childrenLocations[i].indexOf('@'));
				ormShape = (OrmShape)hashMap.remove(string);
				if (ormShape != null) {
					string = childrenLocations[i].substring(childrenLocations[i].indexOf('@')+1);
					xy = string.split(";");
					if(xy.length>1)
						try {
							ormShape.setLocation(new Point(Integer.parseInt(xy[0]),Integer.parseInt(xy[1])));							
						} catch (NumberFormatException  e) {}
					if(xy.length>2)
						if((new Boolean(xy[2])).booleanValue())
							ormShape.refreshHiden();
					tempPoint = ormShape.getLocation().y + getChildrenFigurePreferredHeight(ormShape) + 20;
					if(tempPoint > point)
						point = tempPoint;
				}
			}
		if (getCastedModel().getOrmElement() instanceof RootClass) {
			RootClass persistentClass = (RootClass)getCastedModel().getOrmElement();
			ormShape = (OrmShape)hashMap.remove(persistentClass.getClassName());
			if (ormShape != null) {
				ormShape.setLocation(new Point(20,20));	
				tempPoint = 40 + getChildrenFigurePreferredHeight(ormShape);				
			}
			Table table = persistentClass.getTable();
			ormShape =  (OrmShape)hashMap.remove(table.getSchema() + "." + table.getName());
			if (ormShape != null) {
				ormShape.setLocation(new Point(pointX,20));
				point = 40 + getChildrenFigurePreferredHeight(ormShape);
			}
			if(tempPoint > point)
				point = tempPoint;

		}
		Object objects[] = hashMap.keySet().toArray();
		for (int i = 0; i < objects.length; i++) {
			ormShape = (OrmShape)hashMap.get(objects[i]);
			if (ormShape != null && (ormShape.getOrmElement() instanceof RootClass || ormShape.getOrmElement() instanceof SpecialOrmShape)) {	
				ormShape.setLocation(new Point(20,point));
				tempPoint = point + getChildrenFigurePreferredHeight(ormShape) + 20;
				if (ormShape.getOrmElement() instanceof SpecialRootClass) {
					Component component = (Component)((Collection)((SpecialRootClass)(ormShape.getOrmElement())).getProperty().getValue()).getElement();
					Table ownerTable = component.getOwner().getTable();
					ormShape = (OrmShape)hashMap.remove(ownerTable.getSchema() + "." + ownerTable.getName());
				}
				if (ormShape != null ) {
					ormShape.setLocation(new Point(pointX,point));
					point = point + getChildrenFigurePreferredHeight(ormShape) + 20;
				}
				if(tempPoint > point)
					point = tempPoint;
			}
		}
		Iterator iterator = hashMap.values().iterator();
		while (iterator.hasNext()) {
			ormShape = (OrmShape) iterator.next();
			if (ormShape.getOrmElement() instanceof Table)	{
				ormShape.setLocation(new Point(pointX,point));				
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
			part = (GraphicalEditPart)getChildren().get(i);
			if (ormShape.equals(part.getModel()))
				return part.getFigure().getPreferredSize().height;
		}
		return 0;
	}

	private int calculateTableLocation() {
		int j = 0;
		IFigure figure;
		for (int i = 0; i < getFigure().getChildren().size(); i++) {
			figure = (IFigure)getFigure().getChildren().get(i);
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
			restore();
		}
	}
	
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((ModelElement) getModel()).removePropertyChangeListener(this);
		}
	}
	
	private static class ShapesXYLayoutEditPolicy extends XYLayoutEditPolicy {
		
		protected Command createChangeConstraintCommand(ChangeBoundsRequest request,
				EditPart child, Object constraint) {
			if (child instanceof OrmShapeEditPart && constraint instanceof Rectangle) {
				return new ShapeSetConstraintCommand(
						(OrmShape) child.getModel(), request, ((Rectangle) constraint).getLocation());
			}
			return super.createChangeConstraintCommand(request, child, constraint);
		}
		
		protected Command createAddCommand(EditPart child, Object constraint) {
			return null;
		}
		
		protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
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
}