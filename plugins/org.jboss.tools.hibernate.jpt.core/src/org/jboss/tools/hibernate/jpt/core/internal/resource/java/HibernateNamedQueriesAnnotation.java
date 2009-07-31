/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.resource.java;

import org.eclipse.jpt.core.resource.java.ContainerAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public interface HibernateNamedQueriesAnnotation extends
		ContainerAnnotation<HibernateNamedQueryAnnotation> {
	
	String ANNOTATION_NAME = Hibernate.NAMED_QUERIES;

	String HIBERNATE_NAMED_QUERIES_LIST = "hibernateNamedQueries"; //$NON-NLS-1$
}
