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

import java.util.Iterator;

import org.eclipse.jpt.jpa.core.context.Entity;
import org.eclipse.jpt.jpa.core.context.ReadOnlyPersistentAttribute;
import org.eclipse.jpt.jpa.core.context.java.JavaJoinColumn;
import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.java.GenericJavaJoinColumn;
import org.eclipse.jpt.jpa.core.resource.java.JoinColumnAnnotation;
import org.eclipse.jpt.jpa.db.Column;
import org.eclipse.jpt.jpa.db.Table;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.NamingStrategyMappingTools;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaJoinColumnImpl extends GenericJavaJoinColumn
implements HibernateJavaJoinColumn {

	public HibernateJavaJoinColumnImpl(JavaJpaContextNode parent, JavaJoinColumn.Owner owner, JoinColumnAnnotation columnAnnotation) {
		super(parent, owner, columnAnnotation);
	}

	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}

	@Override
	protected String buildDefaultName() {
		return NamingStrategyMappingTools.buildJoinColumnDefaultName(this, this.owner);
	}

	@Override
	public ReadOnlyPersistentAttribute getReferencedPersistentAttribute() {
		if (this.owner.joinColumnsSize() != 1) {
			return null;
		}
		Entity targetEntity = this.owner.getRelationshipTarget();
		if (targetEntity == null) {
			return null;
		}
		ReadOnlyPersistentAttribute pAttr = null;
		Iterator<ReadOnlyPersistentAttribute> attributes = targetEntity.getPersistentType().allAttributes();
		for (Iterator<ReadOnlyPersistentAttribute> stream = attributes; stream.hasNext();) {
			ReadOnlyPersistentAttribute attribute = stream.next();
			String name = attribute.getPrimaryKeyColumnName();
			if (name != null) {
				if (pAttr == null){
					pAttr = attribute;
				} else {
					return null;
				}
			}
		}
		return pAttr;
	}

	@Override
	public Column getDbColumn() {
		Table table = this.getDbTable();
		return (table == null) ? null : table.getColumnForIdentifier(this.getDBColumnName());
	}

	@Override
	public String getDBColumnName() {
		return getSpecifiedDBColumnName() != null ? getSpecifiedDBColumnName()
				: getDefaultDBColumnName();
	}

	@Override
	public String getDefaultDBColumnName() {
		return getDefaultName();
	}

	@Override
	public String getSpecifiedDBColumnName() {
		if (getSpecifiedName() == null) return null;
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null){
			try {
				return ns.columnName(getSpecifiedName());
			} catch (Exception e) {
				IMessage m = HibernateJpaValidationMessage.buildMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return this.getSpecifiedName();
	}

	@Override
	public Column getReferencedDbColumn() {
		Table table = this.getReferencedColumnDbTable();
		return (table == null) ? null : table.getColumnForIdentifier(this.getReferencedDBColumnName());
	}

	@Override
	public String getReferencedDBColumnName() {
		return getReferencedSpecifiedDBColumnName() != null ? getReferencedSpecifiedDBColumnName()
				: getReferencedDefaultDBColumnName();
	}

	@Override
	public String getReferencedDefaultDBColumnName() {
		return this.defaultReferencedColumnName;
	}

	@Override
	public String getReferencedSpecifiedDBColumnName() {
		if (this.specifiedReferencedColumnName == null) return null;
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null){
			try {
				return ns.columnName(this.specifiedReferencedColumnName);
			} catch (Exception e) {
				IMessage m = HibernateJpaValidationMessage.buildMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return this.specifiedReferencedColumnName;
	}

	/*protected void validateJoinColumnName(List<IMessage> messages, CompilationUnit astRoot) {
		if ( ! this.isResolved() && getDbTable() != null) {
			if (getDBColumnName() != null) {
				messages.add(
					DefaultJpaValidationMessages.buildMessage(
						IMessage.HIGH_SEVERITY,
						JpaValidationMessages.JOIN_COLUMN_UNRESOLVED_NAME,
						new String[] {this.getDBColumnName()},
						this,
						this.getNameTextRange(astRoot)
					)
				);
			}
			else if (getOwner().joinColumnsSize() > 1) {
				messages.add(this.buildUnspecifiedReferencedColumnNameMultipleJoinColumnsMessage(astRoot));
			}
			//If the name is null and there is only one join-column, one of these validation messages will apply
			// 1. target entity does not have a primary key
			// 2. target entity is not specified
			// 3. target entity is not an entity
		}
	}

	protected void validateReferencedColumnName(List<IMessage> messages, CompilationUnit astRoot) {
		if ( ! this.isReferencedColumnResolved() && getReferencedColumnDbTable() != null) {
			if (getReferencedDBColumnName() != null) {
				messages.add(
					DefaultJpaValidationMessages.buildMessage(
						IMessage.HIGH_SEVERITY,
						JpaValidationMessages.JOIN_COLUMN_REFERENCED_COLUMN_UNRESOLVED_NAME,
						new String[] {this.getReferencedDBColumnName(), this.getDBColumnName()},
						this,
						this.getReferencedColumnNameTextRange(astRoot)
					)
				);
			}
			else if (getOwner().joinColumnsSize() > 1) {
				messages.add(
						DefaultJpaValidationMessages.buildMessage(
							IMessage.HIGH_SEVERITY,
							JpaValidationMessages.JOIN_COLUMN_REFERENCED_COLUMN_UNRESOLVED_NAME_MULTIPLE_JOIN_COLUMNS,
							this,
							this.getNameTextRange(astRoot)
						)
					);
			}
			//If the referenced column name is null and there is only one join-column, one of these validation messages will apply
			// 1. target entity does not have a primary key
			// 2. target entity is not specified
			// 3. target entity is not an entity
		}
	}*/




}
