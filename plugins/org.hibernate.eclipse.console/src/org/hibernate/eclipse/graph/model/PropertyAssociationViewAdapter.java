package org.hibernate.eclipse.graph.model;

import org.hibernate.util.StringHelper;

public class PropertyAssociationViewAdapter extends AssociationViewAdapter {

	private final PropertyViewAdapter property;
	private final PersistentClassViewAdapter clazz;
	private final PersistentClassViewAdapter target;

	public PropertyAssociationViewAdapter(PersistentClassViewAdapter clazz, PropertyViewAdapter property, PersistentClassViewAdapter target) {
		this.clazz = clazz;
		this.property = property;
		this.target = target;		
	}

	public String getSourceName() {
		return StringHelper.qualify(clazz.getPersistentClass().getEntityName(), property.getProperty().getName());
	}

	public String getTargetName() {
		return target.getPersistentClass().getEntityName();
	}

}
