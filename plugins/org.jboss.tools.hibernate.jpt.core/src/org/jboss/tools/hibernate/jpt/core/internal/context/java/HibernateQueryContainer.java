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

import java.util.ListIterator;

import org.eclipse.jpt.core.context.QueryContainer;

/**
 * @author Dmitry Geraskov
 *
 */
public interface HibernateQueryContainer extends QueryContainer {
	
	String HIBERNATE_NAMED_QUERIES_LIST = "hibernateNamedQueries"; //$NON-NLS-1$
	
	String HIBERNATE_NAMED_NATIVE_QUERIES_LIST = "hibernateNamedNativeQueries"; //$NON-NLS-1$
	// ********** named queries **********

	/**
	 * Return a list iterator of the named queries.
	 * This will not be null.
	 */
	<T extends HibernateNamedQuery> ListIterator<T> hibernateNamedQueries();

	/**
	 * Return the number of named queries.
	 */
	int hibernateNamedQueriesSize();

	/**
	 * Add a named query to the entity return the object representing it.
	 */
	HibernateNamedQuery addHibernateNamedQuery(int index);

	/**
	 * Remove the named query at the index from the entity.
	 */
	void removeHibernateNamedQuery(int index);

	/**
	 * Remove the named query at from the entity.
	 */
	void removeHibernateNamedQuery(HibernateNamedQuery namedQuery);

	/**
	 * Move the named query from the source index to the target index.
	 */
	void moveHibernateNamedQuery(int targetIndex, int sourceIndex);
	
	// ********** named native queries **********

	/**
	 * Return a list iterator of the specified named native queries.
	 * This will not be null.
	 */
	<T extends HibernateNamedNativeQuery> ListIterator<T> hibernateNamedNativeQueries();

	/**
	 * Return the number of named native queries.
	 */
	int hibernateNamedNativeQueriesSize();

	/**
	 * Add a named native query to the entity return the object representing it.
	 */
	HibernateNamedNativeQuery addHibernateNamedNativeQuery(int index);

	/**
	 * Remove the named native query at the index from the entity.
	 */
	void removeHibernateNamedNativeQuery(int index);

	/**
	 * Remove the named native query at from the entity.
	 */
	void removeHibernateNamedNativeQuery(HibernateNamedNativeQuery namedNativeQuery);

	/**
	 * Move the named native query from the source index to the target index.
	 */
	void moveHibernateNamedNativeQuery(int targetIndex, int sourceIndex);


}
