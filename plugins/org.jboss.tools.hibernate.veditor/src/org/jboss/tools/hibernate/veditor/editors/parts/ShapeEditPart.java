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
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping;
import org.jboss.tools.hibernate.veditor.editors.figures.TitleFigure;
import org.jboss.tools.hibernate.veditor.editors.figures.TopLineBorder;
import org.jboss.tools.hibernate.veditor.editors.model.Connection;
import org.jboss.tools.hibernate.veditor.editors.model.ModelElement;
import org.jboss.tools.hibernate.veditor.editors.model.Shape;
import org.jboss.tools.hibernate.view.views.OrmLabelProvider;
import org.jboss.tools.hibernate.view.views.OrmModelImageVisitor;
import org.jboss.tools.hibernate.view.views.OrmModelNameVisitor;


public class ShapeEditPart extends
		AbstractGraphicalEditPart implements PropertyChangeListener,  NodeEditPart {

	static protected OrmLabelProvider ormLabelProvider = 
		new OrmLabelProvider(new OrmModelImageVisitor(), new OrmModelNameVisitor(null));
	
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,  new ShapesSelectionEditPolicy());
	}

	
	protected IFigure createFigure() {
		if (getModel() instanceof Shape) {
			Label label = new Label();
			label.setText(ormLabelProvider.getText(getCastedModel().getOrmElement()));	
			label.setBackgroundColor(getColor());
			label.setIcon(ormLabelProvider.getImage(getCastedModel().getOrmElement()));
			label.setLabelAlignment(PositionConstants.LEFT);
			label.setOpaque(true);
			TopLineBorder border = new TopLineBorder(1,2+getCastedModel().getIndent(),1,2);
			border.setColor(getOrmShapeEditPart().getColor());
			label.setBorder(border);
			return label;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (Shape.SHOW_SELECTION.equals(prop)) {
			getFigure().setBackgroundColor(getSelectionColor());	
			getFigure().setForegroundColor(ResourceManager.getInstance().getColor(new RGB(255,255,255)));			
		} else if (Shape.HIDE_SELECTION.equals(prop)) {
			getFigure().setBackgroundColor(getColor());		
			getFigure().setForegroundColor(ResourceManager.getInstance().getColor(new RGB(0,0,0)));			
		}	else if (Shape.SET_FOCUS.equals(prop)) {
			getViewer().select(this);
			getViewer().reveal(this);
		} 
	}

	public void performRequest(Request req) {
		if(RequestConstants.REQ_OPEN.equals(req.getType())) {
			if (getCastedModel().getOrmElement() instanceof IDatabaseColumn) {
				if(getCastedModel().getTargetConnections().size() > 0)
					((Connection)getCastedModel().getTargetConnections().get(0)).getSource().setFocus();
			} else {
				if(getCastedModel().getSourceConnections().size() > 0)
					((Connection)getCastedModel().getSourceConnections().get(0)).getTarget().setFocus();
			}
		}
	}

	public void activate() {
		if (!isActive()) {
			super.activate();
			((ModelElement) getModel()).addPropertyChangeListener(this);
		}
	}
	
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((ModelElement) getModel()).removePropertyChangeListener(this);
		}
	}
	
	protected Shape getCastedModel() {
		return (Shape) getModel();
	}

	protected List getModelSourceConnections() {
		return getCastedModel().getSourceConnections();
	}
	
	protected List getModelTargetConnections() {
		return getCastedModel().getTargetConnections();
	}

	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return getConnectionAnchor();
	}
	
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return getConnectionAnchor();
	}
	
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}
	
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}
	
	protected ConnectionAnchor getConnectionAnchor() {
		ChopboxAnchor anchor = new ChopboxAnchor(getFigure()){
			public Point getLocation(Point reference) {
				Rectangle r = getOwner().getBounds().getCopy();
				getOwner().translateToAbsolute(r);
				if (getOwner() instanceof TitleFigure) {
					r = ((IFigure)getOwner().getChildren().get(0)).getBounds().getCopy();
					((IFigure)getOwner().getChildren().get(0)).translateToAbsolute(r);
				}
				OrmShapeEditPart part = getOrmShapeEditPart();
				Point p = r.getCenter();
				if (reference.x < p.x) 
					p.x-=part.getFigure().getBounds().width/2;
				 else 
					p.x+=part.getFigure().getBounds().width/2;					
				return p;
			}
		};
		return anchor;
	}
	
	private OrmShapeEditPart getOrmShapeEditPart() {
		int i = 0;
		EditPart part = this;
		while (!((part instanceof OrmShapeEditPart))) {
			part = part.getParent();
			if(i++ > 4)
				throw new RuntimeException();
		}
		return (OrmShapeEditPart)part;
	}

	protected Color getColor() {
		if (getCastedModel().getOrmElement() instanceof IPersistentField || getCastedModel().getOrmElement() instanceof IHibernateValueMapping) 
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(Messages.Colors_PersistentFieldR),
					Integer.parseInt(Messages.Colors_PersistentFieldG),
					Integer.parseInt(Messages.Colors_PersistentFieldB)));
		else if (getCastedModel().getOrmElement() instanceof IDatabaseColumn) 
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(Messages.Colors_DatabaseColumnR),
					Integer.parseInt(Messages.Colors_DatabaseColumnG),
					Integer.parseInt(Messages.Colors_DatabaseColumnB)));
		else if (getCastedModel().getOrmElement() instanceof IPersistentClass) 
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(Messages.Colors_PersistentClassR),
					Integer.parseInt(Messages.Colors_PersistentClassG),
					Integer.parseInt(Messages.Colors_PersistentClassB)));
		else if (getCastedModel().getOrmElement() instanceof IDatabaseTable) 
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(Messages.Colors_DatabaseTableR),
					Integer.parseInt(Messages.Colors_DatabaseTableG),
					Integer.parseInt(Messages.Colors_DatabaseTableB)));
		else
			throw new IllegalArgumentException();
	}

	protected Color getSelectionColor() {
		if (getCastedModel().getOrmElement() instanceof IPersistentClass || getCastedModel().getOrmElement() instanceof IPersistentField || getCastedModel().getOrmElement() instanceof IHibernateValueMapping) 
			return ResourceManager.getInstance().getColor(new RGB(112,161,99));
		else if (getCastedModel().getOrmElement() instanceof IDatabaseTable || getCastedModel().getOrmElement() instanceof IDatabaseColumn) 
			return ResourceManager.getInstance().getColor(new RGB(66,173,247));
		else
			throw new IllegalArgumentException();
	}
	
	private class ShapesSelectionEditPolicy extends SelectionEditPolicy {

		protected void hideSelection() {
			getCastedModel().hideSelection();
			Iterator iter  = getCastedModel().getSourceConnections().iterator();	
			while (iter.hasNext()) {
				Connection element = (Connection) iter.next();
				element.hideSelection();				
			}
			iter = getCastedModel().getTargetConnections().iterator();	
			while (iter.hasNext()) {
				Connection element = (Connection) iter.next();
				element.hideSelection();				
			}
		}

		protected void showSelection() {
			getCastedModel().showSelection();
			Iterator iter  = getCastedModel().getSourceConnections().iterator();	
			while (iter.hasNext()) {
				Connection element = (Connection) iter.next();
				element.showSelection();				
			}
			iter = getCastedModel().getTargetConnections().iterator();	
			while (iter.hasNext()) {
				Connection element = (Connection) iter.next();
				element.showSelection();				
			}
		}
		
	}

}
