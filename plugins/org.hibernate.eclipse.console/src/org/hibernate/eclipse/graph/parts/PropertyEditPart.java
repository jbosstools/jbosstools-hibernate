package org.hibernate.eclipse.graph.parts;

import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.hibernate.eclipse.graph.anchor.LeftOrRightParentAnchor;
import org.hibernate.eclipse.graph.figures.EditableLabel;
import org.hibernate.eclipse.graph.model.PropertyViewAdapter;
import org.hibernate.mapping.Property;

public class PropertyEditPart extends AbstractGraphicalEditPart implements NodeEditPart {

	public PropertyEditPart(PropertyViewAdapter property) {
		setModel(property);
	}

	protected IFigure createFigure() {
		Property property = ((PropertyViewAdapter) getModel()).getProperty();
		String label = property.getName();
		Label propertyLabel = new EditableLabel(label);
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

}
