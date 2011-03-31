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

import org.eclipse.jpt.jpa.core.context.orm.OrmPersistentAttribute;
import org.eclipse.jpt.jpa.core.internal.context.orm.AbstractOrmManyToOneMapping;
import org.eclipse.jpt.jpa.core.resource.orm.XmlManyToOne;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmManyToOneMapping2_0<T extends XmlManyToOne>
extends AbstractOrmManyToOneMapping<T> {

	public HibernateOrmManyToOneMapping2_0(OrmPersistentAttribute parent,
			T resourceMapping) {
		super(parent, resourceMapping);
	}

	@Override
	protected OrmRelationshipReference buildRelationshipReference() {
		return new HibernateOrmManyToOneRelationshipReference2_0(this, this.resourceAttributeMapping);
	}

}
