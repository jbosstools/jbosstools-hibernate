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

import org.eclipse.jpt.core.JpaAnnotationProvider;
import org.eclipse.jpt.core.JpaFactory;
import org.eclipse.jpt.core.JpaPlatform;
import org.eclipse.jpt.core.JpaPlatformFactory;
import org.eclipse.jpt.core.JpaValidation;
import org.eclipse.jpt.core.internal.platform.GenericJpaAnnotationDefinitionProvider;
import org.eclipse.jpt.core.internal.platform.GenericJpaAnnotationProvider;
import org.eclipse.jpt.core.internal.platform.GenericJpaPlatformProvider;

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
	
	public JpaPlatform buildJpaPlatform(String id) {
		return new HibernateJpaPlatform(
			id,
			buildJpaFactory(), 
			buildJpaAnnotationProvider(), 
			buildJpaValidation(),
			GenericJpaPlatformProvider.instance());
	}
	
	protected JpaFactory buildJpaFactory() {
		return new HibernateJpaFactory();
	}
	
	protected JpaAnnotationProvider buildJpaAnnotationProvider() {
		return new GenericJpaAnnotationProvider(
			GenericJpaAnnotationDefinitionProvider.instance(),
			HibernateJpaAnnotationDefinitionProvider.instance());
	}
	
	protected JpaValidation buildJpaValidation() {
		return new JpaValidation() {
			public Supported getTablePerConcreteClassInheritanceIsSupported() {
				return Supported.MAYBE;
			}
		};
	}

}
