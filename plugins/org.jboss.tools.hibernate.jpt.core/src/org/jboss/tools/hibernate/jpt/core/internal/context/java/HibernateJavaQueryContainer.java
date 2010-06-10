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

import org.eclipse.jpt.core.context.java.JavaQueryContainer;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;

/**
 * 
 * @author Dmitry Geraskov
 *
 */
public interface HibernateJavaQueryContainer extends JavaQueryContainer {

	//********** Hibernate Named Queries **************	
	ListIterator<HibernateNamedQuery> hibernateNamedQueries();
	
	int hibernateNamedQueriesSize();

	HibernateNamedQuery addHibernateNamedQuery(int index);
	
	void removeHibernateNamedQuery(int index);

	void removeHibernateNamedQuery(HibernateNamedQuery namedQuery);

	void moveHibernateNamedQuery(int targetIndex, int sourceIndex);

	String HIBERNATE_NAMED_QUERIES_LIST = "hibernateNamedQueries"; //$NON-NLS-1$
	
	//********** Hibernate Named Native Queries **************;

	ListIterator<HibernateNamedNativeQuery> hibernateNamedNativeQueries();
	
	int hibernateNamedNativeQueriesSize();

	HibernateNamedNativeQuery addHibernateNamedNativeQuery(int index);
	
	void removeHibernateNamedNativeQuery(int index);

	void removeHibernateNamedNativeQuery(HibernateNamedNativeQuery namedQuery);

	void moveHibernateNamedNativeQuery(int targetIndex, int sourceIndex);

	String HIBERNATE_NAMED_NATIVE_QUERIES_LIST = "hibernateNamedQueries"; //$NON-NLS-1$
	
	
	void initialize(JavaResourcePersistentMember jrpm);
	
	/**
	 * Update the JavaGeneratorContainer context model object to match the JavaResourcePersistentMember 
	 * resource model object. see {@link org.eclipse.jpt.core.JpaProject#update()}
	 */
	void update(JavaResourcePersistentMember jrpm);


}
