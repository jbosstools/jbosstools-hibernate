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

import org.eclipse.jpt.jpa.core.context.java.JavaMappingRelationship;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.java.GenericJavaOneToManyMapping;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaOneToManyMapping extends GenericJavaOneToManyMapping {

	/**
	 * @param parent
	 */
	public HibernateJavaOneToManyMapping(JavaPersistentAttribute parent) {
		super(parent);
	}

	@Override
	protected JavaMappingRelationship buildRelationship() {
		return new HibernateJavaOneToManyRelationship(this, this.isJpa2_0Compatible());
	}

}
