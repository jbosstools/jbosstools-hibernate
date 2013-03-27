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

import org.eclipse.jpt.jpa.core.context.NamedColumn;
import org.eclipse.jpt.jpa.core.context.orm.OrmPersistentAttribute;
import org.eclipse.jpt.jpa.core.context.orm.OrmSpecifiedPersistentAttribute;
import org.eclipse.jpt.jpa.core.internal.context.orm.AbstractOrmBasicMapping;
import org.eclipse.jpt.jpa.core.resource.orm.XmlBasic;
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
public class HibernateOrmBasicMapping extends AbstractOrmBasicMapping<XmlBasic> {

	public HibernateOrmBasicMapping(OrmSpecifiedPersistentAttribute parent,
			XmlBasic resourceMapping) {
		super(parent, resourceMapping);
	}

	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}

	@Override
	public String getDefaultColumnName(NamedColumn column) {
		if (getName() != null){
			NamingStrategy ns = getJpaProject().getNamingStrategy();
			if (getJpaProject().isNamingStrategyEnabled() && ns != null) {
				try {
					return ns.propertyToColumnName(getName());
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

}
