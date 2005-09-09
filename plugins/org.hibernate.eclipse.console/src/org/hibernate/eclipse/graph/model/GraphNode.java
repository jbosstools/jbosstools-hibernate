package org.hibernate.eclipse.graph.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.eclipse.draw2d.geometry.Rectangle;

/**
 * base class for model elements in our views that are node-like and have a moveable physical bound.
 *  
 * @author Max Rydahl Andersen
 *
 */
public abstract class GraphNode extends Observable {

	public static final String ASSOCIATONS = "ASSOCIATIONS";
	private Rectangle bounds = new Rectangle( 0, 0, -1, -1 );
	protected List targetAssociations;
	protected List sourceAssociations;

	public GraphNode() {
		targetAssociations = new ArrayList();
		sourceAssociations = null; //lazily created
	}
	
	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		Rectangle oldBounds = this.bounds;
		if ( !bounds.equals( oldBounds ) ) {
			this.bounds = bounds;
			setChanged();
			notifyObservers();
		}
	}
	
	abstract public void createAssociations();

	public List getSourceAssociations() {
		checkAssociations();
		return sourceAssociations;
	}

	private void checkAssociations() {
		if(sourceAssociations==null) {
			sourceAssociations=new ArrayList();
			createAssociations();
		}		
	}

	protected void addTargetAssociation(AssociationViewAdapter iva) {
		targetAssociations.add(iva);
		setChanged();
		notifyObservers(ASSOCIATONS);
	}

	protected void addSourceAssociation(AssociationViewAdapter iva) {
		checkAssociations();
		sourceAssociations.add(iva);
		setChanged();
		notifyObservers(ASSOCIATONS);
	}

	public List getTargetAssociations() {
		return targetAssociations;
	}

}
