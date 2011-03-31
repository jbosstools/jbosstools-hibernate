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
package org.jboss.tools.hibernate.jpt.core.internal.context.orm;

import org.eclipse.jpt.jpa.core.context.orm.OrmJoinTableRelationshipStrategy;
import org.eclipse.jpt.jpa.core.context.orm.OrmOneToManyMapping;
import org.eclipse.jpt.jpa.core.internal.context.orm.GenericOrmOneToManyRelationship;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmOneToManyRelationshipReference extends
		GenericOrmOneToManyRelationship {

	/**
	 * @param parent
	 * @param resource
	 */
	public HibernateOrmOneToManyRelationshipReference(OrmOneToManyMapping parent, boolean supportsJoinColumnStrategy) {
		super(parent, supportsJoinColumnStrategy);
	}

	@Override
	protected OrmJoinTableRelationshipStrategy buildJoinTableStrategy() {
		return new HibernateOrmJoinTableJoiningStrategy(this);
	}

}
