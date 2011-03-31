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
import org.eclipse.jpt.jpa.core.JpaAnnotationProvider;
import org.eclipse.jpt.jpa.core.JpaFactory;
import org.eclipse.jpt.jpa.core.JpaPlatformProvider;
import org.eclipse.jpt.jpa.core.JpaPlatformVariation;
import org.eclipse.jpt.jpa.core.ResourceDefinition;
import org.eclipse.jpt.jpa.core.internal.GenericJpaPlatform;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaPlatform extends GenericJpaPlatform {

	public static final String HIBERNATE_PLATFORM_ID = "hibernate"; //$NON-NLS-1$

	public static final String HIBERNATE2_0_PLATFORM_ID = "hibernate2_0"; //$NON-NLS-1$

	@Override
	public ResourceDefinition getResourceDefinition(JptResourceType resourceType) {
		for (ResourceDefinition resourceDefinition : getPlatformProvider().getResourceDefinitions()) {
			if (resourceDefinition.getResourceType().equals(resourceType)) {
				return resourceDefinition;
			}
		}
		return super.getResourceDefinition(resourceType);
	}

	public HibernateJpaPlatform(String id, Version jpaVersion, JpaFactory jpaFactory, JpaAnnotationProvider jpaAnnotationProvider, JpaPlatformProvider platformProvider, JpaPlatformVariation jpaVariation) {
		super(id, jpaVersion, jpaFactory, jpaAnnotationProvider, platformProvider, jpaVariation);
	}

}
