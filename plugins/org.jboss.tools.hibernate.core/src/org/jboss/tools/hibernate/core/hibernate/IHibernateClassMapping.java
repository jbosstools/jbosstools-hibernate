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
import org.jboss.tools.hibernate.core.IPersistentClassMapping;




/**
 * @author alex
 *
 */
public interface IHibernateClassMapping extends IPersistentClassMapping {
	public boolean getDynamicInsert();

	public boolean getDynamicUpdate();

	public void setDynamicInsert(boolean dynamicInsert);

	public void setDynamicUpdate(boolean dynamicUpdate);

	//subclasses
	public boolean hasSubclasses();
	public int getSubclassSpan();
	public Iterator getDirectSubclasses();
	public void addSubclass(IHibernateClassMapping subclass);

	//super classes
	public IHibernateClassMapping getSuperclass();
	
	public String getEntityName();

	public boolean isMutable();

	//Id props
	public boolean hasIdentifierProperty();

	public IPropertyMapping getIdentifierProperty();
	public void setIdentifierProperty(IPropertyMapping idMapping);

	public IHibernateKeyMapping getIdentifier();
	public void setIdentifier(IHibernateKeyMapping id);
	
	public boolean hasEmbeddedIdentifier();

	//version props
	public IPropertyMapping getVersion();
	public void setVersion(IPropertyMapping version);
	//akuzmin 03.05.2005
	public String getVersionColumnName();
	
	//Discriminator props
	public IHibernateValueMapping getDiscriminator();
	public void setDiscriminator(IHibernateValueMapping mapping);
	public boolean isDiscriminatorInsertable();
	public String getDiscriminatorValue();
	public void setDiscriminatorValue(String discriminatorValue);
	//akuzmin 03.05.2005
	public String getDiscriminatorColumnName();

	public boolean isForceDiscriminator();

	public boolean isInherited();

	public boolean isPolymorphic();

	public boolean isVersioned();

	public String getCacheConcurrencyStrategy();


	public boolean isExplicitPolymorphism();


	public Iterator<IPropertyMapping> getPropertyClosureIterator();

	/*
	 public abstract Iterator getTableClosureIterator();
	 public abstract Iterator getKeyClosureIterator();
	 */public String getProxyInterfaceName();

	public boolean isLazy();


	public String getPersisterClassName();

	public void setPersisterClassName(String persisterClassName);

	public IHibernateKeyMapping getKey();


	public void setEntityName(String persistentClass);

	public void setProxyInterfaceName(String proxyInterface);

	 public String getWhere();

	public int getBatchSize();

	public void setBatchSize(int batchSize);

	public boolean hasSelectBeforeUpdate();

	public void setSelectBeforeUpdate(boolean selectBeforeUpdate);
	public boolean getSelectBeforeUpdate();	

	public IPropertyMapping getProperty(String propertyName);

	public String getOptimisticLockMode();

	public void setOptimisticLockMode(String optimisticLockMode);

	public java.util.Map getMetaAttributes();

	public void setMetaAttributes(java.util.Map metas);

	public IMetaAttribute getMetaAttribute(String name);

	public IRootClassMapping getRootClass();
	
	public IDatabaseTable getRootTable();
	
	public String getClassName();

	public void setClassName(String className);

	public void setDynamic(boolean dynamic);

	public boolean isDynamic();

	public int getPropertyClosureSpan();

	public Iterator<IPropertyMapping> getPropertyIterator();

	public Iterator getUnjoinedPropertyIterator();

	public String getCustomSQLDelete();

	public void setCustomSQLDelete(String customSQLDelete, boolean callable);

	public String getCustomSQLInsert();

	public void setCustomSQLInsert(String customSQLInsert, boolean callable);

	public String getCustomSQLUpdate();

	public void setCustomSQLUpdate(String customSQLUpdate, boolean callable);

	public boolean isCustomDeleteCallable();

	public boolean isCustomInsertCallable();

	public boolean isCustomUpdateCallable();

	public void addFilter(String name, String condition);

	public java.util.Map getFilterMap();

	public boolean isClass();
	public boolean isJoinedSubclass();
	public boolean isUnionSubclass();
	public boolean isSubclass();

	public String getLoaderName();

	public void setLoaderName(String loaderName);

	public void setIsAbstract(boolean isAbstract);
	public boolean getIsAbstract();
	
	//Joins
	public Iterator getJoinIterator();
	public void addJoin(IJoinMapping join);
	public int getJoinClosureSpan();
	//sync tables
	public String getSynchronizedTables();
	public void setSynchronizedTables(String synchronizedTables);
	//subselect
	public String getSubselect();
	public void setSubselect(String subselect);
	//check
	public void setCheck(String check);
	public String getCheck();
	//rowid
	public String getRowId();
	public void setRowId(String rowId);
	
	public void copyFrom(IHibernateClassMapping cm);
}