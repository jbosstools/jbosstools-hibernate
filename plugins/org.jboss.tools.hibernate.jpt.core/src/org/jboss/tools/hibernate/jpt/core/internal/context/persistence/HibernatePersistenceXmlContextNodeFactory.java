/*******************************************************************************
  * Copyright (c) 2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.persistence;

import org.eclipse.jpt.jpa.core.context.persistence.Persistence;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.core.internal.context.persistence.GenericPersistenceXmlContextNodeFactory;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.HibernatePersistenceUnitProperties;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernatePersistenceXmlContextNodeFactory extends
		GenericPersistenceXmlContextNodeFactory implements HibernatePersistenceUnitPropertiesBuilder {

	public PersistenceUnit buildPersistenceUnit(Persistence parent, XmlPersistenceUnit xmlPersistenceUnit) {
		return new HibernatePersistenceUnit(parent, xmlPersistenceUnit);
	}
	
	public HibernatePersistenceUnitProperties buildHibernatePersistenceUnitProperties(PersistenceUnit parent) {
		return new HibernatePersistenceUnitProperties(parent);
	}
}
