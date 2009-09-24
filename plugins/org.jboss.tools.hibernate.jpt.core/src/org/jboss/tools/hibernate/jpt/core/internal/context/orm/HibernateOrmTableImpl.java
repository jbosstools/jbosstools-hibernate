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

import java.util.Iterator;
import java.util.List;

import org.eclipse.jpt.core.context.Table;
import org.eclipse.jpt.core.context.orm.OrmEntity;
import org.eclipse.jpt.core.internal.context.orm.GenericOrmTable;
import org.eclipse.jpt.core.internal.validation.DefaultJpaValidationMessages;
import org.eclipse.jpt.core.internal.validation.JpaValidationMessages;
import org.eclipse.jpt.core.resource.orm.XmlEntity;
import org.eclipse.jpt.db.Schema;
import org.eclipse.jpt.utility.internal.iterators.TransformationIterator;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateTable;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmTableImpl extends GenericOrmTable implements HibernateOrmTable {

	protected String defaultDBTableName;

	public HibernateOrmTableImpl(OrmEntity parent) {
		super(parent);
	}

	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}
	

	@Override
	public void initialize(XmlEntity xmlEntity) {
		super.initialize(xmlEntity);
		this.defaultDBTableName = buildDefaultDBTableName();
	}

	@Override
	public void update(XmlEntity xmlEntity) {
		super.update(xmlEntity);
		setDefaultDBTableName(buildDefaultDBTableName());
	}
	
	@Override
	public org.eclipse.jpt.db.Table getDbTable() {
		Schema dbSchema = this.getDbSchema();
		return (dbSchema == null) ? null : dbSchema.getTableForIdentifier(getDBTableName());
	}
	
	protected void setDefaultDBTableName(String name) {
		String old = this.defaultDBTableName;
		this.defaultDBTableName = name;
		this.firePropertyChanged(DEFAULT_DB_NAME_PROPERTY, old, name);
	}
	
	protected String buildDefaultDBTableName(){
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

	public String getDBTableName() {
		return getSpecifiedDBTableName() != null ? getSpecifiedDBTableName()
				: getDefaultDBTableName();
	}

	public String getDefaultDBTableName() {
		return this.defaultDBTableName;
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

	
	/**
	 * Convert Table to it's DB name.
	 */
	protected Iterator<String> tableNames(Iterator<Table> tables) {
		return new TransformationIterator<Table, String>(tables) {
			@Override
			protected String transform(Table t) {
				if (t instanceof HibernateTable) {
					return ((HibernateTable)t).getDBTableName();					
				} else {
					return t.getName();//What is this???
				}				
			}
		};
	}
	
	@Override
	protected void validateAgainstDatabase(List<IMessage> messages) {
		if ( ! this.hasResolvedCatalog()) {
			messages.add(
				DefaultJpaValidationMessages.buildMessage(
					IMessage.HIGH_SEVERITY,
					JpaValidationMessages.TABLE_UNRESOLVED_CATALOG,
					new String[] {this.getCatalog(), this.getDBTableName()}, 
					this,
					this.getCatalogTextRange()
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
					this.getSchemaTextRange()
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
					this.getNameTextRange()
				)
			);
			return;
		}
	}

}
