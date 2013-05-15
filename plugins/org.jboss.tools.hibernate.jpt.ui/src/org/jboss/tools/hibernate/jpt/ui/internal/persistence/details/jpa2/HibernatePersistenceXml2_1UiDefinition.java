/*******************************************************************************
  * Copyright (c) 2013 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.jpa2;

import java.util.List;

import org.eclipse.jpt.common.core.JptResourceType;
import org.eclipse.jpt.common.core.internal.utility.ContentTypeTools;
import org.eclipse.jpt.common.ui.jface.ItemTreeStateProviderFactoryProvider;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistence;
import org.eclipse.jpt.jpa.ui.ResourceUiDefinition;
import org.eclipse.jpt.jpa.ui.editors.JpaEditorPageDefinition;
import org.eclipse.jpt.jpa.ui.internal.AbstractResourceUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.jpa2.persistence.PersistenceUnitConnectionEditorPageDefinition2_0;
import org.eclipse.jpt.jpa.ui.internal.jpa2.persistence.PersistenceUnitOptionsEditorPageDefinition2_0;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceUnitPropertiesEditorPageDefinition;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceXmlUiDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.persistence.JPA2_1;
import org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.HibernatePersistenceUnitGeneralEditorPageDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.HibernatePropertiesPageDefinition;

/**
 * @author Koen Aers
 *
 */
public class HibernatePersistenceXml2_1UiDefinition extends AbstractResourceUiDefinition {

	// singleton
	private static final ResourceUiDefinition INSTANCE = new HibernatePersistenceXml2_1UiDefinition();
	
	
	/**
	 * Return the singleton
	 */
	public static ResourceUiDefinition instance() {
		return INSTANCE;
	}
	
	
	/**
	 * Enforce singleton usage
	 */
	private HibernatePersistenceXml2_1UiDefinition() {
		super();
	}
	
	
	@Override
	protected void addEditorPageDefinitionsTo(List<JpaEditorPageDefinition> definitions) {
		definitions.add(HibernatePersistenceUnitGeneralEditorPageDefinition.instance());
		definitions.add(PersistenceUnitConnectionEditorPageDefinition2_0.instance());
		definitions.add(PersistenceUnitOptionsEditorPageDefinition2_0.instance());
		definitions.add(PersistenceUnitPropertiesEditorPageDefinition.instance());
		definitions.add(HibernatePropertiesPageDefinition.instance());		
	}
		
	public boolean providesUi(JptResourceType resourceType) {
		JptResourceType resType = ContentTypeTools.getResourceType(XmlPersistence.CONTENT_TYPE, JPA2_1.SCHEMA_VERSION);
		return resourceType.equals(resType);
	}
	
	public ItemTreeStateProviderFactoryProvider getStructureViewFactoryProvider() {
		return PersistenceXmlUiDefinition.STRUCTURE_VIEW_FACTORY_PROVIDER;
	}
}
