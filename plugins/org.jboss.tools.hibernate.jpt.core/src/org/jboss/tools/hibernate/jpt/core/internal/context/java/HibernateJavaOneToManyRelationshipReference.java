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

import org.eclipse.jpt.jpa.core.context.java.JavaJoinColumnRelationshipStrategy;
import org.eclipse.jpt.jpa.core.context.java.JavaOneToManyMapping;
import org.eclipse.jpt.jpa.core.internal.context.java.GenericJavaOneToManyRelationship;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaOneToManyRelationshipReference extends
		GenericJavaOneToManyRelationship {

	/**
	 * @param parent
	 */
	public HibernateJavaOneToManyRelationshipReference(JavaOneToManyMapping parent, boolean supportsJoinColumnStrategy) {
		super(parent, supportsJoinColumnStrategy);
	}
	
	
	/*@Override
	protected JavaJoinTableJoiningStrategy buildJoinTableJoiningStrategy() {
		return new HibernateJavaJoinTableJoiningStrategy(this);
	}*/

}
