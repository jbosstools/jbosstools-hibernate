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
import org.eclipse.jpt.jpa.ui.internal.AbstractJpaPlatformUiProvider;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.java.HibernateJavaResourceUiDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.java.PackageInfoResourceUIDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.orm.HibernateOrmXmlUiDefinition;
import org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.HibernatePersistenceXmlUiDefinition;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaPlatformUiProvider extends
	AbstractJpaPlatformUiProvider {

	// singleton
	private static final JpaPlatformUiProvider INSTANCE = new HibernateJpaPlatformUiProvider();

	/**
	 * Return the singleton.
	 */
	public static JpaPlatformUiProvider instance() {
		return INSTANCE;
	}

	/**
	 * Ensure single instance.
	 */
	private HibernateJpaPlatformUiProvider() {
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
//	}
	
	// ********** resource ui definitions **********
	
	@Override
	protected void addResourceUiDefinitionsTo(List<ResourceUiDefinition> definitions) {
		definitions.add(PackageInfoResourceUIDefinition.instance());
		definitions.add(HibernateJavaResourceUiDefinition.instance());
		definitions.add(HibernateOrmXmlUiDefinition.instance());
		definitions.add(HibernatePersistenceXmlUiDefinition.instance());
	}

}
