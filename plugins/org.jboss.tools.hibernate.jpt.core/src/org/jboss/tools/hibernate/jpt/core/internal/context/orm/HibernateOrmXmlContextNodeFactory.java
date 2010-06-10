package org.jboss.tools.hibernate.jpt.core.internal.context.orm;

import org.eclipse.jpt.core.context.XmlContextNode;
import org.eclipse.jpt.core.context.orm.OrmBasicMapping;
import org.eclipse.jpt.core.context.orm.OrmColumn;
import org.eclipse.jpt.core.context.orm.OrmEntity;
import org.eclipse.jpt.core.context.orm.OrmIdMapping;
import org.eclipse.jpt.core.context.orm.OrmJoinColumn;
import org.eclipse.jpt.core.context.orm.OrmJoinTable;
import org.eclipse.jpt.core.context.orm.OrmJoinTableJoiningStrategy;
import org.eclipse.jpt.core.context.orm.OrmPersistentAttribute;
import org.eclipse.jpt.core.context.orm.OrmPersistentType;
import org.eclipse.jpt.core.context.orm.OrmTable;
import org.eclipse.jpt.core.internal.context.orm.AbstractOrmXmlContextNodeFactory;
import org.eclipse.jpt.core.resource.orm.XmlBasic;
import org.eclipse.jpt.core.resource.orm.XmlEntity;
import org.eclipse.jpt.core.resource.orm.XmlId;
import org.eclipse.jpt.core.resource.orm.XmlJoinColumn;
import org.eclipse.jpt.core.resource.orm.XmlJoinTable;
import org.eclipse.jpt.core.resource.orm.XmlJoinTableMapping;

public class HibernateOrmXmlContextNodeFactory extends
		AbstractOrmXmlContextNodeFactory {

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

}
