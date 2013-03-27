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
package org.jboss.tools.hibernate.jpt.core.internal.context.persistence.jpa2;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.jpt.common.core.JptResourceType;
import org.eclipse.jpt.common.core.internal.utility.PlatformTools;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceXmlContextModelFactory;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceXmlDefinition;
import org.eclipse.jpt.jpa.core.internal.context.persistence.AbstractPersistenceXmlDefinition;
import org.eclipse.jpt.jpa.core.resource.persistence.PersistenceFactory;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistence;
import org.eclipse.jpt.jpa.core.resource.persistence.v2_0.JPA2_0;

/**
 * @author Dmitry Geraskov
 *
 */
public class Hibernate2_0PersistenceXmlDefinition extends
		AbstractPersistenceXmlDefinition {

	// singleton
	private static final PersistenceXmlDefinition INSTANCE = 
			new Hibernate2_0PersistenceXmlDefinition();
	
	
	/**
	 * Return the singleton
	 */
	public static PersistenceXmlDefinition instance() {
		return INSTANCE;
	}
	
	
	/**
	 * Enforce singleton usage
	 */
	private Hibernate2_0PersistenceXmlDefinition() {
		super();
	}
	
	
	public EFactory getResourceModelFactory() {
		return PersistenceFactory.eINSTANCE;
	}
	
	@Override
	protected PersistenceXmlContextModelFactory buildContextModelFactory() {
		return new Hibernate2_0PersistenceXmlContextNodeFactory();
	}
	
	public JptResourceType getResourceType() {
		return PlatformTools.getResourceType(
				XmlPersistence.CONTENT_TYPE, 
				JPA2_0.SCHEMA_VERSION);
	}

}
