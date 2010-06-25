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

import org.eclipse.jpt.core.context.XmlContextNode;
import org.eclipse.jpt.core.context.orm.EntityMappings;
import org.eclipse.jpt.core.context.orm.OrmBasicMapping;
import org.eclipse.jpt.core.context.orm.OrmColumn;
import org.eclipse.jpt.core.context.orm.OrmEntity;
import org.eclipse.jpt.core.context.orm.OrmIdMapping;
import org.eclipse.jpt.core.context.orm.OrmJoinColumn;
import org.eclipse.jpt.core.context.orm.OrmJoinTable;
import org.eclipse.jpt.core.context.orm.OrmJoinTableJoiningStrategy;
import org.eclipse.jpt.core.context.orm.OrmManyToManyMapping;
import org.eclipse.jpt.core.context.orm.OrmManyToOneMapping;
import org.eclipse.jpt.core.context.orm.OrmOneToManyMapping;
import org.eclipse.jpt.core.context.orm.OrmOneToOneMapping;
import org.eclipse.jpt.core.context.orm.OrmPersistentAttribute;
import org.eclipse.jpt.core.context.orm.OrmPersistentType;
import org.eclipse.jpt.core.context.orm.OrmTable;
import org.eclipse.jpt.core.internal.context.orm.GenericOrmXmlContextNodeFactory;
import org.eclipse.jpt.core.resource.orm.XmlBasic;
import org.eclipse.jpt.core.resource.orm.XmlEntity;
import org.eclipse.jpt.core.resource.orm.XmlId;
import org.eclipse.jpt.core.resource.orm.XmlJoinColumn;
import org.eclipse.jpt.core.resource.orm.XmlJoinTable;
import org.eclipse.jpt.core.resource.orm.XmlManyToMany;
import org.eclipse.jpt.core.resource.orm.XmlManyToOne;
import org.eclipse.jpt.core.resource.orm.XmlOneToMany;
import org.eclipse.jpt.core.resource.orm.XmlOneToOne;
import org.eclipse.jpt.core.resource.orm.XmlTypeMapping;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmXmlContextNodeFactory extends GenericOrmXmlContextNodeFactory {
	
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
	public OrmTable buildOrmTable(OrmEntity parent) {
		return new HibernateOrmTableImpl(parent);
	}
	
	@Override
	public OrmJoinTable buildOrmJoinTable(OrmJoinTableJoiningStrategy parent,
			XmlJoinTable resourceJoinTable) {
		return new HibernateOrmJoinTableImpl(parent, resourceJoinTable);
	}

	@Override
	public OrmColumn buildOrmColumn(XmlContextNode parent,
			org.eclipse.jpt.core.context.orm.OrmColumn.Owner owner) {
		return new HibernateOrmColumnImpl(parent, owner);
	}

	@Override
	public OrmJoinColumn buildOrmJoinColumn(XmlContextNode parent,
			org.eclipse.jpt.core.context.orm.OrmJoinColumn.Owner owner,
			XmlJoinColumn resourceJoinColumn) {
		return new HibernateOrmJoinColumnImpl(parent, owner, resourceJoinColumn);
	}

	@Override
	public OrmManyToOneMapping buildOrmManyToOneMapping(
			OrmPersistentAttribute parent, XmlManyToOne resourceMapping) {
		return new HibernateOrmManyToOneMapping<XmlManyToOne>(parent,
				resourceMapping);
	}

	@Override
	public OrmOneToOneMapping buildOrmOneToOneMapping(
			OrmPersistentAttribute parent, XmlOneToOne resourceMapping) {
		return new HibernateOrmOneToOneMapping<XmlOneToOne>(parent,
				resourceMapping);
	}
	
	@Override
	public OrmOneToManyMapping buildOrmOneToManyMapping(
			OrmPersistentAttribute parent, XmlOneToMany resourceMapping) {
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
