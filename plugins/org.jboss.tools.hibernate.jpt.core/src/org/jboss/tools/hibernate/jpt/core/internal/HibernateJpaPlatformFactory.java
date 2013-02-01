/*******************************************************************************
 * Copyright (c) 2008-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal;

import org.eclipse.jpt.common.core.JptResourceType;
import org.eclipse.jpt.jpa.core.JpaFactory;
import org.eclipse.jpt.jpa.core.JpaPlatform;
import org.eclipse.jpt.jpa.core.JpaPlatformFactory;
import org.eclipse.jpt.jpa.core.JpaPlatformVariation;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.context.AccessType;
import org.eclipse.jpt.jpa.core.internal.GenericJpaAnnotationDefinitionProvider;
import org.eclipse.jpt.jpa.core.internal.GenericJpaPlatformFactory.GenericJpaPlatformVersion;
import org.eclipse.jpt.jpa.core.internal.JpaAnnotationProvider;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar1_0;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaPlatformFactory implements JpaPlatformFactory {
	
	/**
	 * zero-argument constructor
	 */
	public HibernateJpaPlatformFactory() {
		super();
	}

	public JpaPlatform buildJpaPlatform(JpaPlatform.Config config) {
		return new HibernateJpaPlatform(
				config,
				this.buildJpaVersion(),
				buildJpaFactory(),
				buildJpaAnnotationProvider(),
				HibernateJpaPlatformProvider.instance(),
				this.buildJpaPlatformVariation(),
				JPQLGrammar1_0.instance());
	}



	private JpaPlatform.Version buildJpaVersion() {
		return new GenericJpaPlatformVersion(JpaProject.FACET_VERSION_STRING);
	}

	protected JpaFactory buildJpaFactory() {
		return new HibernateJpaFactory();
	}

	protected JpaAnnotationProvider buildJpaAnnotationProvider() {
		return new JpaAnnotationProvider(GenericJpaAnnotationDefinitionProvider.instance(),
				HibernateJpaAnnotationDefinitionProvider.instance());
	}

	protected JpaPlatformVariation buildJpaPlatformVariation() {
		return new JpaPlatformVariation() {
			public Supported getTablePerConcreteClassInheritanceIsSupported() {
				return Supported.YES;
			}
			public boolean isJoinTableOverridable() {
				return false;
			}
			@Override
			public AccessType[] getSupportedAccessTypes(
					JptResourceType resourceType) {
				return GENERIC_SUPPORTED_ACCESS_TYPES;
			}
		};
	}

}
