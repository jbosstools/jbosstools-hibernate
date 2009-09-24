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

package org.jboss.tools.hibernate.jpt.core.internal.context.orm;

import org.eclipse.jpt.core.context.orm.OrmJoinColumnJoiningStrategy;
import org.eclipse.jpt.core.context.orm.OrmOneToOneMapping;
import org.eclipse.jpt.core.internal.context.orm.GenericOrmOneToOneRelationshipReference;
import org.eclipse.jpt.core.resource.orm.XmlOneToOne;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmOneToOneRelationshipReference extends
		GenericOrmOneToOneRelationshipReference {

	public HibernateOrmOneToOneRelationshipReference(OrmOneToOneMapping parent,
			XmlOneToOne resource) {
		super(parent, resource);
	}
	
	protected OrmJoinColumnJoiningStrategy buildJoinColumnJoiningStrategy() {
		return new HibernateOrmJoinColumnJoiningStrategy(this, getResourceMapping());
	}

}
