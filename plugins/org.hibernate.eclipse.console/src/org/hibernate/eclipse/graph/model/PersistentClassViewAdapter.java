package org.hibernate.eclipse.graph.model;

import org.hibernate.mapping.PersistentClass;

public class PersistentClassViewAdapter extends GraphNode {

	private PersistentClass persistentClass;

	private final ConfigurationViewAdapter configuration;

	public PersistentClassViewAdapter(ConfigurationViewAdapter configuration, PersistentClass clazz) {
		this.configuration = configuration;
		this.persistentClass = clazz;
				
	}

	
	public PersistentClass getPersistentClass() {
		return persistentClass;
	}


	public ConfigurationViewAdapter getConfiguration() {
		return configuration;
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

	public String toString() {
		return "PersistentClassAdapter: " + persistentClass;
	}

	public void createAssociations() {
		createInheritanceAssociations();		
	}
}
