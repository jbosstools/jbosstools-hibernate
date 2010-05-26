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
import org.eclipse.jpt.core.internal.context.orm.GenericOrmManyToManyMapping;
import org.eclipse.jpt.core.resource.orm.XmlManyToMany;
import org.jboss.tools.hibernate.jpt.core.internal.context.NamingStrategyMappingTools;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmManyToManyMapping<T extends XmlManyToMany> extends
		GenericOrmManyToManyMapping<T> {
	
	public HibernateOrmManyToManyMapping(OrmPersistentAttribute parent,
			T resourceMapping) {
		super(parent, resourceMapping);
	}
	
	@Override
	public String getJoinTableDefaultName() {
		return NamingStrategyMappingTools.buildJoinTableDefaultName(this);
	}

}
