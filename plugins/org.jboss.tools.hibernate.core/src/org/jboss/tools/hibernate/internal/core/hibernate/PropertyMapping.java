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

import java.util.Iterator;

import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IMetaAttribute;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMappingHolder;
import org.jboss.tools.hibernate.internal.core.AbstractFieldMapping;
import org.jboss.tools.hibernate.internal.core.AbstractValueMapping;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.PropertyMappingPropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.CombinedBeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * @author alex
 * 
 * Mapping for a property of a Java class (entity
 * or component)
 */
public class PropertyMapping extends AbstractFieldMapping implements IPropertyMapping {
	private static final long serialVersionUID = 1L;
	// added by Nick 10.06.2005
    private IPropertyMappingHolder propertyMappingHolder;
    // by Nick
    private IHibernateValueMapping value;
	private String cascade;
	private boolean updateable = true;
	private boolean insertable = true;
	private boolean optimisticLocked = true;
	private String propertyAccessorName;
	private java.util.Map metaAttributes;
	private boolean lazy;
	private boolean optional;
	
	// added by yk 26.08.2005
	private boolean naturalID;
	// added by yk 26.08.2005.

	//excessive data. may be replaced with getPersistentField().getOwnerClass().getPersistentClassMapping()
	private ClassMapping persistentClassMapping;  

	/*public Type getType() throws MappingException {
		return value.getType();
	}*/
	
	public void clear(){
		super.clear();
		if(value!=null)value.clear();
		setPropertyMappingHolder(null);
    }
	
	public int getColumnSpan() {
		return value.getColumnSpan();
	}
	public Iterator<IDatabaseColumn> getColumnIterator() {
		// #added# by Konstantin Mishin on 21.11.2005 fixed for ESORM-328
		if(value==null)
			return null;
		else
		// #added#
			return value.getColumnIterator();
	}
	public boolean isUpdateable() {
		return updateable && !value.hasFormula();
	}

	public boolean isComposite() {
		return value instanceof ComponentMapping;
	}

	/*
	public boolean isPrimitive(Class clazz) {
		return getGetter(clazz).getReturnType().isPrimitive();
	}*/

	/**
	 * Returns the cascade.
	 * @return String
	 */
	public String getCascade() {
		return cascade;
	}

	/**
	 * Sets the cascade.
	 * @param cascade The cascade to set
	 */
	public void setCascade(String cascade) {
		this.cascade = cascade;
	}

	/**
	 * Sets the mutable.
	 * @param mutable The mutable to set
	 */
	public void setUpdateable(boolean mutable) {
		this.updateable = mutable;
	}

	/**
	 * Returns the insertable.
	 * @return boolean
	 */
	public boolean isInsertable() {
		return insertable && !value.hasFormula();
	}

	/**
	 * Sets the insertable.
	 * @param insertable The insertable to set
	 */
	public void setInsertable(boolean insertable) {
		this.insertable = insertable;
	}

	public String getPropertyAccessorName() {
		return propertyAccessorName;
	}

	public void setPropertyAccessorName(String string) {
		propertyAccessorName = string;
	}


	public boolean isBasicPropertyAccessor() {
		return propertyAccessorName==null || "property".equals(propertyAccessorName);
	}
	public java.util.Map getMetaAttributes() {
		return metaAttributes;
	}
	public IMetaAttribute getMetaAttribute(String attributeName) {
		return (IMetaAttribute) metaAttributes.get(attributeName);
	}

	public void setMetaAttributes(java.util.Map metas) {
		this.metaAttributes = metas;
	}


	public String toString() {
		return getClass().getName() + '(' + getName() + ')';
	}
	
	public void setLazy(boolean lazy) {
		this.lazy=lazy;
	}
	
	public boolean isLazy() {
		return lazy;
	}
//akuzmin 04.07.2005
	public void setToOneLazy(String lazy) {
		
		if (lazy!=null)
		{
			if (value instanceof OneToOneMapping)
			{
				if (lazy.equals(OrmConfiguration.ASSOCIATIONS_LAZY_VALUES[1])) // changed by Nick 20.09.2005
				{
					((OneToOneMapping)value).setProxied(false);
					setLazy(true);
				}
				else if (lazy.equals(OrmConfiguration.ASSOCIATIONS_LAZY_VALUES[2])) // changed by Nick 20.09.2005
					{
						((OneToOneMapping)value).setProxied(false);
						setLazy(false);					
					}
					else ((OneToOneMapping)value).setProxied(true);
					
			}
			else
				if (value instanceof ManyToOneMapping)
				{
					if (lazy.equals(OrmConfiguration.ASSOCIATIONS_LAZY_VALUES[1])) // changed by Nick 20.09.2005
					{
						((ManyToOneMapping)value).setProxied(false);
						setLazy(true);
					}
					else if (lazy.equals(OrmConfiguration.ASSOCIATIONS_LAZY_VALUES[2])) // changed by Nick 20.09.2005
						{
							((ManyToOneMapping)value).setProxied(false);
							setLazy(false);					
						}
						else ((ManyToOneMapping)value).setProxied(true);
	
				}
		}

	}
//	akuzmin 04.07.2005	
	public String getToOneLazy() {
		if (value instanceof OneToOneMapping)
		{
			if (((OneToOneMapping)value).isProxied())
				return OrmConfiguration.ASSOCIATIONS_LAZY_VALUES[0]; // changed by Nick 20.09.2005
			else if (isLazy())
					return OrmConfiguration.ASSOCIATIONS_LAZY_VALUES[1]; // changed by Nick 20.09.2005
				else
					return OrmConfiguration.ASSOCIATIONS_LAZY_VALUES[2]; // changed by Nick 20.09.2005
		}
		else
			if (value instanceof ManyToOneMapping)
			{
				if (((ManyToOneMapping)value).isProxied())
					return OrmConfiguration.ASSOCIATIONS_LAZY_VALUES[0]; // changed by Nick 20.09.2005
				else if (isLazy())
						return OrmConfiguration.ASSOCIATIONS_LAZY_VALUES[1]; // changed by Nick 20.09.2005
					else
						return OrmConfiguration.ASSOCIATIONS_LAZY_VALUES[2]; // changed by Nick 20.09.2005

			}
			else return OrmConfiguration.ASSOCIATIONS_DEFAULT_LAZY; // changed by Nick 20.09.2005
	}
	
	public boolean isOptimisticLocked() {
		return optimisticLocked;
	}

	public void setOptimisticLocked(boolean optimisticLocked) {
		this.optimisticLocked = optimisticLocked;
	}
	
	public boolean isOptional() {
		return optional;
	}
	
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	
	public ClassMapping getPersistentClassMapping() {
		return persistentClassMapping;
	}
	public void setPersistentClassMapping(ClassMapping persistentClass) {
		this.persistentClassMapping = persistentClass;
	}

	/**
	 * @return Returns the value.
	 */
	public IHibernateValueMapping getValue() {
		return value;
	}
	/**
	 * @param value The value to set.
	 */
	public void setValue(IHibernateValueMapping value) {
		this.value = value;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentFieldMapping#getPersistentValueMapping()
	 */
	public IPersistentValueMapping getPersistentValueMapping() {
		return value;
	}
//akuzmin 21.04.2005	
	public void setPersistentValueMapping(IPersistentValueMapping valueMapping) {
		value=(IHibernateValueMapping) valueMapping;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitPropertyMapping(this,argument);
		return visitor.visitPersistentFieldMapping(this,argument);
	}

	public IPropertySource2 getPropertySource(IPersistentValueMapping elemvalue)
	{
		
		if (elemvalue==null)
		{
			CombinedBeanPropertySourceBase bp = 
			new CombinedBeanPropertySourceBase(new Object[]{this,value},new PropertyDescriptorsHolder[]{PropertyMappingPropertyDescriptorsHolder.getInstance(),((AbstractValueMapping)value).getPropertyDescriptorHolder()});
			return bp;
		}
//akuzmin 05.05.2005
		else
		{
			CombinedBeanPropertySourceBase bp;
			if ((getPersistentClassMapping()!=null)&&(this.equals(getPersistentClassMapping().getVersion())))
				bp = new CombinedBeanPropertySourceBase(new Object[]{this,elemvalue},new PropertyDescriptorsHolder[]{((SimpleValueMapping)elemvalue).getPropertyVersionMappingDescriptorHolder(),((SimpleValueMapping)elemvalue).getVersionDescriptorHolder()});
			else
			{
				if ((elemvalue instanceof CollectionMapping)&& (((CollectionMapping)elemvalue).getElement() instanceof OneToManyMapping))
					bp = new CombinedBeanPropertySourceBase(new Object[]{this,elemvalue},new PropertyDescriptorsHolder[]{((AbstractValueMapping)elemvalue).getPropertyMappingDescriptorHolder(),((CollectionMapping)elemvalue).getPropertyDescriptorHolderWithOutTable()});
				else
					bp = new CombinedBeanPropertySourceBase(new Object[]{this,elemvalue},new PropertyDescriptorsHolder[]{((AbstractValueMapping)elemvalue).getPropertyMappingDescriptorHolder(),((AbstractValueMapping)elemvalue).getPropertyDescriptorHolder()});
			}
		return bp;
		}
	}	
//	akuzmin 25.05.2005
	public void setUnique(boolean unique) {
//		if (getColumnSpan()==1)
//		{
//			((Column)getColumnIterator().next()).setUnique(unique);
//		}
	    Iterator itr = getColumnIterator();
        if (itr != null)
            while (itr.hasNext())
            {
                IDatabaseColumn column = (IDatabaseColumn) itr.next();
                column.setUnique(unique);
            }
		
	}
//	akuzmin 25.05.2005	
	public boolean getUnique() {
//		if (getColumnSpan()==1)
//		{
//			return ((Column)getColumnIterator().next()).isUnique();			
//		}
//		else return false;
		
	    boolean unique = true;
	    if (getColumnSpan()>0)
	    {
	        Iterator itr = getColumnIterator();
	        if (itr != null)
	            while (itr.hasNext() && unique)
	            {
	                IDatabaseColumn column = (IDatabaseColumn) itr.next();
	                if (!column.isUnique())
	                	unique = false;
	            }
	    }
	    else unique = false;
        return unique;
		
		
		
	}
//	akuzmin 25.05.2005
	public void setNotNull(boolean NotNull) {
        // changed by Nick 24.06.2005
//		if (getColumnSpan()==1)
//		{
//			((Column)getColumnIterator().next()).setNullable(NotNull);
//      }
	    Iterator itr = getColumnIterator();
        if (itr != null)
            while (itr.hasNext())
            {
                IDatabaseColumn column = (IDatabaseColumn) itr.next();
                column.setNullable(!NotNull);
            }
        // by Nick
	}

    //	akuzmin 25.05.2005	
	public boolean getNotNull() {
        // changed by Nick 24.06.2005
//		if (getColumnSpan()==1)
//		{
//			//return ((Column)getColumnIterator().next()).isNullable();			
//            return !((Column)getColumnIterator().next()).isNullable();         
//        }
//		else return false;
        
        //all columns should be non-nullable for property to be non-nullable
        
	    boolean notNull = true;
        Iterator itr = getColumnIterator();
        if (itr != null)
            while (itr.hasNext() && notNull)
            {
                IDatabaseColumn column = (IDatabaseColumn) itr.next();
                if (column.isNullable())
                    notNull = false;
            }
        return notNull;
        // by Nick
	}

    
    // added by Nick 10.06.2005
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.hibernate.IPropertyMapping#getPropertyMappingHolder()
     */
    public IPropertyMappingHolder getPropertyMappingHolder() {
        return propertyMappingHolder;
    }
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.hibernate.IPropertyMapping#setPropertyMappingHolder(org.jboss.tools.hibernate.core.hibernate.IPropertyMappingHolder)
     */
    public void setPropertyMappingHolder(IPropertyMappingHolder holder) {
        this.propertyMappingHolder = holder;
    }
    // by Nick
    
    // added by yk 26.08.2005
    public boolean 	isNaturalID()
    {		return naturalID;		}
    
    /**
     * set new value for naturalID field. 
     */
    public void 	setNaturalID(final boolean value)
    {		naturalID = value;		}
    // added by yk 26.08.2005.
}
