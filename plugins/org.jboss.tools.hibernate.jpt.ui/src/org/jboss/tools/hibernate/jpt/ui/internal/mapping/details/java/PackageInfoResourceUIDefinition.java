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

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jpt.common.core.JptResourceType;
import org.eclipse.jpt.common.core.internal.utility.PlatformTools;
import org.eclipse.jpt.common.core.resource.java.JavaResourceCompilationUnit;
import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.ui.jface.ItemTreeStateProviderFactoryProvider;
import org.eclipse.jpt.common.utility.internal.iterable.IterableTools;
import org.eclipse.jpt.common.utility.iterable.ListIterable;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.AttributeMapping;
import org.eclipse.jpt.jpa.core.context.PersistentType;
import org.eclipse.jpt.jpa.core.context.PersistentAttribute;
import org.eclipse.jpt.jpa.core.context.TypeMapping;
import org.eclipse.jpt.jpa.ui.MappingResourceUiDefinition;
import org.eclipse.jpt.jpa.ui.ResourceUiDefinition;
import org.eclipse.jpt.jpa.ui.details.DefaultMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.details.JpaComposite;
import org.eclipse.jpt.jpa.ui.details.JpaDetailsProvider;
import org.eclipse.jpt.jpa.ui.details.MappingUiDefinition;
import org.eclipse.jpt.jpa.ui.editors.JpaEditorPageDefinition;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Dmitry Geraskov
 * The provider provides empty ui for Structure view for package-info.java
 */
public class PackageInfoResourceUIDefinition implements
		MappingResourceUiDefinition {
	
	private static final ItemTreeStateProviderFactoryProvider EMPTY = null;
	
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
		JptResourceType pirt = PlatformTools.getResourceType(JavaResourceCompilationUnit.PACKAGE_INFO_CONTENT_TYPE);
		return resourceType.equals(pirt);
	}

	public ItemTreeStateProviderFactoryProvider getStructureViewFactoryProvider() {
		return EMPTY;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.ui.MappingResourceUiDefinition#buildAttributeMappingComposite(java.lang.String, org.eclipse.jpt.common.utility.model.value.PropertyValueModel, org.eclipse.swt.widgets.Composite, org.eclipse.jpt.common.ui.WidgetFactory)
	 */
	@Override
	public JpaComposite buildAttributeMappingComposite(String key,
			PropertyValueModel<AttributeMapping> mappingHolder,
			PropertyValueModel<Boolean> enabledModel,
			Composite parent, WidgetFactory widgetFactory, ResourceManager resourcemgr) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.ui.MappingResourceUiDefinition#attributeMappingUiDefinitions()
	 */
	@Override
	public Iterable<MappingUiDefinition> getAttributeMappingUiDefinitions() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.ui.MappingResourceUiDefinition#getDefaultAttributeMappingUiDefinition(java.lang.String)
	 */
	@Override
	public DefaultMappingUiDefinition getDefaultAttributeMappingUiDefinition(
			String key) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.ui.MappingResourceUiDefinition#buildTypeMappingComposite(java.lang.String, org.eclipse.jpt.common.utility.model.value.PropertyValueModel, org.eclipse.swt.widgets.Composite, org.eclipse.jpt.common.ui.WidgetFactory)
	 */
	@Override
	public JpaComposite buildTypeMappingComposite(String key,
			PropertyValueModel<TypeMapping> mappingHolder,
			PropertyValueModel<Boolean> enabledModel,
			Composite parent,
			WidgetFactory widgetFactory, ResourceManager resourceManager) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.ui.MappingResourceUiDefinition#typeMappingUiDefinitions()
	 */
	@Override
	public Iterable<MappingUiDefinition> getTypeMappingUiDefinitions() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.ui.MappingResourceUiDefinition#getDefaultTypeMappingUiDefinition()
	 */
	@Override
	public DefaultMappingUiDefinition getDefaultTypeMappingUiDefinition() {
		return null;
	}

	@Override
	public MappingUiDefinition getTypeMappingUiDefinition(
			String mappingKey) {
		Iterable<MappingUiDefinition> iterable = getTypeMappingUiDefinitions();
		Iterator<MappingUiDefinition> iter = iterable.iterator();
		while (iter.hasNext()) {
			MappingUiDefinition mapping = iter.next();
			if (mapping.getKey().equals(mappingKey)) {
				return mapping;
			}
		}
		return null;
	}

	@Override
	public MappingUiDefinition getAttributeMappingUiDefinition(
			String mappingKey) {
		Iterable<MappingUiDefinition> iterable = getAttributeMappingUiDefinitions();
		Iterator<MappingUiDefinition> iter = iterable.iterator();
		while (iter.hasNext()) {
			MappingUiDefinition mapping = iter.next();
			if (mapping.getKey().equals(mappingKey)) {
				return mapping;
			}
		}
		return null;
	}

	@Override
	public ListIterable<JpaEditorPageDefinition> getEditorPageDefinitions() {
		return IterableTools.emptyListIterable();
	}

	@Override
	public Iterable<JpaDetailsProvider> getDetailsProviders() {
		return IterableTools.emptyListIterable();
	}

}
