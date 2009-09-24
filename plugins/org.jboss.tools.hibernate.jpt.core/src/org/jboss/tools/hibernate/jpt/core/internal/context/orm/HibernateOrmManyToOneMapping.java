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

import org.eclipse.jpt.core.context.orm.OrmPersistentAttribute;
import org.eclipse.jpt.core.context.orm.OrmRelationshipReference;
import org.eclipse.jpt.core.internal.context.orm.GenericOrmManyToOneMapping;
import org.eclipse.jpt.core.resource.orm.XmlManyToOne;
import org.jboss.tools.hibernate.jpt.core.internal.context.NamingStrategyMappingTools;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmManyToOneMapping<T extends XmlManyToOne>
extends GenericOrmManyToOneMapping<T> {

	public HibernateOrmManyToOneMapping(OrmPersistentAttribute parent,
			T resourceMapping) {
		super(parent, resourceMapping);
	}
	
	@Override
	public String getJoinTableDefaultName() {
		return NamingStrategyMappingTools.buildJoinTableDefaultName(this);
	}

	@Override
	protected OrmRelationshipReference buildRelationshipReference() {
		return new HibernateOrmManyToOneRelationshipReference(this, this.resourceAttributeMapping);
	}

}
