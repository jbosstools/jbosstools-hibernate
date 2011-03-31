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

package org.jboss.tools.hibernate.jpt.core.internal.context.orm.jpa2;

import org.eclipse.jpt.jpa.core.context.orm.OrmManyToOneMapping;
import org.eclipse.jpt.jpa.core.resource.orm.XmlManyToOne;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmJoinColumnJoiningStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmJoinTableJoiningStrategy;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmManyToOneRelationshipReference2_0 extends
		GenericOrmManyToOneRelationshipReference2_0 {

	public HibernateOrmManyToOneRelationshipReference2_0(
			OrmManyToOneMapping parent, XmlManyToOne resource) {
		super(parent, resource);
	}
	
	@Override
	protected OrmJoinColumnJoiningStrategy buildJoinColumnJoiningStrategy() {
		return new HibernateOrmJoinColumnJoiningStrategy(this,  getResourceMapping());
	}
	
	@Override
	protected OrmJoinTableJoiningStrategy buildJoinTableJoiningStrategy() {
		return new HibernateOrmJoinTableJoiningStrategy(this, getResourceMapping());
	}

}
