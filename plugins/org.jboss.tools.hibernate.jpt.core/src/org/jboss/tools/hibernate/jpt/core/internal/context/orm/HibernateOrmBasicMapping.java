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

import org.eclipse.jpt.core.context.orm.OrmPersistentAttribute;
import org.eclipse.jpt.core.internal.context.orm.GenericOrmBasicMapping;
import org.eclipse.jpt.core.resource.orm.XmlBasic;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;

/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
public class HibernateOrmBasicMapping extends GenericOrmBasicMapping<XmlBasic> {

	public HibernateOrmBasicMapping(OrmPersistentAttribute parent,
			XmlBasic resourceMapping) {
		super(parent, resourceMapping);
	}

	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}

	@Override
	public String getDefaultColumnName() {
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (ns != null) {
			return ns.propertyToColumnName(getName());
		}
		return super.getDefaultColumnName();
	}

}
