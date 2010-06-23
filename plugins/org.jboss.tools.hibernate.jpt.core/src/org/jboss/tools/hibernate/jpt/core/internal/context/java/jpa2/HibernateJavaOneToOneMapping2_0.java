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

package org.jboss.tools.hibernate.jpt.core.internal.context.java.jpa2;

import org.eclipse.jpt.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.core.jpa2.context.java.JavaOneToOneRelationshipReference2_0;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.AbstractHibernateJavaOneToOneMapping;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaOneToOneMapping2_0 extends AbstractHibernateJavaOneToOneMapping {

	public HibernateJavaOneToOneMapping2_0(JavaPersistentAttribute parent) {
		super(parent);
	}

	@Override
	protected JavaOneToOneRelationshipReference2_0 buildRelationshipReference() {
		return new HibernateJavaOneToOneRelationshipReference2_0(this);
	}


}

