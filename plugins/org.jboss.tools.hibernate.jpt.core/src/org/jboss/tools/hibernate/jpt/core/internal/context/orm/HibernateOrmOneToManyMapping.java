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

import org.eclipse.jpt.core.context.orm.OrmPersistentAttribute;
import org.eclipse.jpt.core.context.orm.OrmRelationshipReference;
import org.eclipse.jpt.core.internal.jpa1.context.orm.GenericOrmOneToManyMapping;
import org.eclipse.jpt.core.resource.orm.XmlOneToMany;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmOneToManyMapping extends GenericOrmOneToManyMapping {

	/**
	 * @param parent
	 * @param resourceMapping
	 */
	public HibernateOrmOneToManyMapping(OrmPersistentAttribute parent,
			XmlOneToMany resourceMapping) {
		super(parent, resourceMapping);
	}
	
	@Override
	protected OrmRelationshipReference buildRelationshipReference() {
		return new HibernateOrmOneToManyRelationshipReference(this, this.resourceAttributeMapping);
	}

}
