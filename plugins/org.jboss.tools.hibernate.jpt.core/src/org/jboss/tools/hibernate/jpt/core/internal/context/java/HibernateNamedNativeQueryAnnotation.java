/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jpt.core.resource.java.NestableNamedNativeQueryAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 * Corresponds to the Hibernate annotation
 * org.hibernate.annotations.NamedNativeQuery
 */
public interface HibernateNamedNativeQueryAnnotation 
	extends NestableNamedNativeQueryAnnotation, HibernateQueryAnnotation {
	//replace with Hibernate annotation
	String ANNOTATION_NAME = Hibernate.NAMED_NATIVE_QUERY;
	
	//************************ callable *********************************	
	Boolean isCallable();
	void setCallable(Boolean value);
		String CALLABLE_PROPERTY = "callable"; //$NON-NLS-1$

}
