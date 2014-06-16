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

import org.eclipse.jpt.jpa.core.context.Entity;
import org.eclipse.jpt.jpa.core.context.JoinColumn;
import org.eclipse.jpt.jpa.core.context.PersistentAttribute;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.orm.GenericOrmJoinColumn;
import org.eclipse.jpt.jpa.core.resource.orm.XmlJoinColumn;
import org.eclipse.jpt.jpa.db.Column;
import org.eclipse.jpt.jpa.db.Table;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.NamingStrategyMappingTools;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;
import org.jboss.tools.hibernate.spi.INamingStrategy;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmJoinColumnImpl extends GenericOrmJoinColumn implements
		HibernateOrmJoinColumn {

	public HibernateOrmJoinColumnImpl(JoinColumn.ParentAdapter owner,
			XmlJoinColumn resourceJoinColumn) {
		super(owner, resourceJoinColumn);
	}

	@Override
	protected String buildDefaultName() {
		return NamingStrategyMappingTools.buildJoinColumnDefaultName(this, this.parentAdapter);
	}

	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
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
		INamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null){
			try {
				return ns.columnName(getSpecifiedName());
			} catch (Exception e) {
				IMessage m = HibernateJpaValidationMessage.buildMessage(
						IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, this);
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
		INamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null){
			try {
				return ns.columnName(this.specifiedReferencedColumnName);
			} catch (Exception e) {
				IMessage m = HibernateJpaValidationMessage.buildMessage(
						IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, this);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return this.specifiedReferencedColumnName;
	}

	public PersistentAttribute getReferencedPersistentAttribute() {
		if (this.parentAdapter.getJoinColumnsSize() != 1) {
			return null;
		}
		Entity targetEntity = this.parentAdapter.getRelationshipTarget();
		if (targetEntity == null) {
			return null;
		}
		PersistentAttribute pAttr = null;
		for (PersistentAttribute attribute : targetEntity.getPersistentType().getAllAttributes()) {
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

}
