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
import java.util.Iterator;

import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.IJoinMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.internal.core.AbstractOrmElement;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.CollectionMappingFKDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.JoinMapingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.CombinedBeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;



/**
 * A join to another table.
 */
public class JoinMapping extends AbstractOrmElement implements IJoinMapping {
	private static final long serialVersionUID = 1L;
	private ArrayList<IPropertyMapping> properties = new ArrayList<IPropertyMapping>();
	private IDatabaseTable table;
	private IHibernateKeyMapping key;
	private IHibernateClassMapping persistentClass;
	private boolean sequentialSelect;
	private boolean inverse;
	private boolean optional;

	private String subselect;
	
	// Custom SQL
	private String customSQLInsert;
	private String customSQLUpdate;
	private String customSQLDelete;
	private boolean customInsertCallable;
	private boolean customUpdateCallable;
	private boolean customDeleteCallable;

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#addProperty(org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping)
	 */
	public void addProperty(IPropertyMapping prop) {
		if (prop == null)
		    return ;
        
        properties.add(prop);
		//prop.setPersistentClass( getPersistentClass() );
		prop.setPropertyMappingHolder(this);
    }
	
	public void removeProperty(IPropertyMapping prop){
		properties.remove(prop);
		if (prop != null)
		    prop.setPropertyMappingHolder(null);
    }
	
	public void renameProperty(IPropertyMapping prop, String newName) {
		((PropertyMapping)prop).setName(newName);
    }
	
	public void clear(){
		Iterator it = properties.iterator();
		while(it.hasNext()){
			IPropertyMapping prop= (IPropertyMapping) it.next();
			prop.clear();
		}
		if(key!=null) key.clear();
		properties.clear();
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#containsProperty(org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping)
	 */
	public boolean containsProperty(IPropertyMapping prop) {
		return properties.contains(prop);
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#getPropertyIterator()
	 */
	public Iterator<IPropertyMapping> getPropertyIterator() {
		return properties.iterator();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#getTable()
	 */
	public IDatabaseTable getTable() {
		return table;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#setTable(org.jboss.tools.hibernate.internal.core.data.Table)
	 */
	public void setTable(IDatabaseTable table) {
		this.table = table;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#getKey()
	 */
	public IHibernateKeyMapping getKey() {
		return key;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#setKey(org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping)
	 */
	public void setKey(IHibernateKeyMapping key) {
		this.key = key;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#getPersistentClass()
	 */
	public IHibernateClassMapping getPersistentClass() {
		return persistentClass;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#setPersistentClass(org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping)
	 */
	public void setPersistentClass(IHibernateClassMapping persistentClass) {
		this.persistentClass = persistentClass;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#getPropertySpan()
	 */
	public int getPropertySpan() {
		return properties.size();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#getCustomSQLDelete()
	 */
	public String getCustomSQLDelete() {
		return customSQLDelete;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#setCustomSQLDelete(java.lang.String, boolean)
	 */
	public void setCustomSQLDelete(String customSQLDelete, boolean callable) {
		this.customSQLDelete = customSQLDelete;
		this.customDeleteCallable = callable;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#getCustomSQLInsert()
	 */
	public String getCustomSQLInsert() {
		return customSQLInsert;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#setCustomSQLInsert(java.lang.String, boolean)
	 */
	public void setCustomSQLInsert(String customSQLInsert, boolean callable) {
		this.customSQLInsert = customSQLInsert;
		this.customInsertCallable = callable;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#getCustomSQLUpdate()
	 */
	public String getCustomSQLUpdate() {
		return customSQLUpdate;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#setCustomSQLUpdate(java.lang.String, boolean)
	 */
	public void setCustomSQLUpdate(String customSQLUpdate, boolean callable) {
		this.customSQLUpdate = customSQLUpdate;
		this.customUpdateCallable = callable;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#isCustomDeleteCallable()
	 */
	public boolean isCustomDeleteCallable() {
		return customDeleteCallable;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#isCustomInsertCallable()
	 */
	public boolean isCustomInsertCallable() {
		return customInsertCallable;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#isCustomUpdateCallable()
	 */
	public boolean isCustomUpdateCallable() {
		return customUpdateCallable;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#isSequentialSelect()
	 */
	public boolean isSequentialSelect() {
		return sequentialSelect;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#setSequentialSelect(boolean)
	 */
	public void setSequentialSelect(boolean deferred) {
		this.sequentialSelect = deferred;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#isInverse()
	 */
	public boolean isInverse() {
		return inverse;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#setInverse(boolean)
	 */
	public void setInverse(boolean leftJoin) {
		this.inverse = leftJoin;
	}

	public String toString() {
		return getClass().getName() + '(' + table.toString() + ')';
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#isLazy()
	 */
	public boolean isLazy() {
		Iterator iter = getPropertyIterator();
		while ( iter.hasNext() ) {
			PropertyMapping prop = (PropertyMapping) iter.next();
			if ( !prop.isLazy() ) return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#isOptional()
	 */
	public boolean isOptional() {
		return optional;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.IJoinMapping#setOptional(boolean)
	 */
	public void setOptional(boolean nullable) {
		this.optional = nullable;
	}
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitJoinMapping(this,argument);
		return null;
	}
	public String getSubselect() {
		return subselect;
	}
	/**
	 * @param subselect The subselect to set.
	 */
	public void setSubselect(String subselect) {
		this.subselect = subselect;
	}

//	akuzmin 05/06/2005	
	public IPropertySource2 getPropertySource()
	{
		CombinedBeanPropertySourceBase bp=
		new CombinedBeanPropertySourceBase(new Object[]{this,this},new PropertyDescriptorsHolder[]{getPropertyDescriptorHolder(),CollectionMappingFKDescriptorsHolder.getInstance(key.getTable())});
		return bp;

//		BeanPropertySourceBase bp = new BeanPropertySourceBase(this);
//		bp.setPropertyDescriptors(getPropertyDescriptorHolder());		
//		return bp;
	}
//	akuzmin 05/06/2005
	public PropertyDescriptorsHolder getPropertyDescriptorHolder() {
		return JoinMapingDescriptorsHolder.getInstance();
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
	
}
