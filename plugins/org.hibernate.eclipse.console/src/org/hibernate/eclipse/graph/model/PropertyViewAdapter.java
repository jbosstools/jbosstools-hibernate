package org.hibernate.eclipse.graph.model;

import java.util.List;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.Property;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.util.StringHelper;

public class PropertyViewAdapter {

	final private Property property;

	private final ConfigurationViewAdapter configuration;

	private final PersistentClassViewAdapter clazz;

	private List sourceAssociations;

	public PropertyViewAdapter(PersistentClassViewAdapter clazz,
			Property property) {
		this.clazz = clazz;
		this.property = property;
		this.configuration = clazz.getConfiguration();
	}

	public Property getProperty() {
		return property;
	}

	public List getSourceConnections() {		
		if(sourceAssociations==null) {
			createSingleEndedEnityAssociations();
			sourceAssociations = configuration.getSourceAssociations(StringHelper.qualify(clazz.getPersistentClass().getEntityName(), property.getName())); 
		}
		return sourceAssociations;
	}

	public List getTargetConnections() {
		return configuration.getTargetAssociations(StringHelper.qualify(clazz.getPersistentClass().getEntityName(), property.getName()));
	}
	
	private void createSingleEndedEnityAssociations() {
		if ( property.getType().isEntityType() ) {
			EntityType et = (EntityType) property.getType();
			PersistentClassViewAdapter target = configuration
					.getPersistentClassViewAdapter( et.getAssociatedEntityName() );
			configuration.addAssociation( new PropertyAssociationViewAdapter( clazz, this, target ) );
		} 
		
		if ( property.getValue() instanceof Collection ) {
			Collection collection = (Collection) property.getValue();
			if(collection.getElement() instanceof OneToMany) {
				OneToMany oneToMany = (OneToMany) collection.getElement();
				String entityName = oneToMany.getAssociatedClass().getEntityName();
				PersistentClassViewAdapter target = configuration
				.getPersistentClassViewAdapter( entityName );
				configuration.addAssociation( new PropertyAssociationViewAdapter( clazz, this, target ) );
			}
			
		}
		
		
	}
}
