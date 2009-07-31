/*******************************************************************************
  * Copyright (c) 2008-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.core.context.persistence.PersistenceUnit.Property;
import org.eclipse.jpt.utility.internal.model.AbstractModel;
import org.eclipse.jpt.utility.model.value.ListValueModel;


/**
 * @author Dmitry Geraskov
 *
 */
public abstract class  HibernatePersistenceUnitProperties extends AbstractModel implements PersistenceUnitProperties {

	private PersistenceUnit persistenceUnit;

	private PersistenceUnitPropertyListListener propertyListListener;
	
	private Map<String, String> propertyNames;
	
	protected HibernatePersistenceUnitProperties(
			PersistenceUnit parent, 
			ListValueModel<Property> propertyListAdapter) {
		super();
		this.initialize(parent, propertyListAdapter);
	}
	
	protected void initialize(
			PersistenceUnit parent, 
			ListValueModel<Property> propertyListAdapter) {
		this.persistenceUnit = parent;
		
		this.propertyListListener = new PersistenceUnitPropertyListListener(this);
		propertyListAdapter.addListChangeListener(ListValueModel.LIST_VALUES, this.propertyListListener);
		
		this.initializePropertyNames();
		this.initializeProperties();
	}
	
	protected void initializePropertyNames() {
		this.propertyNames = new HashMap<String, String>();
		this.addPropertyNames(this.propertyNames);
	}

	/**
	 * Initializes properties with values from the persistence unit.
	 */
	protected abstract void initializeProperties();

	// ********** behavior **********
	public PersistenceUnit persistenceUnit() {
		return this.persistenceUnit;
	}
	
	public JpaProject getJpaProject() {
		return this.persistenceUnit.getJpaProject();
	}

	public PersistenceUnitPropertyListListener propertyListListener() {
		return this.propertyListListener;
	}

	private Map<String, String> propertyNames() {
		return this.propertyNames;
	}

	/**
	 * Adds property names key/value pairs, used by the methods: itemIsProperty
	 * and propertyIdFor.
	 * 
	 * key = EclipseLink property key; value = property id
	 */
	protected abstract void addPropertyNames(Map<String, String> propertyNames);

	/**
	 * Method used for identifying the given property.
	 */
	public boolean itemIsProperty(Property item) {
		if (item == null) {
			throw new IllegalArgumentException("Property is null"); //$NON-NLS-1$
		}
		return this.propertyNames().keySet().contains(item.getName());
	}

	/**
	 * Returns the property name used for change notification of the given
	 * property.
	 */
	public String propertyIdFor(Property property) {
		String propertyId = this.propertyNames().get(property.getName());
		if (propertyId == null) {
			throw new IllegalArgumentException("Illegal property: " + property.toString()); //$NON-NLS-1$
		}
		return propertyId;
	}

	protected String hibernateKeyFor(String propertyId) {
		for (String hibernateKey : this.propertyNames().keySet()) {
			if (this.propertyNames().get(hibernateKey).equals(propertyId)) {
				return hibernateKey;
			}
		}
		throw new IllegalArgumentException("Illegal property: " + propertyId); //$NON-NLS-1$
	}

	// ****** get/set String convenience methods *******
	/**
	 * Returns the String value of the given Property from the PersistenceXml.
	 */
	protected String getStringValue(String key) {
		return this.getStringValue(key, null);
	}

	protected String getStringValue(String key, String keySuffix) {
		String elKey = (keySuffix == null) ? key : key + keySuffix;
		if (this.persistenceUnit().getProperty(elKey) != null) {
			// TOREVIEW - handle incorrect String in persistence.xml
			return this.persistenceUnit().getProperty(elKey).getValue();
		}
		return null;
	}

	/**
	 * Put the given String value into the PersistenceXml.
	 * @param key
	 *            EclipseLink Key
	 * @param keySuffix
	 *            e.g. entity name
	 * @param newValue
	 *            value to be associated with the key
	 * @param allowDuplicate
	 */
	protected void putStringValue(String key, String keySuffix, String newValue, boolean allowDuplicate) {
		String elKey = (keySuffix == null) ? key : key + keySuffix;
		if (newValue == null) {
			this.persistenceUnit().removeProperty(elKey);
		}
		else {
			this.persistenceUnit().setProperty(elKey, newValue, allowDuplicate);
		}
	}

	// ******** Convenience methods ********
	/**
	 * Put into persistenceUnit properties.
	 * 
	 * @param key -
	 *            property name
	 * @param value -
	 *            property value
	 */
	protected void putProperty(String key, Object value) {
		String elKey = this.hibernateKeyFor(key);
		if (value == null)
			this.removeProperty(elKey);
		else
			 this.putProperty_(elKey, value);
	}

	private void putProperty_(String key, Object value) {
		this.persistenceUnit().setProperty(key, value.toString(), false);
	}

	/**
	 * Removes a property with the given key.
	 */
	protected void removeProperty(String key) {
		if(this.persistenceUnit().getProperty(key) != null) { 
			this.persistenceUnit().removeProperty(key);
		}
	}

}
