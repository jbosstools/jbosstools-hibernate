/*******************************************************************************
 * Copyright (c) 2009-2011 Red Hat, Inc.
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

import org.eclipse.jpt.jpa.core.context.NamedColumn;
import org.eclipse.jpt.jpa.core.context.orm.OrmPersistentAttribute;
import org.eclipse.jpt.jpa.core.context.orm.OrmSpecifiedPersistentAttribute;
import org.eclipse.jpt.jpa.core.internal.context.orm.AbstractOrmIdMapping;
import org.eclipse.jpt.jpa.core.resource.orm.XmlId;
import org.eclipse.jpt.jpa.core.validation.JptJpaCoreValidationMessages;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmIdMappingImpl extends AbstractOrmIdMapping<XmlId>
implements HibernateOrmIdMapping {

	public HibernateOrmIdMappingImpl(OrmSpecifiedPersistentAttribute parent,
			XmlId resourceMapping) {
		super(parent, resourceMapping);
	}
	
	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}

	@Override
	public String getDefaultColumnName(NamedColumn column) {
		if (getName() != null){
			NamingStrategy namingStrategy = getJpaProject().getNamingStrategy();
			if (getJpaProject().isNamingStrategyEnabled() && namingStrategy != null){
				try {
					return namingStrategy.propertyToColumnName(getName());
				} catch (Exception e) {
					IMessage m = HibernateJpaValidationMessage.buildMessage(
							IMessage.HIGH_SEVERITY,
							Messages.NAMING_STRATEGY_EXCEPTION, this);
					HibernateJptPlugin.logException(m.getText(), e);
				}
			}
		}
		return super.getDefaultColumnName(column);
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
		String tableName = this.column.getTableName();
		if (this.getTypeMapping().tableNameIsInvalid(tableName)) {
			if (pa.isVirtual()) {
				messages.add(
					buildValidationMessage(
						JptJpaCoreValidationMessages.VIRTUAL_ATTRIBUTE_COLUMN_TABLE_NOT_VALID,
						new String[] {pa.getName(), tableName, this.getColumn().getDBColumnName()},
						this.column, 
						this.column.getTableNameValidationTextRange()
					)
				);
			} else {
				messages.add(
					buildValidationMessage(
						JptJpaCoreValidationMessages.COLUMN_UNRESOLVED_TABLE,
						new String[] {tableName, this.getColumn().getDBColumnName()}, 
						this.column, 
						this.column.getTableNameValidationTextRange()
					)
				);
			}
			return;
		}
		
		if ( ! this.column.isResolved() && this.column.getDbTable() != null) {
			if (pa.isVirtual()) {
				messages.add(
					buildValidationMessage(
						JptJpaCoreValidationMessages.VIRTUAL_ATTRIBUTE_COLUMN_UNRESOLVED_NAME,
						new String[] {pa.getName(), this.getColumn().getDBColumnName()},
						this.column, 
						this.column.getTableNameValidationTextRange()
					)
				);
			} else {
				messages.add(
					buildValidationMessage(
						JptJpaCoreValidationMessages.COLUMN_UNRESOLVED_NAME,
						new String[] {this.getColumn().getDBColumnName()}, 
						this.column, 
						this.column.getTableNameValidationTextRange()
					)
				);
			}
		}
	}

}
