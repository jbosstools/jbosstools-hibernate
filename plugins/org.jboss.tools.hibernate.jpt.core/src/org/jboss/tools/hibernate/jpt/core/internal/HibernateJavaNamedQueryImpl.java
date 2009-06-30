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
package org.jboss.tools.hibernate.jpt.core.internal;

import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.context.java.GenericJavaNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaNamedQuery;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaNamedQueryImpl extends GenericJavaNamedQuery
	implements HibernateJavaNamedQuery {

	/**
	 * @param parent
	 */
	public HibernateJavaNamedQueryImpl(JavaJpaContextNode parent) {
		super(parent);
	}


}
