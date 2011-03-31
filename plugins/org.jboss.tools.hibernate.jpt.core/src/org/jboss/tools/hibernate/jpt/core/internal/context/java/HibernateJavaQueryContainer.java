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
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.ListIterator;

import org.eclipse.jpt.jpa.core.context.java.JavaQueryContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;

/**
 *
 * @author Dmitry Geraskov
 *
 */
public interface HibernateJavaQueryContainer extends JavaQueryContainer {

	//********** Hibernate Named Queries **************
	ListIterator<HibernateJavaNamedQuery> hibernateNamedQueries();

	int hibernateNamedQueriesSize();

	HibernateNamedQuery addHibernateNamedQuery(int index);

	HibernateNamedQuery addHibernateNamedQuery();

	void removeHibernateNamedQuery(int index);

	void removeHibernateNamedQuery(HibernateNamedQuery namedQuery);

	void moveHibernateNamedQuery(int targetIndex, int sourceIndex);

	String HIBERNATE_NAMED_QUERIES_LIST = "hibernateNamedQueries"; //$NON-NLS-1$

	//********** Hibernate Named Native Queries **************;

	ListIterator<HibernateJavaNamedNativeQuery> hibernateNamedNativeQueries();

	int hibernateNamedNativeQueriesSize();

	HibernateJavaNamedNativeQuery addHibernateNamedNativeQuery(int index);

	void removeHibernateNamedNativeQuery(int index);

	void removeHibernateNamedNativeQuery(HibernateNamedNativeQuery namedQuery);

	void moveHibernateNamedNativeQuery(int targetIndex, int sourceIndex);

	String HIBERNATE_NAMED_NATIVE_QUERIES_LIST = "hibernateNamedNativeQueries"; //$NON-NLS-1$

}
