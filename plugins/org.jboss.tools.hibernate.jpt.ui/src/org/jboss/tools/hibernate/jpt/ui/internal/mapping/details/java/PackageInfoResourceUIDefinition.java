/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.java;

import java.util.Iterator;

import org.eclipse.jpt.common.core.JptCommonCorePlugin;
import org.eclipse.jpt.common.core.JptResourceType;
import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.AttributeMapping;
import org.eclipse.jpt.jpa.core.context.PersistentType;
import org.eclipse.jpt.jpa.core.context.ReadOnlyPersistentAttribute;
import org.eclipse.jpt.jpa.core.context.TypeMapping;
import org.eclipse.jpt.jpa.ui.MappingResourceUiDefinition;
import org.eclipse.jpt.jpa.ui.ResourceUiDefinition;
import org.eclipse.jpt.jpa.ui.details.DefaultMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.details.JpaComposite;
import org.eclipse.jpt.jpa.ui.details.MappingUiDefinition;
import org.eclipse.jpt.jpa.ui.structure.JpaStructureProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Dmitry Geraskov
 * The provider provides empty ui for Structure view for package-info.java
 */
public class PackageInfoResourceUIDefinition implements
		MappingResourceUiDefinition {
	
	private static final JpaStructureProvider EMPTY = null;
	
	// singleton
	private static final ResourceUiDefinition INSTANCE = new PackageInfoResourceUIDefinition();

	/**
	 * Return the singleton.
	 */
	public static ResourceUiDefinition instance() {
		return INSTANCE;
	}
	
	/**
	 * Enforce singleton usage
	 */
	private PackageInfoResourceUIDefinition() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.ui.ResourceUiDefinition#providesUi(org.eclipse.jpt.common.core.JptResourceType)
	 */
	@Override
	public boolean providesUi(JptResourceType resourceType) {
		return resourceType.equals(JptCommonCorePlugin.JAVA_SOURCE_PACKAGE_INFO_RESOURCE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.ui.ResourceUiDefinition#getStructureProvider()
	 */
	@Override
	public JpaStructureProvider getStructureProvider() {
		return EMPTY;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.ui.MappingResourceUiDefinition#buildAttributeMappingComposite(java.lang.String, org.eclipse.jpt.common.utility.model.value.PropertyValueModel, org.eclipse.swt.widgets.Composite, org.eclipse.jpt.common.ui.WidgetFactory)
	 */
	@Override
	public JpaComposite buildAttributeMappingComposite(String key,
			PropertyValueModel<AttributeMapping> mappingHolder,
			Composite parent, WidgetFactory widgetFactory) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.ui.MappingResourceUiDefinition#attributeMappingUiDefinitions()
	 */
	@Override
	public Iterator<MappingUiDefinition<ReadOnlyPersistentAttribute, ? extends AttributeMapping>> attributeMappingUiDefinitions() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.ui.MappingResourceUiDefinition#getDefaultAttributeMappingUiDefinition(java.lang.String)
	 */
	@Override
	public DefaultMappingUiDefinition<ReadOnlyPersistentAttribute, ? extends AttributeMapping> getDefaultAttributeMappingUiDefinition(
			String key) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.ui.MappingResourceUiDefinition#buildTypeMappingComposite(java.lang.String, org.eclipse.jpt.common.utility.model.value.PropertyValueModel, org.eclipse.swt.widgets.Composite, org.eclipse.jpt.common.ui.WidgetFactory)
	 */
	@Override
	public JpaComposite buildTypeMappingComposite(String key,
			PropertyValueModel<TypeMapping> mappingHolder, Composite parent,
			WidgetFactory widgetFactory) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.ui.MappingResourceUiDefinition#typeMappingUiDefinitions()
	 */
	@Override
	public Iterator<MappingUiDefinition<PersistentType, ? extends TypeMapping>> typeMappingUiDefinitions() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.ui.MappingResourceUiDefinition#getDefaultTypeMappingUiDefinition()
	 */
	@Override
	public DefaultMappingUiDefinition<PersistentType, ? extends TypeMapping> getDefaultTypeMappingUiDefinition() {
		return null;
	}

}
