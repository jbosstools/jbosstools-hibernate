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
package org.jboss.tools.hibernate.jpt.core.internal.context.orm.jpa2;

import org.eclipse.jpt.core.context.orm.OrmJoinTableJoiningStrategy;
import org.eclipse.jpt.core.context.orm.OrmOneToManyMapping;
import org.eclipse.jpt.core.internal.jpa2.context.orm.GenericOrmOneToManyRelationshipReference2_0;
import org.eclipse.jpt.core.resource.orm.XmlOneToMany;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmJoinTableJoiningStrategy;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmOneToManyRelationshipReference2_0 extends
		GenericOrmOneToManyRelationshipReference2_0 {

	/**
	 * @param parent
	 * @param resource
	 */
	public HibernateOrmOneToManyRelationshipReference2_0(
			OrmOneToManyMapping parent, XmlOneToMany resource) {
		super(parent, resource);
	}
	
	@Override
	protected OrmJoinTableJoiningStrategy buildJoinTableJoiningStrategy() {
		return new HibernateOrmJoinTableJoiningStrategy(this, getResourceMapping());
	}

}
