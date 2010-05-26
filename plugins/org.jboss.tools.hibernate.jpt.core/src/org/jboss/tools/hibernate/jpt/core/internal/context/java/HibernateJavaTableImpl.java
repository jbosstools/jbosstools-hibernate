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
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.java.JavaEntity;
import org.eclipse.jpt.core.internal.context.java.GenericJavaTable;
import org.eclipse.jpt.core.internal.validation.DefaultJpaValidationMessages;
import org.eclipse.jpt.core.internal.validation.JpaValidationMessages;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember;
import org.eclipse.jpt.db.Schema;
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
public class HibernateJavaTableImpl extends GenericJavaTable implements HibernateJavaTable {

	protected String defaultDBTableName;

	public HibernateJavaTableImpl(JavaEntity parent) {
		super(parent);
	}

	@Override
	public void initialize(JavaResourcePersistentMember pr) {
		super.initialize(pr);
		this.defaultDBTableName = buildDefaultDBTableName();
	}

	@Override
	public void update(JavaResourcePersistentMember jrpm) {
		super.update(jrpm);
		setDefaultDBTableName(buildDefaultDBTableName());
	}

	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}

	@Override
	public org.eclipse.jpt.db.Table getDbTable() {
		Schema dbSchema = this.getDbSchema();
		return (dbSchema == null) ? null : dbSchema.getTableForIdentifier(getDBTableName());
	}

	public String getDBTableName(){
		return getSpecifiedDBTableName() != null ? getSpecifiedDBTableName()
				: getDefaultDBTableName();
	}

	public String getSpecifiedDBTableName() {
		if (getSpecifiedName() == null) return null;
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null){
			try {
				return ns.tableName(getSpecifiedName());
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
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
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return this.getDefaultName();
	}

	public String getDefaultDBTableName() {
		return this.defaultDBTableName;
	}

	protected void setDefaultDBTableName(String name) {
		String old = this.defaultDBTableName;
		this.defaultDBTableName = name;
		this.firePropertyChanged(DEFAULT_DB_NAME_PROPERTY, old, name);
	}

	protected void validateAgainstDatabase(List<IMessage> messages, CompilationUnit astRoot) {
		if ( ! this.hasResolvedCatalog()) {
			messages.add(
				DefaultJpaValidationMessages.buildMessage(
					IMessage.HIGH_SEVERITY,
					JpaValidationMessages.TABLE_UNRESOLVED_CATALOG,
					new String[] {this.getCatalog(), this.getDBTableName()}, 
					this, 
					this.getCatalogTextRange(astRoot)
				)
			);
			return;
		}
		
		if ( ! this.hasResolvedSchema()) {
			messages.add(
				DefaultJpaValidationMessages.buildMessage(
					IMessage.HIGH_SEVERITY,
					JpaValidationMessages.TABLE_UNRESOLVED_SCHEMA,
					new String[] {this.getSchema(), this.getDBTableName()}, 
					this, 
					this.getSchemaTextRange(astRoot)
				)
			);
			return;
		}
		
		if ( ! this.isResolved()) {
			messages.add(
				DefaultJpaValidationMessages.buildMessage(
					IMessage.HIGH_SEVERITY,
					JpaValidationMessages.TABLE_UNRESOLVED_NAME,
					new String[] {this.getDBTableName()}, 
					this, 
					this.getNameTextRange(astRoot)
				)
			);
		}
	}

}
