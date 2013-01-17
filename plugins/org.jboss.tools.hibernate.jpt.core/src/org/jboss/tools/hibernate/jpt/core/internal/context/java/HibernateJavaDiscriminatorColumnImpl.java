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

import org.eclipse.jpt.jpa.core.context.ReadOnlyNamedDiscriminatorColumn;
import org.eclipse.jpt.jpa.core.context.java.JavaEntity;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.java.GenericJavaDiscriminatorColumn;
import org.eclipse.jpt.jpa.core.internal.validation.JpaValidationMessages;
import org.eclipse.jpt.jpa.db.Column;
import org.eclipse.jpt.jpa.db.Table;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaDiscriminatorColumnImpl extends
		GenericJavaDiscriminatorColumn implements
		HibernateJavaDiscriminatorColumn {

	public HibernateJavaDiscriminatorColumnImpl(JavaEntity parent, ReadOnlyNamedDiscriminatorColumn.Owner owner) {
		super(parent, owner);
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

	public String getDBColumnName(){
		return getSpecifiedDBColumnName() != null ? getSpecifiedDBColumnName()
				: getDefaultDBColumnName();
	}

	public String getSpecifiedDBColumnName(){
		if (getSpecifiedName() == null) return null;
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null){
			try {
				return ns.columnName(getSpecifiedName());
			} catch (Exception e) {
				IMessage m = HibernateJpaValidationMessage.buildMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, this);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return this.getSpecifiedName();
	}
	
	public String getDefaultDBColumnName() {
		return getDefaultName();
	}
	
	@Override
	public void validate(List<IMessage> messages, IReporter reporter) {
		super.validate(messages, reporter);
		if (this.connectionProfileIsActive()) {
			if ( ! this.isResolved()) {
				messages.add(
					HibernateJpaValidationMessage.buildMessage(
						IMessage.HIGH_SEVERITY,
						JpaValidationMessages.DISCRIMINATOR_COLUMN_UNRESOLVED_NAME,
						new String[] {this.getDBColumnName()}, 
						this,
						this.getNameTextRange()
					)
				);
			}
		}
	}
}
