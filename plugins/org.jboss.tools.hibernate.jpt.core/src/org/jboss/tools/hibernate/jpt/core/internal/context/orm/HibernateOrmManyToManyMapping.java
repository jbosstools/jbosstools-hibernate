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

import org.eclipse.jpt.jpa.core.context.orm.OrmMappingRelationship;
import org.eclipse.jpt.jpa.core.context.orm.OrmPersistentAttribute;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.orm.GenericOrmManyToManyMapping;
import org.eclipse.jpt.jpa.core.resource.orm.XmlManyToMany;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmManyToManyMapping extends GenericOrmManyToManyMapping {

	/**
	 * @param parent
	 * @param resourceMapping
	 */
	public HibernateOrmManyToManyMapping(OrmPersistentAttribute parent,
			XmlManyToMany resourceMapping) {
		super(parent, resourceMapping);
	}

	@Override
	protected OrmMappingRelationship buildRelationship() {
		return new HibernateOrmManyToManyRelationshipReference(this);
	}

}
