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
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.List;

import org.eclipse.jpt.jpa.core.context.java.JavaEntity;
import org.eclipse.jpt.jpa.core.context.java.JavaTable;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.java.GenericJavaTable;
import org.eclipse.jpt.jpa.core.validation.JptJpaCoreValidationMessages;
import org.eclipse.jpt.jpa.db.Schema;
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
public class HibernateJavaTableImpl extends GenericJavaTable implements HibernateJavaTable {

	protected String defaultDBTableName;

	public HibernateJavaTableImpl(JavaTable.ParentAdapter parent) {
		super(parent);
	}

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.defaultDBTableName = buildDefaultDBTableName();
	}

	@Override
	public void update() {
		super.update();
		setDefaultDBTableName(buildDefaultDBTableName());
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
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null){
			try {
				return ns.tableName(getSpecifiedName());
			} catch (Exception e) {
				IMessage m = HibernateJpaValidationMessage.buildMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, this);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return this.getName();
	}

	protected String buildDefaultDBTableName(){
		if (getDefaultName() == null) return null;
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null) {
			try {
				return ns.classToTableName(getDefaultName());
			} catch (Exception e) {
				IMessage m = HibernateJpaValidationMessage.buildMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, this);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return this.getDefaultName();
	}

	@Override
	public String getDefaultDBTableName() {
		return this.defaultDBTableName;
	}

	protected void setDefaultDBTableName(String name) {
		String old = this.defaultDBTableName;
		this.defaultDBTableName = name;
		this.firePropertyChanged(DEFAULT_DB_NAME_PROPERTY, old, name);
	}

	//@Override
	protected void validateAgainstDatabase(List<IMessage> messages) {
		if ( ! this.catalogIsResolved()) {
			messages.add(
					buildValidationMessage(
							JptJpaCoreValidationMessages.TABLE_UNRESOLVED_CATALOG,
							new String[] {this.getCatalog(), this.getDBTableName()},
							this.getCatalogValidationTextRange()
					)
			);
			return;
		}

		if ( ! this.schemaIsResolved()) {
			messages.add(
					buildValidationMessage(
							JptJpaCoreValidationMessages.TABLE_UNRESOLVED_SCHEMA,
							new String[] {this.getSchema(), this.getDBTableName()},
							this.getSchemaValidationTextRange()
					)
			);
			return;
		}

		if ( ! this.isResolved()) {
			messages.add(
					buildValidationMessage(
							JptJpaCoreValidationMessages.TABLE_UNRESOLVED_NAME,
							new String[] {this.getDBTableName()},
							this.getNameValidationTextRange()
					)
			);
		}
	}

}
