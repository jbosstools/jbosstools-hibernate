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

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;


/**
 * @author alex
 *
 */
public interface ICollectionMapping extends IHibernateValueMapping {
	public abstract String getName();

	public abstract boolean isSet();

	public abstract IHibernateKeyMapping getKey();

	public abstract IHibernateValueMapping getElement();

	public abstract boolean isIndexed();

	public abstract IDatabaseTable getCollectionTable();

	public abstract void setCollectionTable(IDatabaseTable table);

	public abstract String getSort();

	public abstract boolean isLazy();

	public abstract void setLazy(boolean lazy);

	public abstract String getRole();

	public abstract boolean isPrimitiveArray();

	public abstract boolean isArray();

	public abstract boolean hasFormula();

	public abstract boolean isOneToMany();

	public abstract boolean isInverse();

	public abstract String getOwnerEntityName();
	public IHibernateClassMapping getOwner();
	public void setOwner(IHibernateClassMapping owner);

	public abstract String getOrderBy();

	public abstract void setElement(IHibernateValueMapping element);

	public abstract void setKey(IHibernateKeyMapping key);

	public abstract void setOrderBy(String orderBy);

	public abstract void setRole(String role);

	public abstract void setSort(String sort);

	public abstract void setInverse(boolean inverse);

	public abstract String getWhere();

	public abstract void setWhere(String where);

	public abstract boolean isIdentified();

	public abstract int getBatchSize();

	public abstract void setBatchSize(int i);

	public abstract String getFetchMode();

	public abstract void setFetchMode(String fetchMode);

	public abstract Iterator<IDatabaseColumn> getColumnIterator();

	public abstract int getColumnSpan();

	public abstract boolean isNullable();

	public abstract boolean isAlternateUniqueKey();

	public abstract IDatabaseTable getTable();

	public abstract boolean isSimpleValue();

	//cache - related methods
	public abstract String getCacheConcurrencyStrategy();
	public abstract void setCacheConcurrencyStrategy(
			String cacheConcurrencyStrategy);
	public abstract String getCacheRegionName();
	public abstract void setCacheRegionName(String cacheRegionName);

	public abstract String getCustomSQLDelete();

	public abstract void setCustomSQLDelete(String customSQLDelete,
			boolean callable);

	public abstract String getCustomSQLDeleteAll();

	public abstract void setCustomSQLDeleteAll(String customSQLDeleteAll,
			boolean callable);

	public abstract String getCustomSQLInsert();

	public abstract void setCustomSQLInsert(String customSQLInsert,
			boolean callable);

	public abstract String getCustomSQLUpdate();

	public abstract void setCustomSQLUpdate(String customSQLUpdate,
			boolean callable);

	public abstract boolean isCustomDeleteCallable();

	public abstract boolean isCustomDeleteAllCallable();

	public abstract boolean isCustomInsertCallable();

	public abstract boolean isCustomUpdateCallable();

	public abstract void addFilter(String name, String condition);

	public abstract java.util.Map getFilterMap();

	public abstract String getLoaderName();

	public abstract void setLoaderName(String name);

	public abstract String getReferencedPropertyName();

	public abstract void setReferencedPropertyName(String propertyRef);

	public abstract boolean isOptimisticLocked();

	public abstract void setOptimisticLocked(boolean optimisticLocked);

	public abstract boolean isMap();

	public abstract Type getType();

	public abstract void setType(Type type);
	
	public String getTypeName();
	public void setTypeName(String typeName);
	public String getSubselect();
	public void setSubselect(String subselect); 
	public String getCascade();
	public void setCascade(String cascade); 
	public String getSynchronizedTables();
	public void setSynchronizedTables(String synchronizedTables);
	public String getCheck();;
	public void setCheck(String check);
	public String getPersister();
	public void setPersister(String persister);
	
	//add tau 31.03.2006
	public boolean isDirtyCacheConcurrencyStrategy();
	public void setDirtyCacheConcurrencyStrategy(boolean dirtyCacheConcurrencyStrategy);
	
}