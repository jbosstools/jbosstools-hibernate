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

import org.eclipse.jpt.jpa.core.context.JoinColumn;
import org.eclipse.jpt.jpa.core.context.orm.EntityMappings;
import org.eclipse.jpt.jpa.core.context.orm.OrmBasicMapping;
import org.eclipse.jpt.jpa.core.context.orm.OrmEntity;
import org.eclipse.jpt.jpa.core.context.orm.OrmIdMapping;
import org.eclipse.jpt.jpa.core.context.orm.OrmManyToManyMapping;
import org.eclipse.jpt.jpa.core.context.orm.OrmManyToOneMapping;
import org.eclipse.jpt.jpa.core.context.orm.OrmOneToManyMapping;
import org.eclipse.jpt.jpa.core.context.orm.OrmOneToOneMapping;
import org.eclipse.jpt.jpa.core.context.orm.OrmPersistentType;
import org.eclipse.jpt.jpa.core.context.orm.OrmSpecifiedColumn;
import org.eclipse.jpt.jpa.core.context.orm.OrmSpecifiedJoinColumn;
import org.eclipse.jpt.jpa.core.context.orm.OrmSpecifiedJoinTable;
import org.eclipse.jpt.jpa.core.context.orm.OrmSpecifiedPersistentAttribute;
import org.eclipse.jpt.jpa.core.context.orm.OrmTable;
import org.eclipse.jpt.jpa.core.internal.context.orm.GenericOrmXmlContextModelFactory;
import org.eclipse.jpt.jpa.core.resource.orm.XmlBasic;
import org.eclipse.jpt.jpa.core.resource.orm.XmlEntity;
import org.eclipse.jpt.jpa.core.resource.orm.XmlId;
import org.eclipse.jpt.jpa.core.resource.orm.XmlJoinColumn;
import org.eclipse.jpt.jpa.core.resource.orm.XmlManyToMany;
import org.eclipse.jpt.jpa.core.resource.orm.XmlManyToOne;
import org.eclipse.jpt.jpa.core.resource.orm.XmlOneToMany;
import org.eclipse.jpt.jpa.core.resource.orm.XmlOneToOne;
import org.eclipse.jpt.jpa.core.resource.orm.XmlTypeMapping;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmXmlContextNodeFactory extends GenericOrmXmlContextModelFactory {

	@Override
	public OrmBasicMapping buildOrmBasicMapping(OrmSpecifiedPersistentAttribute parent,
			XmlBasic resourceMapping) {
		return new HibernateOrmBasicMapping(parent, resourceMapping);
	}

	@Override
	public OrmIdMapping buildOrmIdMapping(OrmSpecifiedPersistentAttribute parent,
			XmlId resourceMapping) {
		return new HibernateOrmIdMappingImpl(parent, resourceMapping);
	}

	@Override
	public OrmEntity buildOrmEntity(OrmPersistentType parent,
			XmlEntity resourceMapping) {
		return new HibernateOrmEntityImpl(parent, resourceMapping);
	}

	@Override
	public OrmTable buildOrmTable(OrmTable.ParentAdapter parentAdapter) {
		return new HibernateOrmTableImpl(parentAdapter);
	}

	@Override
	public OrmSpecifiedJoinTable buildOrmJoinTable(OrmSpecifiedJoinTable.ParentAdapter parentAdapter) {
		return new HibernateOrmJoinTableImpl(parentAdapter);
	}

	@Override
	public OrmSpecifiedColumn buildOrmColumn(OrmSpecifiedColumn.ParentAdapter parentAdapter) {
		return new HibernateOrmColumnImpl(parentAdapter);
	}

	@Override
	public OrmSpecifiedJoinColumn buildOrmJoinColumn(JoinColumn.ParentAdapter parentAdapter, XmlJoinColumn xmlJoinColumn) {
		return new HibernateOrmJoinColumnImpl(parentAdapter, xmlJoinColumn);
	}

	@Override
	public OrmManyToOneMapping buildOrmManyToOneMapping(OrmSpecifiedPersistentAttribute parent, XmlManyToOne resourceMapping) {
		return new HibernateOrmManyToOneMapping(parent,resourceMapping);
	}

	@Override
	public OrmOneToOneMapping buildOrmOneToOneMapping(OrmSpecifiedPersistentAttribute parent, XmlOneToOne resourceMapping) {
		return new HibernateOrmOneToOneMapping(parent, resourceMapping);
	}

	@Override
	public OrmOneToManyMapping buildOrmOneToManyMapping(
			OrmSpecifiedPersistentAttribute parent, XmlOneToMany resourceMapping) {
		return new HibernateOrmOneToManyMapping(parent, resourceMapping);
	}

	@Override
	public OrmManyToManyMapping buildOrmManyToManyMapping(
			OrmSpecifiedPersistentAttribute parent, XmlManyToMany resourceMapping) {
		return new HibernateOrmManyToManyMapping(parent, resourceMapping);
	}

	@Override
	public OrmPersistentType buildOrmPersistentType(EntityMappings parent,
			XmlTypeMapping resourceMapping) {
		return new HibernateOrmPersistentType(parent, resourceMapping);
	}

}
