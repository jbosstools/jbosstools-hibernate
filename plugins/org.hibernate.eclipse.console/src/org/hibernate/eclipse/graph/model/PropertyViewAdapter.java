package org.hibernate.eclipse.graph.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.Property;
import org.hibernate.type.EntityType;

public class PropertyViewAdapter extends Observable {

	final private Property property;

	private final ConfigurationViewAdapter configuration;

	private final PersistentClassViewAdapter clazz;

	private List sourceAssociations;
	private List targetAssociations;
	
	public PropertyViewAdapter(PersistentClassViewAdapter clazz,
			Property property) {
		this.clazz = clazz;
		this.property = property;
		this.configuration = clazz.getConfiguration();
		this.sourceAssociations = null;
		this.targetAssociations = Collections.EMPTY_LIST;
		
	}

	public Property getProperty() {
		return property;
	}

	public List getSourceConnections() {
		checkConnections();
		return sourceAssociations;
	}

	private void checkConnections() {
		if(sourceAssociations==null) {
			sourceAssociations = new ArrayList();
			createSingleEndedEnityAssociations();
		}		
	}

	public List getTargetConnections() {
		return targetAssociations;
	}
	
	private void createSingleEndedEnityAssociations() {
		if ( property.getType().isEntityType() ) {
			EntityType et = (EntityType) property.getType();
			PersistentClassViewAdapter target = configuration
					.getPersistentClassViewAdapter( et.getAssociatedEntityName() );
			PropertyAssociationViewAdapter pava = new PropertyAssociationViewAdapter( clazz, this, target );
			this.addSourceAssociation( pava );
			target.addTargetAssociation( pava );
		} 
		
		if ( property.getValue() instanceof Collection ) {
			Collection collection = (Collection) property.getValue();
			if(collection.getElement() instanceof OneToMany) {
				OneToMany oneToMany = (OneToMany) collection.getElement();
				String entityName = oneToMany.getAssociatedClass().getEntityName();
				PersistentClassViewAdapter target = configuration
				.getPersistentClassViewAdapter( entityName );
				PropertyAssociationViewAdapter pava = new PropertyAssociationViewAdapter( clazz, this, target );
				this.addSourceAssociation( pava );
				target.addTargetAssociation( pava );
			}
			
		}
		
		
	}

	private void addSourceAssociation(PropertyAssociationViewAdapter pava) {
		checkConnections();
		sourceAssociations.add(pava);
		setChanged();
		notifyObservers(PersistentClassViewAdapter.ASSOCIATONS);
	}
}
