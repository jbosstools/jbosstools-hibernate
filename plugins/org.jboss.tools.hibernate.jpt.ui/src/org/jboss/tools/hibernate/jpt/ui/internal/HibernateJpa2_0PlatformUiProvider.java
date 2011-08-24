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
package org.jboss.tools.hibernate.jpt.ui.internal;

import java.util.List;

import org.eclipse.jpt.jpa.ui.JpaPlatformUiProvider;
import org.eclipse.jpt.jpa.ui.ResourceUiDefinition;
import org.eclipse.jpt.jpa.ui.details.JpaDetailsProvider;
import org.eclipse.jpt.jpa.ui.internal.AbstractJpaPlatformUiProvider;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaPersistentAttributeDetailsProvider;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaPersistentTypeDetailsProvider;
import org.eclipse.jpt.jpa.ui.internal.details.orm.EntityMappingsDetailsProvider;
import org.eclipse.jpt.jpa.ui.internal.details.orm.OrmPersistentAttributeDetailsProvider;
import org.eclipse.jpt.jpa.ui.internal.details.orm.OrmPersistentTypeDetailsProvider;
import org.eclipse.jpt.jpa.ui.internal.jpa2.details.orm.EntityMappings2_0DetailsProvider;
import org.jboss.tools.hibernate.jpt.ui.internal.details.java.JavaPackageInfoDetailsProvider;
import org.jboss.tools.hibernate.jpt.ui.internal.jpa2.mapping.details.orm.Hibernate2_0OrmXmlUiDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.java.Hibernate2_0JavaResourceUiDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.java.PackageInfoResourceUIDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.orm.HibernateOrmXmlUiDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.HibernatePersistenceXmlUiDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.jpa2.HibernatePersistenceXml2_0UiDefinition;

/**
 * @author Dmitry Geraskov
 * 
 */
public class HibernateJpa2_0PlatformUiProvider extends
		AbstractJpaPlatformUiProvider {

	// singleton
	private static final JpaPlatformUiProvider INSTANCE = new HibernateJpa2_0PlatformUiProvider();

	/**
	 * Return the singleton.
	 */
	public static JpaPlatformUiProvider instance() {
		return INSTANCE;
	}

	/**
	 * Ensure single instance.
	 */
	private HibernateJpa2_0PlatformUiProvider() {
		super();
	}

	// ********** details providers **********

	@Override
	protected void addDetailsProvidersTo(List<JpaDetailsProvider> providers) {
		providers.add(JavaPackageInfoDetailsProvider.instance());
		providers.add(JavaPersistentTypeDetailsProvider.instance());
		providers.add(JavaPersistentAttributeDetailsProvider.instance());
		providers.add(EntityMappingsDetailsProvider.instance());
		providers.add(OrmPersistentTypeDetailsProvider.instance());
		providers.add(OrmPersistentAttributeDetailsProvider.instance());
		providers.add(EntityMappings2_0DetailsProvider.instance());
	}

	// ********** resource ui definitions **********

	@Override
	protected void addResourceUiDefinitionsTo(
			List<ResourceUiDefinition> definitions) {
		definitions.add(PackageInfoResourceUIDefinition.instance());
		definitions.add(Hibernate2_0JavaResourceUiDefinition.instance());
		definitions.add(HibernateOrmXmlUiDefinition.instance());
		definitions.add(Hibernate2_0OrmXmlUiDefinition.instance());
		definitions.add(HibernatePersistenceXmlUiDefinition.instance());
		definitions.add(HibernatePersistenceXml2_0UiDefinition.instance());
	}

}
