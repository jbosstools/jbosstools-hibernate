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

import org.eclipse.jpt.jpa.core.context.orm.OrmMappingJoinTableRelationship;
import org.eclipse.jpt.jpa.core.internal.context.orm.GenericOrmMappingJoinTableRelationshipStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.context.NamingStrategyMappingTools;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmJoinTableJoiningStrategy extends
		GenericOrmMappingJoinTableRelationshipStrategy {

	/**
	 * @param parent
	 * @param resource
	 */
	public HibernateOrmJoinTableJoiningStrategy(
			OrmMappingJoinTableRelationship parent) {
		super(parent);
	}

	@Override
	public String getJoinTableDefaultName() {
		return NamingStrategyMappingTools
				.buildJoinTableDefaultName(getRelationship());
	}

}
