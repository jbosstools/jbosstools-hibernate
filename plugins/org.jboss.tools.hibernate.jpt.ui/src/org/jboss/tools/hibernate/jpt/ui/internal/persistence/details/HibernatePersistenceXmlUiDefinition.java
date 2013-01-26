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

import java.util.List;

import org.eclipse.jpt.common.core.JptResourceType;
import org.eclipse.jpt.common.core.internal.utility.PlatformTools;
import org.eclipse.jpt.common.ui.jface.ItemTreeStateProviderFactoryProvider;
import org.eclipse.jpt.jpa.core.resource.persistence.JPA;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistence;
import org.eclipse.jpt.jpa.ui.ResourceUiDefinition;
import org.eclipse.jpt.jpa.ui.editors.JpaEditorPageDefinition;
import org.eclipse.jpt.jpa.ui.internal.AbstractResourceUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceUnitConnectionEditorPageDefinition;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceUnitGeneralEditorPageDefinition;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceUnitPropertiesEditorPageDefinition;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceXmlUiDefinition;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernatePersistenceXmlUiDefinition extends AbstractResourceUiDefinition {
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
	protected void addEditorPageDefinitionsTo(List<JpaEditorPageDefinition> definitions) {
		definitions.add(PersistenceUnitGeneralEditorPageDefinition.instance());
		definitions.add(PersistenceUnitConnectionEditorPageDefinition.instance());
		definitions.add(PersistenceUnitPropertiesEditorPageDefinition.instance());
		definitions.add(HibernatePropertiesPageDefinition.instance());
	}
	
	public boolean providesUi(JptResourceType resourceType) {
		JptResourceType resType = PlatformTools.getResourceType(XmlPersistence.CONTENT_TYPE, JPA.SCHEMA_VERSION);
		return resourceType.equals(resType);
	}

	public ItemTreeStateProviderFactoryProvider getStructureViewFactoryProvider() {
		return PersistenceXmlUiDefinition.STRUCTURE_VIEW_FACTORY_PROVIDER;
	}
}
