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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IJoinMapping;
import org.jboss.tools.hibernate.core.hibernate.IMetaAttribute;
import org.jboss.tools.hibernate.core.hibernate.IOneToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMappingHolder;
import org.jboss.tools.hibernate.core.hibernate.IRootClassMapping;
import org.jboss.tools.hibernate.internal.core.AbstractOrmElement;
import org.jboss.tools.hibernate.internal.core.CompoundIterator;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.SchemaSynchronizationHelper;


/**
 * @author alex
 *
 * Mapping for an entity class.
 * @see org.hibernate.mapping.PersistentClass
 */
public abstract class ClassMapping extends AbstractOrmElement implements IHibernateClassMapping /* 9.06.2005 by Nick*/, IPropertyMappingHolder /* by Nick */
{

	private IPersistentClass persistentClass;
	private String entityName;
	private boolean dynamic = false;
	private String discriminatorValue;
	private ArrayList<IPropertyMapping> properties = new ArrayList<IPropertyMapping>();
	private String proxyInterfaceName;
	private ArrayList<IHibernateClassMapping> subclasses = new ArrayList<IHibernateClassMapping>();
	private boolean dynamicInsert;
	private boolean dynamicUpdate;
	private int batchSize=1;
	private boolean selectBeforeUpdate;
	private String optimisticLockMode;
	private java.util.Map metaAttributes;
	private ArrayList<IJoinMapping> joins = new ArrayList<IJoinMapping>();
	private java.util.Map<String,String> filters = new HashMap<String,String>();
	private String loaderName;
	private boolean isAbstract;
	private boolean lazy;
	private String check;
	//private List checkConstraints = new ArrayList();
	private String rowId;
	private String subselect;
	private String synchronizedTables;
	private String extendsClass; // from extends attribute
	private String persisterClassName;

	// Custom SQL
	private String customSQLInsert;
	private String customSQLUpdate;
	private String customSQLDelete;
	private boolean customInsertCallable;
	private boolean customUpdateCallable;
	private boolean customDeleteCallable;

	private IMappingStorage storage;
	//akuzmin 15/03/2005
	public abstract IPropertySource2 getPropertySource();
	
	/**
	 * @param persistentClass The persistentClass to set.
	 */
	public void setPersistentClass(IPersistentClass persistentClass) {
		this.persistentClass = persistentClass;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClassMapping#getPersistentClass()
	 */
	public IPersistentClass getPersistentClass() {
		return persistentClass;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClassMapping#getDatabaseTable()
	 */
	public abstract IDatabaseTable getDatabaseTable();

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClassMapping#getStorage()
	 */
	public IMappingStorage getStorage() {
		return storage;
	}

	/**
	 * @param storage The storage to set.
	 */
	public void setStorage(IMappingStorage storage) {
		this.storage = storage;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClassMapping#getFieldMappings()
	 */
	public Iterator<IPropertyMapping> getFieldMappingIterator() {
		return getPropertyIterator();
	}


	public boolean getDynamicInsert() {
		return dynamicInsert;
	}

	public boolean getDynamicUpdate() {
		return dynamicUpdate;
	}

	public void setDynamicInsert(boolean dynamicInsert) {
		this.dynamicInsert = dynamicInsert;
	}

//	public boolean getDynamicInsert() {
//		return dynamicInsert;
//	}
	

	public void setDynamicUpdate(boolean dynamicUpdate) {
		this.dynamicUpdate = dynamicUpdate;
	}
	
//	public boolean getDynamicUpdate() {
//		return dynamicUpdate;
//	}


	public String getDiscriminatorValue() {
		return discriminatorValue;
	}

	
	public void addSubclass(IHibernateClassMapping subclass) {
		// inheritance cycle detection (paranoid check)
		IHibernateClassMapping superclass = getSuperclass();
		while (superclass!=null) {
			if( subclass.getEntityName().equals( superclass.getEntityName() ) ) {
				throw new RuntimeException(
					"Circular inheritance mapping detected: " +
					subclass.getEntityName() +
					" will have it self as superclass when extending " +
					getEntityName()
				);
			}
			superclass = superclass.getSuperclass();
		}
		subclasses.add(subclass);
	}

	public boolean hasSubclasses() {
		return subclasses.size() > 0;
	}

	public int getSubclassSpan() {
		int n = subclasses.size();
		Iterator iter = subclasses.iterator();
		while ( iter.hasNext() ) {
			n += ( (SubclassMapping) iter.next() ).getSubclassSpan();
		}
		return n;
	}
	

	public Iterator getDirectSubclasses() {
		return subclasses.iterator();
	}

	public void addProperty(IPropertyMapping p) {
		if (p == null)
            return ;
        
		// changed by yk 19.09.2005
		List<IPropertyMapping> thelist = Collections.synchronizedList(properties);
		synchronized(thelist) {
	     /* rem by yk 19.09.2005   properties.add(p); */
			thelist.add(p);
			// changed by Nick 09.06.2005
			if (p instanceof PropertyMapping) {
	            ((PropertyMapping)p).setPersistentClassMapping(this);
	        }
	        p.setPropertyMappingHolder(this);
	        // by Nick
		}// synchronized.
	}

	public String getEntityName() {
		return entityName==null?getName():entityName;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping#getPersisterClassName()
	 */
	public String getPersisterClassName() {
		return persisterClassName;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping#setPersisterClassName(java.lang.String)
	 */
	public void setPersisterClassName(String persisterClassName) {
		this.persisterClassName=persisterClassName;
	}

	public abstract boolean isMutable();
	public abstract boolean hasIdentifierProperty();
	public abstract IPropertyMapping getIdentifierProperty();
	public abstract IHibernateKeyMapping getIdentifier();
	public abstract IPropertyMapping getVersion();
	//akuzmin 03.05.2005
	public abstract String getVersionColumnName();	
	public abstract IHibernateValueMapping getDiscriminator();
	//akuzmin 03.05.2005
	public abstract String getDiscriminatorColumnName();
	public abstract boolean isInherited();
	public abstract boolean isPolymorphic();
	public abstract boolean isVersioned();
	public abstract String getCacheConcurrencyStrategy();
	public abstract IHibernateClassMapping getSuperclass();
	public abstract boolean isExplicitPolymorphism();
	public abstract boolean isDiscriminatorInsertable();
	
	public abstract Iterator<IPropertyMapping> getPropertyClosureIterator();
	/*
	public abstract Iterator getTableClosureIterator();
	public abstract Iterator getKeyClosureIterator();
	*/

	public String getProxyInterfaceName() {
		return proxyInterfaceName;
	}

	public boolean isClassOrSuperclassJoin(IJoinMapping join) {
		return joins.contains(join);
	}

	public boolean isClassOrSuperclassTable(IDatabaseTable closureTable) {
		return getDatabaseTable()==closureTable;
	}
	
	public boolean isLazy() {
		return lazy;
	}
	/**
	 * @param lazy The lazy to set.
	 */
	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public abstract boolean hasEmbeddedIdentifier();
	public abstract IDatabaseTable getRootTable();
	public abstract IRootClassMapping getRootClass();
	public abstract IHibernateKeyMapping getKey();

	public void setDiscriminatorValue(String discriminatorValue) {
		this.discriminatorValue = discriminatorValue;
	}

	public void setEntityName(String persistentClass) {
		this.entityName = persistentClass;
	}

	public void setProxyInterfaceName(String proxyInterface) {
		this.proxyInterfaceName = proxyInterface;
	}

	/*
	public void createPrimaryKey() {
		//Primary key constraint
		PrimaryKey pk = new PrimaryKey();
		Table table = getTable();
		pk.setTable(table);
		pk.setName( PK_ALIAS.toAliasString( table.getName() ) );
		table.setPrimaryKey(pk);

		pk.addColumns( getKey().getColumnIterator() );
	}*/

	public abstract String getWhere();
//akuzmin
	public abstract boolean isClass();	

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public boolean hasSelectBeforeUpdate() {
		return selectBeforeUpdate;
	}

	public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
		this.selectBeforeUpdate = selectBeforeUpdate;
	}
	public boolean getSelectBeforeUpdate() {
		return selectBeforeUpdate;
	}
	

	public IPropertyMapping getProperty(String propertyName) {
		Iterator iter = getPropertyClosureIterator();
		while ( iter.hasNext() ) {
			PropertyMapping prop = (PropertyMapping) iter.next();
			if ( prop.getName().equals(propertyName) ) return prop;
		}
		throw new RuntimeException("property not found: " + propertyName);
	}

	public String getOptimisticLockMode() {
		return optimisticLockMode;
	}

	public void setOptimisticLockMode(String optimisticLockMode) {
		this.optimisticLockMode = optimisticLockMode;
	}


	public java.util.Map getMetaAttributes() {
		return metaAttributes;
	}

	public void setMetaAttributes(java.util.Map metas) {
		this.metaAttributes = metas;
	}

	public IMetaAttribute getMetaAttribute(String name) {
		return metaAttributes==null?null:(MetaAttribute) metaAttributes.get(name);
	}

	public String getClassName() {
		return getName();
	}

	public void setClassName(String className) {
		setName(className);
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	public boolean isDynamic() {
		return dynamic;
	}

	public String toString() {
		return getClass().getName() + '(' + getEntityName() + ')';
	}
	
	public Iterator getJoinIterator() {
		return joins.iterator();
	}

	public Iterator getJoinClosureIterator() {
		return joins.iterator();
	}

	public void addJoin(IJoinMapping join) {
		joins.add(join);
		join.setPersistentClass(this);
	}

	public void removeJoin(IJoinMapping join) {
		joins.remove(join);
		join.clear();
		join.setPersistentClass(null);
	}

	public int getJoinClosureSpan() {
		return joins.size();
	}

	public int getPropertyClosureSpan() {
		int span = properties.size();
		for ( int i=0; i<joins.size(); i++ ) {
			JoinMapping join = (JoinMapping) joins.get(i);
			span += join.getPropertySpan();
		}
		return span;
	}


	public Iterator<IPropertyMapping> getPropertyIterator() {
		//add tau -> ESORM-582: NullPointerException - orm2.core.CodeRendererService.importTypeName
		// del tau 23.05.2006 
		//this.getPersistentClass().getFields();
		
		ArrayList<Iterator<IPropertyMapping>> iterators = new ArrayList<Iterator<IPropertyMapping>>();
		iterators.add( properties.iterator() );
		for ( int i = 0; i < joins.size(); i++ ) {
			JoinMapping join = (JoinMapping) joins.get(i);
			iterators.add( join.getPropertyIterator() );
		}
		return new CompoundIterator<IPropertyMapping>(iterators);
	}

	public Iterator getUnjoinedPropertyIterator() {
		return properties.iterator();
	}
	
	public String getCustomSQLDelete() {
		return customSQLDelete;
	}

	public void setCustomSQLDelete(String customSQLDelete, boolean callable) {
		this.customSQLDelete = customSQLDelete;
		this.customDeleteCallable = callable;
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

	public boolean isCustomInsertCallable() {
		return customInsertCallable;
	}

	public boolean isCustomUpdateCallable() {
		return customUpdateCallable;
	}

	public void addFilter(String name, String condition) {
		filters.put(name, condition);
	}

	public java.util.Map<String,String> getFilterMap() {
		return filters;
	}

	public boolean isForceDiscriminator() {
		return false;
	}

	public abstract boolean isJoinedSubclass();

	public String getLoaderName() {
		return loaderName;
	}

	public void setLoaderName(String loaderName) {
		this.loaderName = loaderName;
	}

	public void setIsAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	public boolean getIsAbstract() {
		return isAbstract;
	}

	protected Iterator getNonDuplicatedPropertyIterator() {
		return getUnjoinedPropertyIterator();
	}
	
	protected Iterator getDiscriminatorColumnIterator() {
		return Collections.EMPTY_LIST.iterator();
	}
	
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClassMapping#getSuperclassMapping()
	 */
	public IPersistentClassMapping getSuperclassMapping() {
		return getSuperclass();
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		return visitor.visitPersistentClassMapping(this,argument);
	}

	/**
	 * @param check The check to set.
	 */
	public void setCheck(String check) {
		this.check = check;
	}
	/**
	 * @return Returns the check.
	 */
	
	public String getCheck() {
		return check;
	}
	
	/**
	 * @return Returns the rowId.
	 */
	public String getRowId() {
		return rowId;
	}
	/**
	 * @param rowId The rowId to set.
	 */
	public void setRowId(String rowId) {
		this.rowId = rowId;
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
	/*
	public void addCheckConstraint(String check){
		checkConstraints.add(check);
	}
	public List getCheckContraints(){
		return checkConstraints;
	}*/
	
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

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClassMapping#deleteFieldMapping(java.lang.String)
	 */
	public void deleteFieldMapping(String fieldName) {
		/* rem by yk 19.09.2005
		 Iterator it=properties.iterator(); */
		List<IPropertyMapping> thelist = Collections.synchronizedList(properties);
		synchronized(thelist) {
			IPropertyMapping map=null;
			Iterator<IPropertyMapping> it = SchemaSynchronizationHelper.<IPropertyMapping>getIteratorCopy(thelist.iterator());
			while(it.hasNext()){
				map=(IPropertyMapping) it.next();
				if(fieldName.equals(map.getName())){
				    // changed by Nick 10.06.2005 - code moved to #removeProperty
	                // because removeProperty = deleteFieldMapping for that property
	//                properties.remove(map);
	//				if(getIdentifierProperty()==map){
	//					this.setIdentifier(null);
	//					this.setIdentifierProperty(null);
	//				}
	//				if(getVersion()==map) setVersion(null);
	//				map.clear();
	//				return;
				    removeProperty(map);
	                //by Nick
				}
			}
			for ( int i=0; i<joins.size(); i++ ) {
				JoinMapping join = (JoinMapping) joins.get(i);
				it= join.getPropertyIterator();
				while(it.hasNext()){
					map=(PropertyMapping) it.next();
					if(fieldName.equals(map.getName())){
						join.removeProperty(map);
						map.clear();
						if(join.getPropertySpan()==0) removeJoin(join);
						return;
					}
				}
			}
		}// synchronized.
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClassMapping#clear()
	 */
	public void clear() {
		/* rem by yk 19.09.2005
		 Iterator it=properties.iterator();   */
		
		// changed by yk 19.09.2005
		List thelist = Collections.synchronizedList(properties);
		synchronized(thelist)
		{
			Iterator it = thelist.iterator();
			IPropertyMapping map=null;
			while(it.hasNext()){
				map=(IPropertyMapping) it.next();
				map.clear();
			}
			properties.clear();
			for ( int i=0; i<joins.size(); i++ ) {
				JoinMapping join = (JoinMapping) joins.get(i);
				join.clear();
			}
			joins.clear();
			if(isClass()){
				if(getIdentifier()!=null)getIdentifier().clear();
				if(getDiscriminator()!=null){
					getDiscriminator().clear();
				}
				setDiscriminator(null);
				setIdentifier(null);
				setIdentifierProperty(null); //by Nick 19.04.2005
				setVersion(null);
			}
			
			//add tau 30.03.2006
			getStorage().setDirty(true);
			
		}// synchronized.
	}

	/**
	 * @return Returns the extendsClass.
	 */
	public String getExtends() {
		return extendsClass;
	}

	/**
	 * @param extendsClass The extendsClass to set.
	 */
	public void setExtends(String extendsClass) {
		this.extendsClass = extendsClass;
	}


	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping#copyFrom(org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping)
	 */
	public void copyFrom(IHibernateClassMapping hcm) {
		ClassMapping cm=(ClassMapping)hcm;
		entityName=cm.entityName;
		dynamic = cm.dynamic;
		discriminatorValue=cm.discriminatorValue;
		properties = cm.properties;
		proxyInterfaceName=cm.proxyInterfaceName;
		subclasses = cm.subclasses;
		dynamicInsert= cm.dynamicInsert;
		dynamicUpdate=cm.dynamicUpdate;
		batchSize=cm.batchSize;
		selectBeforeUpdate=cm.selectBeforeUpdate;
		optimisticLockMode=cm.optimisticLockMode;
		metaAttributes=cm.metaAttributes;
		joins = cm.joins;
		filters = cm.filters;
		loaderName=cm.loaderName;
		isAbstract=cm.isAbstract;
		lazy=cm.lazy;
		check=cm.check;
		rowId=cm.rowId;
		subselect=cm.subselect;
		synchronizedTables=cm.synchronizedTables;
		persisterClassName=cm.getPersisterClassName();
		
		//extendsClass = cm.extendsClass; may be another class
		customSQLInsert=cm.customSQLInsert;
		customSQLUpdate=cm.customSQLUpdate;
		customSQLDelete=cm.customSQLDelete;
		customInsertCallable=cm.customInsertCallable;
		customUpdateCallable=cm.customUpdateCallable;
		customDeleteCallable=cm.customDeleteCallable;
		copyFieldMappings();
	}

	protected void copyFieldMappings() {
		Iterator it=properties.iterator();
		PropertyMapping map=null;
		while(it.hasNext()){
			map=(PropertyMapping) it.next();
			map.setPersistentClassMapping(this);
			// added by Nick 10.06.2005
			map.setPropertyMappingHolder(this);
            // by Nick
        }
		for ( int i=0; i<joins.size(); i++ ) {
			JoinMapping join = (JoinMapping) joins.get(i);
			join.setPersistentClass(this);
			it= join.getPropertyIterator();
			while(it.hasNext()){
				map=(PropertyMapping) it.next();
				map.setPersistentClassMapping(this);
				// added by Nick 10.06.2005
				map.setPropertyMappingHolder(join);
                // by Nick
            }
		}
		for ( int i=0; i<subclasses.size(); i++ ) {
			SubclassMapping subclass = (SubclassMapping) subclasses.get(i);
			subclass.setSuperclass(this);
		}
	}

	public void removeProperty(IPropertyMapping old_mapping) {
		// changed by yk 19.09.2005
		List thelist = Collections.synchronizedList(properties);
		synchronized(thelist)
		{
	    //properties.remove(old_mapping);
			thelist.remove(old_mapping);
	    // added by Nick 10.06.2005
        if(getIdentifierProperty()==old_mapping){
	        this.setIdentifier(null);
	        this.setIdentifierProperty(null);
	    }
	    if(getVersion()==old_mapping) setVersion(null);
// added by yk 08.07.2005
	    if(!(old_mapping.getValue() instanceof IOneToOneMapping))
// added by yk 08.07.2005 stop
	    	old_mapping.clear();
        // by Nick
		}
    }

	public void renameProperty(IPropertyMapping prop, String newName) {
		((PropertyMapping)prop).setName(newName);
    }

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClassMapping#renameFieldMapping(java.lang.String, java.lang.String)
	 */
	public void renameFieldMapping(IPersistentFieldMapping fm, String newFieldName) {
		renameProperty((IPropertyMapping)fm,newFieldName );
		
	}
	
	// added by Nick 16.06.2005
	public boolean equals(Object object)
    {
     if (object instanceof ClassMapping)
     {
         if (object == null)
             return false;
         
         ClassMapping mapping = (ClassMapping) object;
         if (this.getPersistentClass() != null 
                 && this.getPersistentClass().equals(mapping.getPersistentClass()))
             return true;
         else
             return false;
     }
     else
         return false;
    }
    // by Nick

	
}
