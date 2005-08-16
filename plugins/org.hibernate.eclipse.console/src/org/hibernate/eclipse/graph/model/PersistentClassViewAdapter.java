package org.hibernate.eclipse.graph.model;

import java.util.List;
import java.util.Observable;

import org.eclipse.draw2d.geometry.Rectangle;
import org.hibernate.mapping.PersistentClass;

public class PersistentClassViewAdapter extends Observable {

	private PersistentClass persistentClass;

	private Rectangle bounds = new Rectangle( 0, 0, -1, -1 );

	private final ConfigurationViewAdapter configuration;

	private List sourceAssociations;

	public PersistentClassViewAdapter(ConfigurationViewAdapter configuration, PersistentClass clazz) {
		this.configuration = configuration;
		this.persistentClass = clazz;
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
		if(sourceAssociations==null) {
			createInheritanceAssociations();
			sourceAssociations = configuration.getSourceAssociations(this.getPersistentClass().getEntityName());
		} 
		return sourceAssociations;
	}


	private void createInheritanceAssociations() {
		
		PersistentClass superclass = getPersistentClass().getSuperclass();
		if(superclass!=null) {
			PersistentClassViewAdapter target = getConfiguration().getPersistentClassViewAdapter(superclass.getEntityName());
			configuration.addAssociation(new InheritanceViewAdapter(this, target));
		}
	}

	public List getTargetAssociations() {
		List targetAssociations = getConfiguration().getTargetAssociations(this.getPersistentClass().getEntityName());
		return targetAssociations;
	}
	
	public String toString() {
		return "PersistentClassAdapter: " + persistentClass;
	}

}
