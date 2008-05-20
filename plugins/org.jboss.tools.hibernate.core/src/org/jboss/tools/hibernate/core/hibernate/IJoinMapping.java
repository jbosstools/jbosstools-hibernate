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

import java.util.Iterator;

import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IOrmElement;

public interface IJoinMapping extends IOrmElement/* 9.06.2005 by Nick*/, IPropertyMappingHolder /* by Nick */{

	public void addProperty(IPropertyMapping prop);

	public boolean containsProperty(IPropertyMapping prop);

	public Iterator<IPropertyMapping> getPropertyIterator();

	public IDatabaseTable getTable();

	public void setTable(IDatabaseTable table);

	public IHibernateKeyMapping getKey();

	public void setKey(IHibernateKeyMapping key);

	public IHibernateClassMapping getPersistentClass();

	public void setPersistentClass(IHibernateClassMapping persistentClass);

	public int getPropertySpan();

	public String getCustomSQLDelete();

	public void setCustomSQLDelete(String customSQLDelete, boolean callable);

	public String getCustomSQLInsert();

	public void setCustomSQLInsert(String customSQLInsert, boolean callable);

	public String getCustomSQLUpdate();

	public void setCustomSQLUpdate(String customSQLUpdate, boolean callable);

	public boolean isCustomDeleteCallable();

	public boolean isCustomInsertCallable();

	public boolean isCustomUpdateCallable();

	public boolean isSequentialSelect();

	public void setSequentialSelect(boolean deferred);

	public boolean isInverse();

	public void setInverse(boolean leftJoin);

	public boolean isLazy();

	public boolean isOptional();

	public void setOptional(boolean nullable);
	
	public void clear();

	public String getSubselect();
	public void setSubselect(String subselect);
	
}