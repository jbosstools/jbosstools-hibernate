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

import org.eclipse.jpt.jpa.core.context.java.JavaManyToOneMapping;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaManyToOneRelationshipReference extends
		GenericJavaManyToOneRelationshipReference {

	public HibernateJavaManyToOneRelationshipReference(
			JavaManyToOneMapping parent) {
		super(parent);
	}
	
	@Override
	protected JavaJoinColumnJoiningStrategy buildJoinColumnJoiningStrategy() {
		return new HibernateJavaJoinColumnJoiningStrategy(this);
	}

}
