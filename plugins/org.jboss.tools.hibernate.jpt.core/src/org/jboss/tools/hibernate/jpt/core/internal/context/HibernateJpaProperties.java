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

import java.util.ListIterator;

import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.core.context.persistence.PersistenceUnit.Property;
import org.eclipse.jpt.utility.internal.model.AbstractModel;
import org.eclipse.jpt.utility.internal.model.value.ItemPropertyListValueModelAdapter;
import org.eclipse.jpt.utility.internal.model.value.ListAspectAdapter;
import org.eclipse.jpt.utility.internal.model.value.SimplePropertyValueModel;
import org.eclipse.jpt.utility.model.event.PropertyChangeEvent;
import org.eclipse.jpt.utility.model.value.ListValueModel;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.HibernateBasic;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaProperties extends AbstractModel implements
		HibernateProperties {
	
	private PersistenceUnit persistenceUnit;
	
	private BasicHibernateProperties basicHibernateProperties;
	
	private ListValueModel<Property> propertiesAdapter;
	private ListValueModel<Property> propertyListAdapter;
	
	public HibernateJpaProperties(PersistenceUnit parent) {
		super();
		this.initialize(parent);
	}
	
	protected void initialize(PersistenceUnit parent) {
		this.persistenceUnit = parent;
		PropertyValueModel<PersistenceUnit> persistenceUnitHolder = 
			new SimplePropertyValueModel<PersistenceUnit>(this.persistenceUnit);
		
		this.propertiesAdapter = this.buildPropertiesAdapter(persistenceUnitHolder);
		this.propertyListAdapter = this.buildPropertyListAdapter(this.propertiesAdapter);
		
		this.basicHibernateProperties = this.buildBasicProperties();
	}
	
	private ListValueModel<Property> buildPropertyListAdapter(ListValueModel<Property> propertiesAdapter) {
		return new ItemPropertyListValueModelAdapter<Property>(propertiesAdapter, Property.VALUE_PROPERTY);
	}
	
	private ListValueModel<Property> buildPropertiesAdapter(PropertyValueModel<PersistenceUnit> subjectHolder) {
		return new ListAspectAdapter<PersistenceUnit, Property>(subjectHolder, PersistenceUnit.PROPERTIES_LIST) {
			@Override
			protected ListIterator<Property> listIterator_() {
				return this.subject.properties();
			}

			@Override
			protected int size_() {
				return this.subject.propertiesSize();
			}
		};
	}
	
	// ******** Behavior *********
	public BasicHibernateProperties getBasicHibernate() {
		return this.basicHibernateProperties;
	}
	
	private BasicHibernateProperties buildBasicProperties() {
		return new HibernateBasic(this.persistenceUnit(), this.propertyListAdapter());
	}

	public ListValueModel<Property> propertyListAdapter() {
		return this.propertyListAdapter;
	}

	public PersistenceUnit persistenceUnit() {
		return this.persistenceUnit;
	}
	
	public JpaProject getJpaProject() {
		return this.persistenceUnit.getJpaProject();
	}

	public boolean itemIsProperty(Property item) {
		throw new UnsupportedOperationException();
	}

	public void propertyChanged(PropertyChangeEvent event) {
		throw new UnsupportedOperationException();
	}

	public String propertyIdFor(Property property) {
		throw new UnsupportedOperationException();
	}
}
