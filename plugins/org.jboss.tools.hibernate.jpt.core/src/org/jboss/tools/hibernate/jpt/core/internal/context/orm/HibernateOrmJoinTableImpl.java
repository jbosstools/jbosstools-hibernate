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

import org.eclipse.jpt.core.context.PersistentAttribute;
import org.eclipse.jpt.core.context.orm.OrmJoinTableJoiningStrategy;
import org.eclipse.jpt.core.internal.jpa1.context.orm.GenericOrmJoinTable;
import org.eclipse.jpt.core.internal.validation.DefaultJpaValidationMessages;
import org.eclipse.jpt.core.resource.orm.XmlJoinTable;
import org.eclipse.jpt.db.Schema;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmJoinTableImpl extends GenericOrmJoinTable implements
		HibernateOrmJoinTable {


	public HibernateOrmJoinTableImpl(OrmJoinTableJoiningStrategy parent,
			XmlJoinTable resourceJoinTable) {
		super(parent, resourceJoinTable);
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

	public String getDefaultDBTableName() {
		return getDefaultName();
	}
	
	@Override
	protected boolean validateAgainstDatabase(List<IMessage> messages,
			IReporter reporter) {
		PersistentAttribute persistentAttribute = this.getPersistentAttribute();
		if ( ! this.hasResolvedCatalog()) {
			if (persistentAttribute.isVirtual()) {
				messages.add(
					DefaultJpaValidationMessages.buildMessage(
						IMessage.HIGH_SEVERITY,
						getVirtualAttributeUnresolvedCatalogMessageId(),
						new String[] {persistentAttribute.getName(), this.getCatalog(), this.getDBTableName()}, 
						this,
						this.getCatalogTextRange()
					)
				);
			} else {
				messages.add(
					DefaultJpaValidationMessages.buildMessage(
						IMessage.HIGH_SEVERITY,
						getUnresolvedCatalogMessageId(),
						new String[] {this.getCatalog(), this.getDBTableName()}, 
						this,
						this.getCatalogTextRange()
					)
				);
			}
			return false;
		}

		if ( ! this.hasResolvedSchema()) {
			if (persistentAttribute.isVirtual()) {
				messages.add(
					DefaultJpaValidationMessages.buildMessage(
						IMessage.HIGH_SEVERITY,
						getVirtualAttributeUnresolvedSchemaMessageId(),
						new String[] {persistentAttribute.getName(), this.getSchema(), this.getDBTableName()}, 
						this,
						this.getSchemaTextRange()
					)
				);
			} else {
				messages.add(
					DefaultJpaValidationMessages.buildMessage(
						IMessage.HIGH_SEVERITY,
						getUnresolvedSchemaMessageId(),
						new String[] {this.getSchema(), this.getDBTableName()}, 
						this,
						this.getSchemaTextRange()
					)
				);
			}
			return false;
		}
		if ( ! this.isResolved()) {
			if (getDBTableName() != null) { //if name is null, the validation will be handled elsewhere, such as the target entity is not defined
				if (persistentAttribute.isVirtual()) {
					messages.add(
						DefaultJpaValidationMessages.buildMessage(
							IMessage.HIGH_SEVERITY,
							getVirtualAttributeUnresolvedNameMessageId(),
							new String[] {persistentAttribute.getName(), this.getDBTableName()}, 
							this,
							this.getNameTextRange()
						)
					);
				} 
				else {
					messages.add(
						DefaultJpaValidationMessages.buildMessage(
								IMessage.HIGH_SEVERITY,
								getUnresolvedNameMessageId(),
								new String[] {this.getDBTableName()}, 
								this, 
								this.getNameTextRange())
						);
				}
			}
			return false;
		}
		return true;
	}

}
