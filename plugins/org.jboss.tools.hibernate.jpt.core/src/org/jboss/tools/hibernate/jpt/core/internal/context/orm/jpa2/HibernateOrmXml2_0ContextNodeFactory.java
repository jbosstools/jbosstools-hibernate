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
import org.eclipse.jpt.jpa.core.internal.jpa2.context.orm.GenericOrmXmlContextModelFactory2_0;
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
public class HibernateOrmXml2_0ContextNodeFactory extends GenericOrmXmlContextModelFactory2_0 {

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
	public OrmOneToManyMapping buildOrmOneToManyMapping(OrmSpecifiedPersistentAttribute parent, XmlOneToMany resourceMapping) {
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
