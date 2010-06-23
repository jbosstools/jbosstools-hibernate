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

import org.eclipse.jpt.core.context.orm.OrmPersistentAttribute;
import org.eclipse.jpt.core.context.orm.OrmRelationshipReference;
import org.eclipse.jpt.core.internal.jpa2.context.orm.GenericOrmOneToManyMapping2_0;
import org.eclipse.jpt.core.resource.orm.XmlOneToMany;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmOneToManyMapping2_0 extends
		GenericOrmOneToManyMapping2_0 {

	/**
	 * @param parent
	 * @param resourceMapping
	 */
	public HibernateOrmOneToManyMapping2_0(OrmPersistentAttribute parent,
			XmlOneToMany resourceMapping) {
		super(parent, resourceMapping);
	}
	
	@Override
	protected OrmRelationshipReference buildRelationshipReference() {
		return new HibernateOrmOneToManyRelationshipReference2_0(this, this.resourceAttributeMapping);
	}

}
