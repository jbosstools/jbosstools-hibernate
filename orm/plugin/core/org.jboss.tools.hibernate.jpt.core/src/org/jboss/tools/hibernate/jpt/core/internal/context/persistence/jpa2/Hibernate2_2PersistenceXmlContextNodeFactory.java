/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.persistence.jpa2;

import org.eclipse.jpt.jpa.core.context.persistence.ClassRef;
import org.eclipse.jpt.jpa.core.context.persistence.Persistence;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceXml;
import org.eclipse.jpt.jpa.core.internal.jpa2_2.context.persistence.GenericPersistenceXmlContextModelFactory2_2;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlJavaClassRef;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistence;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.HibernatePersistenceUnitProperties;
import org.jboss.tools.hibernate.jpt.core.internal.context.persistence.HibernateClassRef;
import org.jboss.tools.hibernate.jpt.core.internal.context.persistence.HibernatePersistence;
import org.jboss.tools.hibernate.jpt.core.internal.context.persistence.HibernatePersistenceUnitPropertiesBuilder;

/**
 * @author Koen Aers, jkopriva@redhat.com
 *
 */
public class Hibernate2_2PersistenceXmlContextNodeFactory extends
		GenericPersistenceXmlContextModelFactory2_2 implements HibernatePersistenceUnitPropertiesBuilder {

	public PersistenceUnit buildPersistenceUnit(Persistence parent, XmlPersistenceUnit xmlPersistenceUnit) {
		return new HibernatePersistenceUnit(parent, xmlPersistenceUnit);
	}
	
	public HibernatePersistenceUnitProperties buildHibernatePersistenceUnitProperties(PersistenceUnit parent) {
		return new HibernatePersistenceUnitProperties(parent);
	}
	
	public Persistence buildPersistence(PersistenceXml parent, XmlPersistence xmlPersistence) {
		return new HibernatePersistence(parent, xmlPersistence);
	}
	
	@Override
	public ClassRef buildClassRef(PersistenceUnit parent,
			XmlJavaClassRef classRef) {
		return new HibernateClassRef(parent, classRef);
	}
}
