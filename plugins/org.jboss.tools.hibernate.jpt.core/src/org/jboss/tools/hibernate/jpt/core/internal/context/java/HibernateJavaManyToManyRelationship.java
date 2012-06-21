/*******************************************************************************
 * Copyright (c) 2010-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jpt.jpa.core.context.java.JavaManyToManyMapping;
import org.eclipse.jpt.jpa.core.internal.context.java.GenericJavaManyToManyRelationship;
import org.eclipse.jpt.jpa.core.jpa2.context.java.JavaMappingJoinTableRelationshipStrategy2_0;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaManyToManyRelationship extends GenericJavaManyToManyRelationship {

	/**
	 * @param parent
	 */
	public HibernateJavaManyToManyRelationship(
			JavaManyToManyMapping parent) {
		super(parent);
	}

	@Override
	protected JavaMappingJoinTableRelationshipStrategy2_0 buildJoinTableStrategy() {
		return new HibernateJavaJoinTableRelationshipStrategy(this);
	}

}
