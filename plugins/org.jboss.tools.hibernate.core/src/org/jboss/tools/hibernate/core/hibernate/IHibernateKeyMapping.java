/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.core.hibernate;


/**
 * Represents an identifying key of a table: the value for primary key
 * of an entity, or a foreign key of a collection or join table or
 * joined subclass table.
 */

public interface IHibernateKeyMapping extends IHibernateValueMapping {
	public boolean isCascadeDeleteEnabled();
	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled);	
	public String getForeignKeyName();
	public void setForeignKeyName(String foreignKeyName);
	
	public boolean isNullable();
	public void setNullable(boolean nullable);
	public boolean isUpdateable();
	public void setUpdateable(boolean updateable);
	
}
