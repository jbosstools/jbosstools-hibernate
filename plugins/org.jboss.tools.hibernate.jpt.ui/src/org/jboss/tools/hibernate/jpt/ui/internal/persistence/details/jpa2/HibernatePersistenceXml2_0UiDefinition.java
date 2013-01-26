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
package org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.jpa2;

import java.util.List;

import org.eclipse.jpt.common.core.JptResourceType;
import org.eclipse.jpt.common.core.internal.utility.PlatformTools;
import org.eclipse.jpt.common.ui.jface.ItemTreeStateProviderFactoryProvider;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistence;
import org.eclipse.jpt.jpa.core.resource.persistence.v2_0.JPA2_0;
import org.eclipse.jpt.jpa.ui.ResourceUiDefinition;
import org.eclipse.jpt.jpa.ui.editors.JpaEditorPageDefinition;
import org.eclipse.jpt.jpa.ui.internal.AbstractResourceUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.jpa2.persistence.PersistenceUnitConnection2_0EditorPageDefinition;
import org.eclipse.jpt.jpa.ui.internal.jpa2.persistence.PersistenceUnitOptions2_0EditorPageDefinition;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceUnitPropertiesEditorPageDefinition;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceXmlUiDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.HibernatePersistenceUnitGeneralEditorPageDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.HibernatePropertiesPageDefinition;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernatePersistenceXml2_0UiDefinition extends AbstractResourceUiDefinition {
	// singleton
	private static final ResourceUiDefinition INSTANCE = new HibernatePersistenceXml2_0UiDefinition();
	
	
	/**
	 * Return the singleton
	 */
	public static ResourceUiDefinition instance() {
		return INSTANCE;
	}
	
	
	/**
	 * Enforce singleton usage
	 */
	private HibernatePersistenceXml2_0UiDefinition() {
		super();
	}
	
	
//	@Override
//	protected PersistenceXmlUiFactory buildPersistenceXmlUiFactory() {
//		return new HibernatePersistenceXml2_0UiFactory();
//	}
	
	@Override
	protected void addEditorPageDefinitionsTo(List<JpaEditorPageDefinition> definitions) {
		definitions.add(HibernatePersistenceUnitGeneralEditorPageDefinition.instance());
		definitions.add(PersistenceUnitConnection2_0EditorPageDefinition.instance());
		definitions.add(PersistenceUnitOptions2_0EditorPageDefinition.instance());
		definitions.add(PersistenceUnitPropertiesEditorPageDefinition.instance());
		definitions.add(HibernatePropertiesPageDefinition.instance());		
	}
		
	public boolean providesUi(JptResourceType resourceType) {
		JptResourceType resType = PlatformTools.getResourceType(XmlPersistence.CONTENT_TYPE, JPA2_0.SCHEMA_VERSION);
		return resourceType.equals(resType);
	}
	
	public ItemTreeStateProviderFactoryProvider getStructureViewFactoryProvider() {
		return PersistenceXmlUiDefinition.STRUCTURE_VIEW_FACTORY_PROVIDER;
	}
}
