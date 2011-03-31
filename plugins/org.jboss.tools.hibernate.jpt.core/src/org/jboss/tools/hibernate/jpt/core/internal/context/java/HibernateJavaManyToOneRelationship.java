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

package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jpt.jpa.core.context.java.JavaJoinColumnRelationshipStrategy;
import org.eclipse.jpt.jpa.core.context.java.JavaManyToOneMapping;
import org.eclipse.jpt.jpa.core.internal.context.java.GenericJavaManyToOneRelationship;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaManyToOneRelationship extends GenericJavaManyToOneRelationship {

	public HibernateJavaManyToOneRelationship(
			JavaManyToOneMapping parent) {
		super(parent);
	}

	@Override
	protected JavaJoinColumnRelationshipStrategy buildJoinColumnStrategy() {
		return new HibernateJavaJoinColumnRelationshipStrategy(this);
	}

}
