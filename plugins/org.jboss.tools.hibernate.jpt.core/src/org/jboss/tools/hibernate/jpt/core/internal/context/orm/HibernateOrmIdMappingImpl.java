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

import java.util.List;

import org.eclipse.jpt.core.context.orm.OrmPersistentAttribute;
import org.eclipse.jpt.core.internal.context.orm.GenericOrmIdMapping;
import org.eclipse.jpt.core.internal.validation.DefaultJpaValidationMessages;
import org.eclipse.jpt.core.internal.validation.JpaValidationMessages;
import org.eclipse.jpt.core.resource.orm.XmlId;
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
public class HibernateOrmIdMappingImpl extends GenericOrmIdMapping<XmlId>
implements HibernateOrmIdMapping {

	public HibernateOrmIdMappingImpl(OrmPersistentAttribute parent,
			XmlId resourceMapping) {
		super(parent, resourceMapping);
	}
	
	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}

	@Override
	public String getDefaultColumnName() {
		NamingStrategy namingStrategy = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && namingStrategy != null && getName() != null){
			try {
				return namingStrategy.propertyToColumnName(getName());
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return super.getDefaultColumnName();
	}
	
	@Override
	public HibernateOrmColumn getColumn() {
		return (HibernateOrmColumn) column;
	}
	
	@Override
	public String getPrimaryKeyColumnName() {
		return this.getColumn().getDBColumnName();
	}
	
	protected void validateColumn(List<IMessage> messages) {
		OrmPersistentAttribute pa = this.getPersistentAttribute();
		String tableName = this.column.getTable();
		if (this.getTypeMapping().tableNameIsInvalid(tableName)) {
			if (pa.isVirtual()) {
				messages.add(
					DefaultJpaValidationMessages.buildMessage(
						IMessage.HIGH_SEVERITY,
						JpaValidationMessages.VIRTUAL_ATTRIBUTE_COLUMN_UNRESOLVED_TABLE,
						new String[] {pa.getName(), tableName, this.getColumn().getDBColumnName()},
						this.column, 
						this.column.getTableTextRange()
					)
				);
			} else {
				messages.add(
					DefaultJpaValidationMessages.buildMessage(
						IMessage.HIGH_SEVERITY,
						JpaValidationMessages.COLUMN_UNRESOLVED_TABLE,
						new String[] {tableName, this.getColumn().getDBColumnName()}, 
						this.column, 
						this.column.getTableTextRange()
					)
				);
			}
			return;
		}
		
		if ( ! this.column.isResolved() && this.column.getDbTable() != null) {
			if (pa.isVirtual()) {
				messages.add(
					DefaultJpaValidationMessages.buildMessage(
						IMessage.HIGH_SEVERITY,
						JpaValidationMessages.VIRTUAL_ATTRIBUTE_COLUMN_UNRESOLVED_NAME,
						new String[] {pa.getName(), this.getColumn().getDBColumnName()},
						this.column, 
						this.column.getNameTextRange()
					)
				);
			} else {
				messages.add(
					DefaultJpaValidationMessages.buildMessage(
						IMessage.HIGH_SEVERITY,
						JpaValidationMessages.COLUMN_UNRESOLVED_NAME,
						new String[] {this.getColumn().getDBColumnName()}, 
						this.column, 
						this.column.getNameTextRange()
					)
				);
			}
		}
	}

}
