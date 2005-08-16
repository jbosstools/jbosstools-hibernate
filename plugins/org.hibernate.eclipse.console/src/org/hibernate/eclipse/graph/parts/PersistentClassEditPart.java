package org.hibernate.eclipse.graph.parts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.hibernate.eclipse.graph.anchor.TopOrBottomAnchor;
import org.hibernate.eclipse.graph.figures.EditableLabel;
import org.hibernate.eclipse.graph.figures.PersistentClassFigure;
import org.hibernate.eclipse.graph.model.PersistentClassViewAdapter;
import org.hibernate.eclipse.graph.model.PropertyViewAdapter;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

public class PersistentClassEditPart extends AbstractGraphicalEditPart implements Observer, NodeEditPart {

	public PersistentClassEditPart(PersistentClassViewAdapter model) {		
		setModel( model );				
	}

	public void activate() {
		super.activate();
		Observable o = (Observable) getModel();
		o.addObserver(this);
	}
	
	public void deactivate() {
		super.deactivate();
		Observable o = (Observable) getModel();
		o.deleteObserver(this);
	}

	protected void createEditPolicies() {
	}

	public void refreshVisuals() {
		PersistentClassFigure myFigure = (PersistentClassFigure) getFigure();
		PersistentClass node = getPersistentClass();
		ConfigurationEditPart parent = (ConfigurationEditPart) getParent();
		myFigure.getLabel().setText( node.getEntityName() );
		Rectangle bounds = getPersistentClassViewAdapter().getBounds().getCopy();
		parent.setLayoutConstraint( this, myFigure, bounds );		
	}

	protected IFigure createFigure() {
		return new PersistentClassFigure(new EditableLabel(getPersistentClass().getEntityName()));
	}

	protected List getModelChildren() {	
		Iterator propertyIterator = getPersistentClass().getPropertyIterator();
		List list = new ArrayList();
		while ( propertyIterator.hasNext() ) {
			list.add( new PropertyViewAdapter(getPersistentClassViewAdapter(), (Property) propertyIterator.next()) );
		}
		return list;
	}

	private PersistentClass getPersistentClass() {
		return getPersistentClassViewAdapter().getPersistentClass();
	}

	public PersistentClassViewAdapter getPersistentClassViewAdapter() {
		return (PersistentClassViewAdapter)getModel();
	}

	public IFigure getContentPane() {
		PersistentClassFigure figure = (PersistentClassFigure) getFigure();
		return figure.getPropertiesFigure();
	}

	public void update(Observable o, Object arg) {
		refreshVisuals();
	}
	
	protected List getModelSourceConnections() {
		System.out.println("Getting source of " + this);
		return getPersistentClassViewAdapter().getSourceAssociations();		
	}
	
	protected List getModelTargetConnections() {
		System.out.println("Getting target of " + this);
		return getPersistentClassViewAdapter().getTargetAssociations();
	}

	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new TopOrBottomAnchor(getFigure());
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new TopOrBottomAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new TopOrBottomAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new TopOrBottomAnchor(getFigure());
	}

	
	
	
	
}