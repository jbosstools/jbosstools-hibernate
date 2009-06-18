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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.ui.veditor.editors.figures.RoundPolylineConnection;
import org.jboss.tools.hibernate.ui.veditor.editors.model.Connection;
import org.jboss.tools.hibernate.ui.veditor.editors.model.ModelElement;



class ConnectionEditPart extends AbstractConnectionEditPart 
implements PropertyChangeListener, EditPartListener {
	
	public void activate() {
		if (!isActive()) {
			super.activate();
			((ModelElement) getModel()).addPropertyChangeListener(this);
			addEditPartListener(this);
		}
	}
	
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,  new ShapesSelectionEditPolicy());
	}
	
	protected IFigure createFigure() {
		PolylineConnection connection = new RoundPolylineConnection();		
		connection.setForegroundColor(getColor());	
		connection.setTargetDecoration(new PolygonDecoration());		
		connection.setVisible(!getCastedModel().isHiden());
		return connection;
	}
	
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((ModelElement) getModel()).removePropertyChangeListener(this);
		}
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getPropertyName();
		if (Connection.SHOW_SELECTION.equals(property)) {
			getFigure().setForegroundColor(getSelectionColor());
		} else if (Connection.HIDE_SELECTION.equals(property)) {
			getFigure().setForegroundColor(getColor());			
		} else if (Connection.SET_HIDEN.equals(property)) {
			getFigure().setVisible(!((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	private Connection getCastedModel() {
		return (Connection) getModel();
	}

	private Color getColor() {
		Object element = getCastedModel().getTarget().getOrmElement();
		if (element instanceof RootClass || element instanceof Subclass) { 
			return ResourceManager.getInstance().getColor(new RGB(210,155,100));
		} else if (element instanceof Column || element instanceof Table || element instanceof Property) { 
			return ResourceManager.getInstance().getColor(new RGB(160, 160, 160));
		} else {
			throw new IllegalArgumentException();
		}
	}

	private Color getSelectionColor() {
		if (getCastedModel().getTarget().getOrmElement() instanceof RootClass ||
				getCastedModel().getTarget().getOrmElement() instanceof Subclass) { 
			return ResourceManager.getInstance().getColor(new RGB(112,161,99));
		} else if (getCastedModel().getTarget().getOrmElement() instanceof Column || 
				getCastedModel().getTarget().getOrmElement() instanceof Table || 
				getCastedModel().getTarget().getOrmElement() instanceof Component) { 
			return ResourceManager.getInstance().getColor(new RGB(66,173,247));
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	private class ShapesSelectionEditPolicy extends SelectionEditPolicy {

		protected void hideSelection() {
			getCastedModel().hideSelection();
		}

		protected void showSelection() {
			getCastedModel().showSelection();
		}
		
	}

	public void childAdded(EditPart child, int index) {
		
	}

	public void partActivated(EditPart editpart) {
		
	}

	public void partDeactivated(EditPart editpart) {
		
	}

	public void removingChild(EditPart child, int index) {
		
	}

	public void selectedStateChanged(EditPart editpart) {
		if (this.getSelected() == EditPart.SELECTED_PRIMARY) {
			((GEFRootEditPart) getParent()).setToFront(this);
		}
	}
}