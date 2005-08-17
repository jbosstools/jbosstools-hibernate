package org.hibernate.eclipse.graph.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.eclipse.draw2d.geometry.Rectangle;
import org.hibernate.mapping.PersistentClass;

public class PersistentClassViewAdapter extends Observable {

	public static final String ASSOCIATONS = "ASSOCIATIONS";

	private PersistentClass persistentClass;

	private Rectangle bounds = new Rectangle( 0, 0, -1, -1 );

	private final ConfigurationViewAdapter configuration;

	private List targetAssociations;
	private List sourceAssociations;

	public PersistentClassViewAdapter(ConfigurationViewAdapter configuration, PersistentClass clazz) {
		this.configuration = configuration;
		this.persistentClass = clazz;
		
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

	public PersistentClass getPersistentClass() {
		return persistentClass;
	}


	public ConfigurationViewAdapter getConfiguration() {
		return configuration;
	}


	public List getSourceAssociations() {
		checkAssociations();
		return sourceAssociations;
	}


	private void checkAssociations() {
		if(sourceAssociations==null) {
			sourceAssociations=new ArrayList();
			createInheritanceAssociations();
		}		
	}


	private void createInheritanceAssociations() {
		
		PersistentClass superclass = getPersistentClass().getSuperclass();
		if(superclass!=null) {
			PersistentClassViewAdapter target = getConfiguration().getPersistentClassViewAdapter(superclass.getEntityName());
			InheritanceViewAdapter iva = new InheritanceViewAdapter(this, target);
			this.addSourceAssociation(iva);
			target.addTargetAssociation(iva);			
		}
	}

	void addTargetAssociation(AssociationViewAdapter iva) {
		targetAssociations.add(iva);
		setChanged();
		notifyObservers(ASSOCIATONS);
	}

	private void addSourceAssociation(AssociationViewAdapter iva) {
		checkAssociations();
		sourceAssociations.add(iva);
		setChanged();
		notifyObservers(ASSOCIATONS);
	}


	public List getTargetAssociations() {
		return targetAssociations;
	}
	
	public String toString() {
		return "PersistentClassAdapter: " + persistentClass;
	}

}
