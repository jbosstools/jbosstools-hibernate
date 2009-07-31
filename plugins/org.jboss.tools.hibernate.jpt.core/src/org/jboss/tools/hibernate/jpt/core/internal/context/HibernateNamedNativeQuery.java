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
package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.eclipse.jpt.core.context.java.JavaNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateQuery;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedNativeQueryAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public interface HibernateNamedNativeQuery extends JavaNamedNativeQuery, 
	HibernateQuery {

	String HIBERNATE_NAMED_NATIVE_QUERY="hibernateNamedNativeQuery"; //$NON-NLS-1$
	
	//************************ callable *********************************	
	boolean isCallable();
	Boolean getSpecifiedCallable();
	void setSpecifiedCallable(Boolean value);
		String SPECIFIED_CALLABLE_PROPERTY = "specifiedCallable"; //$NON-NLS-1$
	
	boolean isDefaultCallable();
		boolean DEFAULT_CALLABLE = false;
		String DEFAULT_CALLABLE_PROPERTY = "defaultCallable"; //$NON-NLS-1$
	

	void initialize(HibernateNamedNativeQueryAnnotation resourceNamedQuery);

	void update(HibernateNamedNativeQueryAnnotation resourceNamedQuery);
}
