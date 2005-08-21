package org.hibernate.eclipse.graph.parts;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.hibernate.eclipse.graph.anchor.LeftOrRightParentAnchor;
import org.hibernate.eclipse.graph.figures.EditableLabel;
import org.hibernate.eclipse.graph.model.PersistentClassViewAdapter;
import org.hibernate.eclipse.graph.model.PropertyViewAdapter;
import org.hibernate.mapping.Property;

public class PropertyEditPart extends AbstractGraphicalEditPart implements NodeEditPart, Observer {

	public PropertyEditPart(PropertyViewAdapter property) {
		setModel(property);
	}

	public void activate() {
		((Observable)getModel()).addObserver(this);
	}
	
	public void deactivate() {
		((Observable)getModel()).deleteObserver(this);
	}
	
	protected IFigure createFigure() {
		Property property = ((PropertyViewAdapter) getModel()).getProperty();
		String label = property.getName();
		Label propertyLabel = new EditableLabel(label);
		propertyLabel.setIcon(((PropertyViewAdapter)getModel()).getImage());
		return propertyLabel;
	}

	protected void createEditPolicies() {
	}
	
	public List getModelSourceConnections() {
		List sc = ((PropertyViewAdapter) getModel()).getSourceConnections();
		return sc;
	}
	
	public List getModelTargetConnections() {
		List tc = ((PropertyViewAdapter) getModel()).getTargetConnections();
		return tc;
	}

	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new LeftOrRightParentAnchor(getFigure());
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new LeftOrRightParentAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new LeftOrRightParentAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new LeftOrRightParentAnchor(getFigure());
	}

	public void update(Observable o, Object arg) {
	 if(arg==PersistentClassViewAdapter.ASSOCIATONS) {
		 refreshSourceConnections();
		 refreshTargetConnections();
	 }
		
	}

}
