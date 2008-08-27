/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.eclipse.jpt.core.context.persistence.Persistence;
import org.eclipse.jpt.core.internal.context.persistence.GenericPersistenceUnit;
import org.eclipse.jpt.core.resource.persistence.XmlPersistenceUnit;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernatePersistenceUnit extends GenericPersistenceUnit {
	
	private HibernateProperties hibernateProperties;

	/**
	 * @param parent
	 * @param persistenceUnit
	 */
	public HibernatePersistenceUnit(Persistence parent,
			XmlPersistenceUnit persistenceUnit) {
		super(parent, persistenceUnit);
	}
	
	protected void initialize(XmlPersistenceUnit xmlPersistenceUnit) {
		super.initialize(xmlPersistenceUnit);
		this.hibernateProperties = new HibernateJpaProperties(this);
	}

	/*
	 * put getters for specific properties here
	 * 
	 */

}
