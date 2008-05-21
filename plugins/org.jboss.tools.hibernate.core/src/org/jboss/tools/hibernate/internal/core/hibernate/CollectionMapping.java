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
package org.jboss.tools.hibernate.internal.core.hibernate;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.AbstractOrmElement;
import org.jboss.tools.hibernate.internal.core.AbstractValueMapping;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.CollectionMappingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.CollectionMappingDescriptorsHolderWithTable;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.CollectionMappingFKWithRefDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.CollectionMappingFKWithTextRefDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.PropertyMappingForCollectionMapingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;



/**
 * Mapping for a collection. Subclasses specialize to particular
 * collection styles.
 */
public abstract class CollectionMapping extends AbstractValueMapping implements ICollectionMapping
{
    private IHibernateKeyMapping key;
	private IHibernateValueMapping element;
	private IDatabaseTable collectionTable;
	private String role;
	private boolean lazy;
	private boolean inverse;
	private String cacheConcurrencyStrategy;
	private String cacheRegionName;
	private String orderBy;
	private String where;
	private IHibernateClassMapping owner;
	private String referencedPropertyName;
	private String sort="unsorted";
	private String cascade;
	private int batchSize=1;
	private String fetchMode;
	private boolean optimisticLocked = true;
	private Type type;
	private final java.util.Map<String,String> filters = new HashMap<String,String>();

	private String customSQLInsert;
	private String customSQLUpdate;
	private String customSQLDelete;
	private String customSQLDeleteAll;
	private boolean customInsertCallable;
	private boolean customUpdateCallable;
	private boolean customDeleteCallable;
	private boolean customDeleteAllCallable;
	
	private String loaderName;
	private String typeName;
	private String subselect;
	private String synchronizedTables;
	private String check;
//akuzmin 30.05.2005
	private String persister;
	
	//added by Nick 6.04.2005
	//this field is here to hold collection's elements class name...
	private String collectionElementClassName;
	
	//add tau 31.03.2006
	private boolean dirtyCacheConcurrencyStrategy;
	
	//by Nick
	
	protected CollectionMapping(IHibernateClassMapping owner) {
		this.owner = owner;
	}

	public String getName(){
		if(type!=null)return type.getName();
		return "Collection mapping";
	}
	public boolean isSet() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentValueMapping#clear()
	 */
	public void clear() {
		if(key!=null) key.clear();
		if(element!=null) element.clear();
	}

	public IHibernateKeyMapping getKey() {
		return key;
	}
	//akuzmin 04.05.2005
	public String getKeyColumn() {
		if ((key!=null)&&(key.getColumnSpan()>0))
		{
			String keyColumns=null;
			Column fkcolumn=null;
			Iterator iter=key.getColumnIterator();
			while (iter.hasNext())
			{
				fkcolumn=(Column)iter.next();
				if (key.getTable().getColumn(fkcolumn.getName())!=null)
					if (keyColumns!=null) 
						keyColumns=keyColumns+","+fkcolumn.getName();
					else keyColumns=fkcolumn.getName();
			}
			return keyColumns;
		}
		return null;
	}
	
	//akuzmin 04.05.2005
	public void setKeyColumn(String keyColumns) {
		if (key instanceof SimpleValueMapping)
		{
			String[] keyNames = new String[0];
			if (keyColumns!=null)
				keyNames = keyColumns.split(",");
			if ((keyNames!=null)&&(keyNames.length>=0))
			{
				Column col;
				while(key.getColumnIterator().hasNext())
				{
					col=(Column)key.getColumnIterator().next();
					((SimpleValueMapping)key).removeColumn(col);
				}
				for(int i=0;i<keyNames.length;i++)
					if (key.getTable().getColumn(keyNames[i])!=null)
						((SimpleValueMapping)key).addColumn(key.getTable().getColumn(keyNames[i]));
			}
		}
	}
	
	public IHibernateValueMapping getElement() {
		return element;
	}
	public boolean isIndexed() {
		return false;
	}
	public IDatabaseTable getCollectionTable() {
		return collectionTable;
	}
	public void setCollectionTable(IDatabaseTable table) {
		this.collectionTable = table;
		{
		 if (this.getElement()!=null)
			 this.getElement().setTable(table);
		 if ((this instanceof IndexedCollectionMapping)&&(((IndexedCollectionMapping)this).getIndex()!=null))
			 ((IndexedCollectionMapping)this).getIndex().setTable(table);
		}
	}
	public String getSort() {
		return sort;
	}
	
	public boolean isLazy() {
		return lazy;
	}
	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public String getRole() {
		return role;
	}
	
	public boolean isPrimitiveArray() {
		return false;
	}

	public boolean isArray() {
		return false;
	}

	public boolean hasFormula() {
		return false;
	}

	public boolean isOneToMany() {
		return element instanceof OneToManyMapping;
	}

	public boolean isInverse() {
		return inverse;
	}

	public String getOwnerEntityName() {
		return owner.getEntityName();
	}

	public String getOrderBy() {
		return orderBy;
	}


	public void setElement(IHibernateValueMapping element) {
		if(element instanceof AbstractOrmElement){
			((AbstractOrmElement)element).setName( "element");
		}
		this.element = element;
	}

	public void setKey(IHibernateKeyMapping key) {
		if(key instanceof AbstractOrmElement){
			((AbstractOrmElement)key).setName( "key");
		}
//		this.element = element;
		this.key = key;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	public IHibernateClassMapping getOwner() {
		return owner;
	}

	public void setOwner(IHibernateClassMapping owner) {
		this.owner = owner;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public boolean isIdentified() {
		return false;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int i) {
		batchSize = i;
	}

	public String getFetchMode() {
		return fetchMode;
	}

	public void setFetchMode(String fetchMode) {
		this.fetchMode=fetchMode;
	}

	public Iterator<IDatabaseColumn> getColumnIterator() {
		return new Iterator<IDatabaseColumn>() {
			public boolean hasNext() {
				return false;
			}
			public IDatabaseColumn next() {
				return null;
			}
			public void remove() {
			}
		};
	}
	public int getColumnSpan() {
		return 0;
	}
	
	public boolean isNullable() {
		return true;
	}
	public boolean isAlternateUniqueKey() {
		return false;
	}
	public IDatabaseTable getTable() {
		return owner.getDatabaseTable();
	}
//	akuzmin 12.05.2005
	public String getTableName() {
		if (getTable()!=null)
			return getTable().getName();
		else return null;
	}	

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping#setTable(org.jboss.tools.hibernate.core.IDatabaseTable)
	 */
	public void setTable(IDatabaseTable table) {
		throw new RuntimeException("Illegal operation for the collection mapping");
	}
//akuzmin 12.06.2005
	public void createForeignKey() 
	{
		setKey(new SimpleValueMapping(getCollectionTable()));
	}

	public boolean isSimpleValue() {
		return false;
	}


	public String getCacheConcurrencyStrategy() {
		return cacheConcurrencyStrategy;
	}

	public void setCacheConcurrencyStrategy(String cacheConcurrencyStrategy) {
		//edit tau 31.03.2006
		if (this.cacheConcurrencyStrategy != null){
			if (!this.cacheConcurrencyStrategy.equals(cacheConcurrencyStrategy)){
				setDirtyCacheConcurrencyStrategy(true);				
			}
		} else if (cacheConcurrencyStrategy != null){
			setDirtyCacheConcurrencyStrategy(true);
		}
		this.cacheConcurrencyStrategy = cacheConcurrencyStrategy;
	}		

	public synchronized boolean isDirtyCacheConcurrencyStrategy() {
		return dirtyCacheConcurrencyStrategy;
	}

	public synchronized void setDirtyCacheConcurrencyStrategy(
			boolean dirtyCacheConcurrencyStrategy) {
		this.dirtyCacheConcurrencyStrategy = dirtyCacheConcurrencyStrategy;
	}

	public String getCacheRegionName() {
		return cacheRegionName==null ? role : cacheRegionName;
	}
	public void setCacheRegionName(String cacheRegionName) {
		//edit tau 31.03.2006
		if (this.cacheRegionName != null){
			if (!this.cacheRegionName.equals(cacheRegionName)){
				setDirtyCacheConcurrencyStrategy(true);				
			}
		} else if (cacheRegionName != null){
			setDirtyCacheConcurrencyStrategy(true);
		}
		this.cacheRegionName = cacheRegionName;		
		
	}

	public String getCustomSQLDelete() {
		return customSQLDelete;
	}

	public void setCustomSQLDelete(String customSQLDelete, boolean callable) {
		this.customSQLDelete = customSQLDelete;
		this.customDeleteCallable = callable;
	}

	public String getCustomSQLDeleteAll() {
		return customSQLDeleteAll;
	}

	public void setCustomSQLDeleteAll(String customSQLDeleteAll, boolean callable) {
		this.customSQLDeleteAll = customSQLDeleteAll;
		this.customDeleteAllCallable = callable;
	}

	public String getCustomSQLInsert() {
		return customSQLInsert;
	}

	public void setCustomSQLInsert(String customSQLInsert, boolean callable) {
		this.customSQLInsert = customSQLInsert;
		this.customInsertCallable = callable;
	}

	public String getCustomSQLUpdate() {
		return customSQLUpdate;
	}

	public void setCustomSQLUpdate(String customSQLUpdate, boolean callable) {
		this.customSQLUpdate = customSQLUpdate;
		this.customUpdateCallable = callable;
	}

	public boolean isCustomDeleteCallable() {
		return customDeleteCallable;
	}

	public boolean isCustomDeleteAllCallable() {
		return customDeleteAllCallable;
	}

	public boolean isCustomInsertCallable() {
		return customInsertCallable;
	}

	public boolean isCustomUpdateCallable() {
		return customUpdateCallable;
	}

	public void addFilter(String name, String condition) {
		filters.put(name, condition);
	}

	public java.util.Map getFilterMap() {
		return filters;
	}

	public String toString() {
		return getClass().getName() + '(' + getRole() + ')';
	}
	
	public String getLoaderName() {
		return loaderName;
	}
	
	public void setLoaderName(String name) {
		this.loaderName = name;
	}

	public String getReferencedPropertyName() {
		return referencedPropertyName;
	}

	public void setReferencedPropertyName(String propertyRef) {
		this.referencedPropertyName = propertyRef;
	}
	
	public boolean isOptimisticLocked() {
		return optimisticLocked;
	}
	
	public void setOptimisticLocked(boolean optimisticLocked) {
		this.optimisticLocked = optimisticLocked;
	}

	public boolean isMap() {
		return false;
	}
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		return visitor.visitPersistentValueMapping(this,argument);
	}

	public String getCollectionElementClassName() {
		return collectionElementClassName;
	}
	
	public void setCollectionElementClassName(String collectionElementClassName) {
		this.collectionElementClassName = collectionElementClassName;
	}
//	akuzmin 21/04/2005	
	public IPropertySource2 getPropertySource()
	{
		BeanPropertySourceBase bp = new BeanPropertySourceBase(this);
		bp.setPropertyDescriptors(getPropertyDescriptorHolder());		
		return bp;
	}
	
//	akuzmin 30/05/2005	
	public IPropertySource2 getFKPropertySource()
	{
		BeanPropertySourceBase bp = new BeanPropertySourceBase(this);
		if ((getElement()!=null) && (getElement() instanceof OneToManyMapping))
			bp.setPropertyDescriptors(CollectionMappingFKWithRefDescriptorsHolder.getInstance(getCollectionTable(),owner));
		else 
			bp.setPropertyDescriptors(CollectionMappingFKWithTextRefDescriptorsHolder.getInstance(getCollectionTable()));
		return bp;
	}
	
//	akuzmin 05/05/2005
	public PropertyDescriptorsHolder getPropertyDescriptorHolder() {
		return CollectionMappingDescriptorsHolderWithTable.getInstance(getFieldMapping().getPersistentField());
	}
//	akuzmin 24/05/2005
	public PropertyDescriptorsHolder getPropertyMappingDescriptorHolder() {
		return PropertyMappingForCollectionMapingDescriptorsHolder.getInstance();
	}
//  akuzmin 22/09/2005	
	public PropertyDescriptorsHolder getPropertyDescriptorHolderWithOutTable()
	{
		return CollectionMappingDescriptorsHolder.getInstance();
	}
	

	/**
	 * @return Returns the typeName.
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @param typeName The typeName to set.
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * @return Returns the subselect.
	 */
	public String getSubselect() {
		return subselect;
	}

	/**
	 * @param subselect The subselect to set.
	 */
	public void setSubselect(String subselect) {
		this.subselect = subselect;
	}

	/**
	 * @return Returns the cascade.
	 */
	public String getCascade() {
		return cascade;
	}

	/**
	 * @param cascade The cascade to set.
	 */
	public void setCascade(String cascade) {
		this.cascade = cascade;
	}

	/**
	 * @return Returns the synchronizedTables.
	 */
	public String getSynchronizedTables() {
		return synchronizedTables;
	}

	/**
	 * @param synchronizedTables The synchronizedTables to set.
	 */
	public void setSynchronizedTables(String synchronizedTables) {
		this.synchronizedTables = synchronizedTables;
	}

	/**
	 * @return Returns the check.
	 */
	public String getCheck() {
		return check;
	}

	/**
	 * @param check The check to set.
	 */
	public void setCheck(String check) {
		this.check = check;
	}
	
//	akuzmin 30.05.2005
	/**
	 * @return Returns the persister.
	 */
	public String getPersister() {
		return persister;
	}
	
//	akuzmin 30.05.2005
	/**
	 * @param persister The persister to set.
	 */
	public void setPersister(String persister) {
		this.persister = persister;
	}


	
}
