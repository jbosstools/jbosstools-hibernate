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

import org.eclipse.jpt.core.context.BaseJoinColumn;
import org.eclipse.jpt.core.context.Entity;
import org.eclipse.jpt.core.context.TypeMapping;
import org.eclipse.jpt.core.context.java.JavaEntity;
import org.eclipse.jpt.core.context.orm.OrmBaseJoinColumn;
import org.eclipse.jpt.core.context.orm.OrmPersistentType;
import org.eclipse.jpt.core.internal.context.orm.AbstractOrmEntity;
import org.eclipse.jpt.core.resource.orm.XmlEntity;
import org.eclipse.jpt.core.utility.TextRange;
import org.eclipse.jpt.utility.internal.ClassTools;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;

/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
public class HibernateOrmEntity extends AbstractOrmEntity {

	public HibernateOrmEntity(OrmPersistentType parent,
			XmlEntity resourceMapping) {
		super(parent, resourceMapping);
	}
	
	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}
	
	@Override
	protected String buildDefaultName() {
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (!isMetadataComplete()) {
			JavaEntity javaEntity = getJavaEntity();
			if (javaEntity != null) {
				return javaEntity.getName();
			}
		}
		String className = getClass_();
		if (className != null) {
			try {
				return ns == null ? ClassTools.shortNameForClassNamed(className)
					: ns.classToTableName(className);
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY, 
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return null;
	}
	
	@Override
	protected OrmBaseJoinColumn.Owner createPrimaryKeyJoinColumnOwner() {
		return new HibernatePrimaryKeyJoinColumnOwner();
	}
	
	// ********** pk join column owner **********

	class HibernatePrimaryKeyJoinColumnOwner implements OrmBaseJoinColumn.Owner
	{
		public TypeMapping getTypeMapping() {
			return HibernateOrmEntity.this;
		}

		public org.eclipse.jpt.db.Table getDbTable(String tableName) {
			return HibernateOrmEntity.this.getDbTable(tableName);
		}

		public org.eclipse.jpt.db.Table getReferencedColumnDbTable() {
			Entity parentEntity = HibernateOrmEntity.this.getParentEntity();
			return (parentEntity == null) ? null : parentEntity.getPrimaryDbTable();
		}

		public int joinColumnsSize() {
			return HibernateOrmEntity.this.primaryKeyJoinColumnsSize();
		}
		
		public boolean isVirtual(BaseJoinColumn joinColumn) {
			return HibernateOrmEntity.this.defaultPrimaryKeyJoinColumns.contains(joinColumn);
		}
		
		public String getDefaultColumnName() {
			if (joinColumnsSize() != 1) {
				return null;
			}
			Entity parentEntity = HibernateOrmEntity.this.getParentEntity();
			NamingStrategy ns = HibernateOrmEntity.this.getJpaProject().getNamingStrategy();
			if (ns != null){				
				try {
					String name = ns.joinKeyColumnName(parentEntity.getPrimaryKeyColumnName(),
							parentEntity.getPrimaryTableName());
					return name;
				} catch (Exception e) {
					Message m = new LocalMessage(IMessage.HIGH_SEVERITY, 
							Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
					HibernateJptPlugin.logException(m.getText(), e);
				}
			}
			return parentEntity.getPrimaryKeyColumnName();
		}
		
		public TextRange getValidationTextRange() {
			return null;
		}
	}
}
