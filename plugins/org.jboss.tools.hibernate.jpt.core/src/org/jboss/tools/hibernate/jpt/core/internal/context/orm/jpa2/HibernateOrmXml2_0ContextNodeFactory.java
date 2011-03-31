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

import org.eclipse.jpt.jpa.core.context.Table;
import org.eclipse.jpt.jpa.core.context.XmlContextNode;
import org.eclipse.jpt.jpa.core.context.orm.EntityMappings;
import org.eclipse.jpt.jpa.core.context.orm.OrmBasicMapping;
import org.eclipse.jpt.jpa.core.context.orm.OrmColumn;
import org.eclipse.jpt.jpa.core.context.orm.OrmEntity;
import org.eclipse.jpt.jpa.core.context.orm.OrmIdMapping;
import org.eclipse.jpt.jpa.core.context.orm.OrmJoinColumn;
import org.eclipse.jpt.jpa.core.context.orm.OrmJoinTable;
import org.eclipse.jpt.jpa.core.context.orm.OrmJoinTableRelationshipStrategy;
import org.eclipse.jpt.jpa.core.context.orm.OrmManyToManyMapping;
import org.eclipse.jpt.jpa.core.context.orm.OrmManyToOneMapping;
import org.eclipse.jpt.jpa.core.context.orm.OrmOneToManyMapping;
import org.eclipse.jpt.jpa.core.context.orm.OrmOneToOneMapping;
import org.eclipse.jpt.jpa.core.context.orm.OrmPersistentAttribute;
import org.eclipse.jpt.jpa.core.context.orm.OrmPersistentType;
import org.eclipse.jpt.jpa.core.context.orm.OrmTable;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.orm.GenericOrmXml2_0ContextNodeFactory;
import org.eclipse.jpt.jpa.core.resource.orm.XmlBasic;
import org.eclipse.jpt.jpa.core.resource.orm.XmlEntity;
import org.eclipse.jpt.jpa.core.resource.orm.XmlId;
import org.eclipse.jpt.jpa.core.resource.orm.XmlJoinColumn;
import org.eclipse.jpt.jpa.core.resource.orm.XmlManyToMany;
import org.eclipse.jpt.jpa.core.resource.orm.XmlManyToOne;
import org.eclipse.jpt.jpa.core.resource.orm.XmlOneToMany;
import org.eclipse.jpt.jpa.core.resource.orm.XmlOneToOne;
import org.eclipse.jpt.jpa.core.resource.orm.XmlTypeMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmBasicMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmColumnImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmEntityImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmIdMappingImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmJoinColumnImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmJoinTableImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmManyToManyMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmManyToOneMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmOneToManyMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmOneToOneMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmPersistentType;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmTableImpl;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmXml2_0ContextNodeFactory extends GenericOrmXml2_0ContextNodeFactory {

	@Override
	public OrmBasicMapping buildOrmBasicMapping(OrmPersistentAttribute parent,
			XmlBasic resourceMapping) {
		return new HibernateOrmBasicMapping(parent, resourceMapping);
	}

	@Override
	public OrmIdMapping buildOrmIdMapping(OrmPersistentAttribute parent,
			XmlId resourceMapping) {
		return new HibernateOrmIdMappingImpl(parent, resourceMapping);
	}

	@Override
	public OrmEntity buildOrmEntity(OrmPersistentType parent,
			XmlEntity resourceMapping) {
		return new HibernateOrmEntityImpl(parent, resourceMapping);
	}

	@Override
	public OrmTable buildOrmTable(OrmEntity parent, Table.Owner owner) {
		return new HibernateOrmTableImpl(parent, owner);
	}

	@Override
	public OrmJoinTable buildOrmJoinTable(OrmJoinTableRelationshipStrategy parent, Table.Owner owner) {
		return new HibernateOrmJoinTableImpl(parent, owner);
	}

	@Override
	public OrmColumn buildOrmColumn(XmlContextNode parent, OrmColumn.Owner owner) {
		return new HibernateOrmColumnImpl(parent, owner);
	}

	@Override
	public OrmJoinColumn buildOrmJoinColumn(XmlContextNode parent, OrmJoinColumn.Owner owner, XmlJoinColumn xmlJoinColumn) {
		return new HibernateOrmJoinColumnImpl(parent, owner, xmlJoinColumn);
	}

	@Override
	public OrmManyToOneMapping buildOrmManyToOneMapping(OrmPersistentAttribute parent, XmlManyToOne resourceMapping) {
		return new HibernateOrmManyToOneMapping(parent,
				resourceMapping);
	}

	@Override
	public OrmOneToOneMapping buildOrmOneToOneMapping(OrmPersistentAttribute parent, XmlOneToOne resourceMapping) {
		return new HibernateOrmOneToOneMapping(parent, resourceMapping);
	}

	@Override
	public OrmOneToManyMapping buildOrmOneToManyMapping(OrmPersistentAttribute parent, XmlOneToMany resourceMapping) {
		return new HibernateOrmOneToManyMapping(parent, resourceMapping);
	}

	@Override
	public OrmManyToManyMapping buildOrmManyToManyMapping(
			OrmPersistentAttribute parent, XmlManyToMany resourceMapping) {
		return new HibernateOrmManyToManyMapping(parent, resourceMapping);
	}

	@Override
	public OrmPersistentType buildOrmPersistentType(EntityMappings parent,
			XmlTypeMapping resourceMapping) {
		return new HibernateOrmPersistentType(parent, resourceMapping);
	}

}
