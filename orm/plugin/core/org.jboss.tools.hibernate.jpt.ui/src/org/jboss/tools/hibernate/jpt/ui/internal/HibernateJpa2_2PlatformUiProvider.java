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
package org.jboss.tools.hibernate.jpt.ui.internal;

import java.util.List;

import org.eclipse.jpt.jpa.ui.JpaPlatformUiProvider;
import org.eclipse.jpt.jpa.ui.ResourceUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.AbstractJpaPlatformUiProvider;
import org.jboss.tools.hibernate.jpt.ui.internal.jpa2.mapping.details.orm.Hibernate2_0OrmXmlUiDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.jpa2.mapping.details.orm.Hibernate2_1OrmXmlUiDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.jpa2.mapping.details.orm.Hibernate2_2OrmXmlUiDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.java.Hibernate2_0JavaResourceUiDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.java.PackageInfoResourceUIDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.orm.HibernateOrmXmlUiDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.HibernatePersistenceXmlUiDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.jpa2.HibernatePersistenceXml2_0UiDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.jpa2.HibernatePersistenceXml2_1UiDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.jpa2.HibernatePersistenceXml2_2UiDefinition;

/**
 * @author Koen Aers, jkopriva@redhat.com
 * 
 */
public class HibernateJpa2_2PlatformUiProvider extends
		AbstractJpaPlatformUiProvider {

	// singleton
	private static final JpaPlatformUiProvider INSTANCE = new HibernateJpa2_2PlatformUiProvider();

	/**
	 * Return the singleton.
	 */
	public static JpaPlatformUiProvider instance() {
		return INSTANCE;
	}

	/**
	 * Ensure single instance.
	 */
	private HibernateJpa2_2PlatformUiProvider() {
		super();
	}

	// ********** details providers **********

//	@Override
//	protected void addDetailsProvidersTo(List<JpaDetailsProvider> providers) {
//		providers.add(JavaPackageInfoDetailsProvider.instance());
//		providers.add(JavaPersistentTypeDetailsProvider.instance());
//		providers.add(JavaPersistentAttributeDetailsProvider.instance());
//		providers.add(EntityMappingsDetailsProvider.instance());
//		providers.add(OrmPersistentTypeDetailsProvider.instance());
//		providers.add(OrmPersistentAttributeDetailsProvider.instance());
//		providers.add(EntityMappings2_0DetailsProvider.instance());
//	}

	// ********** resource ui definitions **********

	@Override
	protected void addResourceUiDefinitionsTo(
			List<ResourceUiDefinition> definitions) {
		definitions.add(PackageInfoResourceUIDefinition.instance());
		definitions.add(Hibernate2_0JavaResourceUiDefinition.instance());
		definitions.add(HibernateOrmXmlUiDefinition.instance());
		definitions.add(Hibernate2_0OrmXmlUiDefinition.instance());
		definitions.add(Hibernate2_1OrmXmlUiDefinition.instance());
		definitions.add(Hibernate2_2OrmXmlUiDefinition.instance());
		definitions.add(HibernatePersistenceXmlUiDefinition.instance());
		definitions.add(HibernatePersistenceXml2_0UiDefinition.instance());
		definitions.add(HibernatePersistenceXml2_1UiDefinition.instance());
		definitions.add(HibernatePersistenceXml2_2UiDefinition.instance());
	}

}
