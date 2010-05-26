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
import org.eclipse.jpt.core.JpaPlatformProvider;
import org.eclipse.jpt.core.JpaValidation;
import org.eclipse.jpt.core.internal.platform.GenericJpaPlatform;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaPlatform extends GenericJpaPlatform {
	
	public static final String ID = "hibernate"; //$NON-NLS-1$

	public HibernateJpaPlatform(String id, JpaFactory jpaFactory, JpaAnnotationProvider jpaAnnotationProvider, JpaValidation jpaValidation, JpaPlatformProvider... platformProviders) {
		super(id, jpaFactory, jpaAnnotationProvider, jpaValidation, platformProviders);
	}

	@Override
	public String getId() {
		return ID;
	}
	
}
