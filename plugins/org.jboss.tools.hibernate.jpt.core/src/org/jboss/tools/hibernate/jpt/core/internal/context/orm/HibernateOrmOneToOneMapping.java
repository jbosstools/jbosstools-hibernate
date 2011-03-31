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

import org.eclipse.jpt.jpa.core.context.orm.OrmMappingRelationship;
import org.eclipse.jpt.jpa.core.context.orm.OrmPersistentAttribute;
import org.eclipse.jpt.jpa.core.internal.context.orm.AbstractOrmOneToOneMapping;
import org.eclipse.jpt.jpa.core.resource.orm.XmlOneToOne;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmOneToOneMapping extends AbstractOrmOneToOneMapping<XmlOneToOne> {

	public HibernateOrmOneToOneMapping(OrmPersistentAttribute parent, XmlOneToOne xmlMapping) {
		super(parent, xmlMapping);
	}

	@Override
	protected OrmMappingRelationship buildRelationship() {
		return new HibernateOrmOneToOneRelationshipReference(this);
	}

}
