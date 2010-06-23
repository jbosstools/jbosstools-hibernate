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

import org.eclipse.jpt.core.context.orm.OrmJoinTableJoiningStrategy;
import org.eclipse.jpt.core.context.orm.OrmManyToManyMapping;
import org.eclipse.jpt.core.internal.context.orm.GenericOrmManyToManyRelationshipReference;
import org.eclipse.jpt.core.resource.orm.XmlManyToMany;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmManyToManyRelationshipReference extends
		GenericOrmManyToManyRelationshipReference {

	/**
	 * @param parent
	 * @param resource
	 */
	public HibernateOrmManyToManyRelationshipReference(
			OrmManyToManyMapping parent, XmlManyToMany resource) {
		super(parent, resource);
	}
	
	@Override
	protected OrmJoinTableJoiningStrategy buildJoinTableJoiningStrategy() {
		return 	new HibernateOrmJoinTableJoiningStrategy(this, getResourceMapping());
	}

}
