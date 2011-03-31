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
package org.jboss.tools.hibernate.jpt.core.internal.context.java.jpa2;

import org.eclipse.jpt.jpa.core.context.java.JavaPersistentAttribute;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaOneToManyMapping2_0 extends
		GenericJavaOneToManyMapping2_0 {

	/**
	 * @param parent
	 */
	public HibernateJavaOneToManyMapping2_0(JavaPersistentAttribute parent) {
		super(parent);
	}
	
	@Override
	protected JavaRelationshipReference buildRelationshipReference() {
		return new HibernateJavaOneToManyRelationshipReference2_0(this);
	}

}
