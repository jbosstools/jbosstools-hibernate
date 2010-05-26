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

import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.core.context.persistence.PersistenceUnit.Property;
import org.eclipse.jpt.utility.model.Model;
import org.eclipse.jpt.utility.model.listener.PropertyChangeListener;

/**
 * @author Dmitry Geraskov
 *
 */
public interface PersistenceUnitProperties extends Model, PropertyChangeListener {

	/**
	 * Method used for identifying the given property.
	 */
	boolean itemIsProperty(Property item);

	/**
	 * Returns the property name used for change notification of the given property.
	 */
	String propertyIdFor(Property property);
	
	/**
	 * Return the PersistenceUnit of this Properties.
	 */
	PersistenceUnit persistenceUnit();
	
	/**
	 * Return the JPA project the PersistenceUnit belongs to.
	 */
	JpaProject getJpaProject();
	
	void updateProperties();
}
