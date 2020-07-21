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

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.jpt.common.core.JptResourceType;
import org.eclipse.jpt.common.core.internal.utility.ContentTypeTools;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceXmlContextModelFactory;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceXmlDefinition;
import org.eclipse.jpt.jpa.core.internal.context.persistence.AbstractPersistenceXmlDefinition;
import org.eclipse.jpt.jpa.core.resource.persistence.PersistenceFactory;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistence;

/**
 * @author Koen Aers, jkopriva@redhat.com
 *
 */
public class Hibernate2_2PersistenceXmlDefinition extends
		AbstractPersistenceXmlDefinition {

	// singleton
	private static final PersistenceXmlDefinition INSTANCE = 
			new Hibernate2_2PersistenceXmlDefinition();
	
	
	/**
	 * Return the singleton
	 */
	public static PersistenceXmlDefinition instance() {
		return INSTANCE;
	}
	
	
	/**
	 * Enforce singleton usage
	 */
	private Hibernate2_2PersistenceXmlDefinition() {
		super();
	}
	
	
	public EFactory getResourceModelFactory() {
		return PersistenceFactory.eINSTANCE;
	}
	
	@Override
	protected PersistenceXmlContextModelFactory buildContextModelFactory() {
		return new Hibernate2_2PersistenceXmlContextNodeFactory();
	}
	
	public JptResourceType getResourceType() {
		return ContentTypeTools.getResourceType(
				XmlPersistence.CONTENT_TYPE, 
				org.eclipse.jpt.jpa.core.resource.orm.v2_2.JPA2_2.SCHEMA_VERSION);
	}

}
