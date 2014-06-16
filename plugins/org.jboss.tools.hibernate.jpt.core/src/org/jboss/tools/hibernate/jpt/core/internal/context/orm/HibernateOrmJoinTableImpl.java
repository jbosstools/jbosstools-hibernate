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

import org.eclipse.jpt.jpa.core.context.PersistentAttribute;
import org.eclipse.jpt.jpa.core.context.orm.OrmSpecifiedJoinTable;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.orm.GenericOrmJoinTable;
import org.eclipse.jpt.jpa.core.validation.JptJpaCoreValidationMessages;
import org.eclipse.jpt.jpa.db.Schema;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;
import org.jboss.tools.hibernate.spi.INamingStrategy;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmJoinTableImpl extends GenericOrmJoinTable implements
HibernateOrmJoinTable {


	public HibernateOrmJoinTableImpl(OrmSpecifiedJoinTable.ParentAdapter owner) {
		super(owner);
	}

	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}

	@Override
	public org.eclipse.jpt.jpa.db.Table getDbTable() {
		Schema dbSchema = this.getDbSchema();
		return (dbSchema == null) ? null : dbSchema.getTableForIdentifier(getDBTableName());
	}

	@Override
	public String getDBTableName(){
		return getSpecifiedDBTableName() != null ? getSpecifiedDBTableName()
				: getDefaultDBTableName();
	}

	@Override
	public String getSpecifiedDBTableName() {
		if (getSpecifiedName() == null) return null;
		INamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null){
			try {
				return ns.tableName(getSpecifiedName());
			} catch (Exception e) {
				IMessage m = HibernateJpaValidationMessage.buildMessage(
						IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, this);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return this.getName();
	}

	@Override
	public String getDefaultDBTableName() {
		return getDefaultName();
	}

	//@Override
	protected boolean validateAgainstDatabase(List<IMessage> messages,
			IReporter reporter) {
		PersistentAttribute persistentAttribute = this.getPersistentAttribute();
		if ( ! this.catalogIsResolved()) {
			if (persistentAttribute.isVirtual()) {
				messages.add(
						buildValidationMessage(
								JptJpaCoreValidationMessages.JOIN_TABLE_UNRESOLVED_CATALOG,
								new String[] {persistentAttribute.getName(), this.getCatalog(), this.getDBTableName()},
								this.getCatalogValidationTextRange()
						)
				);
			} else {
				messages.add(
						buildValidationMessage(
								JptJpaCoreValidationMessages.VIRTUAL_ATTRIBUTE_JOIN_TABLE_UNRESOLVED_CATALOG,
								new String[] {this.getCatalog(), this.getDBTableName()},
								this.getCatalogValidationTextRange()
						)
				);
			}
			return false;
		}

		if ( ! this.schemaIsResolved()) {
			if (persistentAttribute.isVirtual()) {
				messages.add(
						buildValidationMessage(
								JptJpaCoreValidationMessages.JOIN_TABLE_UNRESOLVED_SCHEMA,
								new String[] {persistentAttribute.getName(), this.getSchema(), this.getDBTableName()},
								this.getSchemaValidationTextRange()
						)
				);
			} else {
				messages.add(
						buildValidationMessage(
								JptJpaCoreValidationMessages.VIRTUAL_ATTRIBUTE_JOIN_TABLE_UNRESOLVED_SCHEMA,
								new String[] {this.getSchema(), this.getDBTableName()},
								this.getSchemaValidationTextRange()
						)
				);
			}
			return false;
		}
		if ( ! this.isResolved()) {
			if (getDBTableName() != null) { //if name is null, the validation will be handled elsewhere, such as the target entity is not defined
				if (persistentAttribute.isVirtual()) {
					messages.add(
							buildValidationMessage(
									JptJpaCoreValidationMessages.VIRTUAL_ATTRIBUTE_JOIN_TABLE_UNRESOLVED_NAME,
									new String[] {persistentAttribute.getName(), this.getDBTableName()},
									this.getNameValidationTextRange()
							)
					);
				}
				else {
					messages.add(
							buildValidationMessage(
									JptJpaCoreValidationMessages.JOIN_TABLE_UNRESOLVED_NAME,
									new String[] {this.getDBTableName()},
									this.getNameValidationTextRange())
					);
				}
			}
			return false;
		}
		return true;
	}

}
