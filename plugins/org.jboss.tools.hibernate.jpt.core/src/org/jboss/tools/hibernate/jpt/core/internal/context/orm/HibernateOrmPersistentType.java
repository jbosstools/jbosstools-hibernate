/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.orm;

import org.eclipse.jpt.jpa.core.context.AccessType;
import org.eclipse.jpt.jpa.core.context.orm.EntityMappings;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.orm.GenericOrmPersistentType;
import org.eclipse.jpt.jpa.core.resource.orm.XmlTypeMapping;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmPersistentType extends GenericOrmPersistentType {

	public HibernateOrmPersistentType(EntityMappings parent,
			XmlTypeMapping resourceMapping) {
		super(parent, resourceMapping);
	}

	protected AccessType buildDefaultAccess() {
		if ( ! this.mapping.isMetadataComplete()) {
			if (this.javaPersistentType != null) {
				if (this.javaPersistentTypeHasSpecifiedAccess()) {
					return this.javaPersistentType.getAccess();
				}
				if (this.superPersistentType != null) {
					return this.superPersistentType.getAccess();
				}
			}
		}
		AccessType access = this.getMappingFileRoot().getAccess();
		//fix for https://jira.jboss.org/browse/JBIDE-6538
		return (access != null) ? access : AccessType.PROPERTY; //default to PROPERTY if no specified access found
	}


}
