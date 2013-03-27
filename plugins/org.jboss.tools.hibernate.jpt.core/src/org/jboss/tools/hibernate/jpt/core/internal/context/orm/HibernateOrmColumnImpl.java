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

import org.eclipse.jpt.jpa.core.context.orm.OrmSpecifiedColumn;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.orm.GenericOrmColumn;
import org.eclipse.jpt.jpa.db.Column;
import org.eclipse.jpt.jpa.db.Table;
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
public class HibernateOrmColumnImpl extends GenericOrmColumn
implements HibernateOrmColumn {

	public HibernateOrmColumnImpl(OrmSpecifiedColumn.ParentAdapter parent) {
		super(parent);
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
	public String getSpecifiedDBColumnName() {
		if (getSpecifiedName() == null) return null;
		NamingStrategy ns = getJpaProject().getNamingStrategy();
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
	public String getDefaultDBColumnName() {
		return getDefaultName();
	}

	
	// FIXME: Check if it was required
//	@Override
//	public Table getDbTable() {
//		return this.parent.resolveDbTable(this.getDBTableName());
//	}

	@Override
	public String getDBTableName() {
		return getSpecifiedDBTableName() != null ? getSpecifiedDBTableName()
				: getDefaultDBTableName();
	}

	@Override
	public String getDefaultDBTableName() {
		return getDefaultTableName();
	}

	@Override
	public String getSpecifiedDBTableName() {
		if (getSpecifiedTableName() == null) return null;
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null){
			try {
				return ns.tableName(getSpecifiedTableName());
			} catch (Exception e) {
				IMessage m = HibernateJpaValidationMessage.buildMessage(
						IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, this);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return this.getSpecifiedTableName();
	}

}
