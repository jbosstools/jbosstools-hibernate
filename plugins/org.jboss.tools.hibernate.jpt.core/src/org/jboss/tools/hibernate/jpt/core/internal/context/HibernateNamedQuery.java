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

import org.eclipse.jpt.core.context.java.JavaNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedQueryAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public interface HibernateNamedQuery extends JavaNamedQuery, HibernateQuery {
	
	String HIBERNATE_NAMED_QUERY="hibernateNamedQuery"; //$NON-NLS-1$
		
	void initialize(HibernateNamedQueryAnnotation resourceNamedQuery);

	void update(HibernateNamedQueryAnnotation resourceNamedQuery);
}
