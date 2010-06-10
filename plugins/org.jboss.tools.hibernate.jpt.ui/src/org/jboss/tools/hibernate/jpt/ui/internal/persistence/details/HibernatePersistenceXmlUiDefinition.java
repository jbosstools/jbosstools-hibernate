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
package org.jboss.tools.hibernate.jpt.ui.internal.persistence.details;

import org.eclipse.jpt.core.JpaResourceType;
import org.eclipse.jpt.core.JptCorePlugin;
import org.eclipse.jpt.ui.ResourceUiDefinition;
import org.eclipse.jpt.ui.internal.persistence.details.AbstractPersistenceXmlResourceUiDefinition;
import org.eclipse.jpt.ui.internal.persistence.details.PersistenceXmlUiFactory;
import org.eclipse.jpt.ui.internal.structure.PersistenceResourceModelStructureProvider;
import org.eclipse.jpt.ui.structure.JpaStructureProvider;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernatePersistenceXmlUiDefinition extends
		AbstractPersistenceXmlResourceUiDefinition {
	// singleton
	private static final ResourceUiDefinition INSTANCE = new HibernatePersistenceXmlUiDefinition();
	
	
	/**
	 * Return the singleton
	 */
	public static ResourceUiDefinition instance() {
		return INSTANCE;
	}
	
	
	/**
	 * Enforce singleton usage
	 */
	private HibernatePersistenceXmlUiDefinition() {
		super();
	}
	
	
	@Override
	protected PersistenceXmlUiFactory buildPersistenceXmlUiFactory() {
		return new HibernatePersistenceXmlUiFactory();
	}
	
	public boolean providesUi(JpaResourceType resourceType) {
		return resourceType.equals(JptCorePlugin.PERSISTENCE_XML_1_0_RESOURCE_TYPE);
	}
	
	public JpaStructureProvider getStructureProvider() {
		return PersistenceResourceModelStructureProvider.instance();
	}
}
