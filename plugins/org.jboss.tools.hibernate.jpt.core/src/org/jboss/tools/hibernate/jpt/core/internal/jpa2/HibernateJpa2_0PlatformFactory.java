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
package org.jboss.tools.hibernate.jpt.core.internal.jpa2;

import org.eclipse.jpt.common.core.JptResourceType;
import org.eclipse.jpt.jpa.core.JpaFactory;
import org.eclipse.jpt.jpa.core.JpaPlatform;
import org.eclipse.jpt.jpa.core.JpaPlatformFactory;
import org.eclipse.jpt.jpa.core.JpaPlatformVariation;
import org.eclipse.jpt.jpa.core.context.AccessType;
import org.eclipse.jpt.jpa.core.internal.GenericJpaPlatformFactory.GenericJpaPlatformVersion;
import org.eclipse.jpt.jpa.core.internal.JpaAnnotationProvider;
import org.eclipse.jpt.jpa.core.internal.jpa2.Generic2_0JpaAnnotationDefinitionProvider;
import org.eclipse.jpt.jpa.core.jpa2.JpaProject2_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_0;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaPlatform;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpa2_0PlatformFactory implements JpaPlatformFactory {

	/**
	 * zero-argument constructor
	 */
	public HibernateJpa2_0PlatformFactory() {
		super();
	}
	
	public JpaPlatform buildJpaPlatform(JpaPlatform.Config config) {
		return new HibernateJpaPlatform(
			config,
			this.buildJpaVersion(),
			this.buildJpaFactory(), 
			this.buildJpaAnnotationProvider(), 
			HibernateJpa2_0PlatformProvider.instance(),
			this.buildJpaPlatformVariation(),
			JPQLGrammar2_0.instance());
	}
	
	
	
	private JpaPlatform.Version buildJpaVersion() {
		return new GenericJpaPlatformVersion(JpaProject2_0.FACET_VERSION_STRING);
	}
	
	protected JpaFactory buildJpaFactory() {
		return new HibernateJpaFactory2_0();
	}
	
	protected JpaAnnotationProvider buildJpaAnnotationProvider() {
		return new JpaAnnotationProvider(
			Generic2_0JpaAnnotationDefinitionProvider.instance(),
			HibernateJpa2_0AnnotationDefinitionProvider.instance());
	}
	
	protected JpaPlatformVariation buildJpaPlatformVariation() {
		return new JpaPlatformVariation() {
			//table_per_class inheritance support is optional in the 2.0 spec
			public Supported getTablePerConcreteClassInheritanceIsSupported() {
				return Supported.MAYBE;
			}
			public boolean isJoinTableOverridable() {
				return true;
			}
			@Override
			public AccessType[] getSupportedAccessTypes(
					JptResourceType resourceType) {
				return GENERIC_SUPPORTED_ACCESS_TYPES;
			}
		};
	}

}
