package org.hibernate.eclipse.graph.parts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.draw2d.ChopboxAnchor;
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
import org.hibernate.util.StringHelper;

public class PersistentClassEditPart extends AbstractGraphicalEditPart implements Observer, NodeEditPart {

	public PersistentClassEditPart(PersistentClassViewAdapter model) {		
		setModel( model );				
	}

	public void activate() {
		super.activate();
		Observable o = (Observable) getModel();
		o.addObserver(this);
		refreshSourceConnections(); // to be in sync with the models connections
		refreshTargetConnections(); // cannot just call refresh as that makes the connections available twice
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
		ConfigurationEditPart parent = (ConfigurationEditPart) getParent();
		myFigure.getLabel().setText( getHeaderName() );
		Rectangle bounds = getPersistentClassViewAdapter().getBounds().getCopy();
		parent.setLayoutConstraint( this, myFigure, bounds );		
	}

	protected IFigure createFigure() {
		
		String unqualify = getHeaderName();
		return new PersistentClassFigure(new EditableLabel(unqualify));
	}

	private String getHeaderName() {
		String unqualify = getPersistentClass().getEntityName();
		if(unqualify.indexOf('.')>=0) {
			unqualify = StringHelper.unqualify(getPersistentClass().getEntityName());
		}
		return unqualify;
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
		if(arg==PersistentClassViewAdapter.ASSOCIATONS) {
			refreshSourceConnections(); 
			refreshTargetConnections();
		} else {
			refreshVisuals();
		}
	}
	
	protected List getModelSourceConnections() {
		return getPersistentClassViewAdapter().getSourceAssociations();		
	}
	
	protected List getModelTargetConnections() {
		return getPersistentClassViewAdapter().getTargetAssociations();
	}

	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	
	
	
	
}