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
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.IOneToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.AbstractValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.OneToManyMappingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.PropertyMappingPropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;



/**
 * A mapping for a one-to-many association
 */
public class OneToManyMapping extends AbstractValueMapping implements IOneToManyMapping {
	private String referencedEntityName;
	private IDatabaseTable referencingTable;
	private IHibernateClassMapping associatedClass;
	private Type type;
	//akuzmin 31.05.05
	private boolean ignoreNotFound=false;

	public OneToManyMapping(IHibernateClassMapping owner)  {
		this.referencingTable = (owner==null) ? null : owner.getDatabaseTable();
	}
	public void clear() {
	}

	public String getName(){
		String name=super.getName();
		return name==null?"one-to-many":name; 
	}
	
	public IHibernateClassMapping getAssociatedClass() {
		return associatedClass;
	}

    /**
     * Associated persistent class on the "Many" side. e.g. if a parent has a one-to-many to it's children, the associated class will be the Child.
     * 
     * @param associatedClass
     */
	public void setAssociatedClass(IHibernateClassMapping associatedClass) {
		this.associatedClass = associatedClass;
	}

	public Iterator getColumns(){
		return getColumnIterator();
	}
	public Iterator getColumnIterator() {
		//akuzmin 18.05.05
		if ((associatedClass==null) || (associatedClass.getKey()==null)) 
			return null;
		else
			return associatedClass.getKey().getColumnIterator();
	}

	public int getColumnSpan() {
		return associatedClass.getKey().getColumnSpan();
	}

	public String getFetchMode() {
		return "join";
	}
	
	//akuzmin 15.06.2005
	public void setFetchMode(String fetchMode)	{}	

    /** Table of the owner */
	public IDatabaseTable getTable() {
		return referencingTable;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping#setTable(org.jboss.tools.hibernate.core.IDatabaseTable)
	 */
	public void setTable(IDatabaseTable table) {
		referencingTable=table;
		
	}
	public boolean isNullable() {
		return false;
	}

	public boolean isSimpleValue() {
		return false;
	}

	public boolean isAlternateUniqueKey() {
		return false;
	}

	public boolean hasFormula() {
		return false;
	}
	
    public String getReferencedEntityName() {
		if(referencedEntityName==null && associatedClass!=null) return associatedClass.getName();
		return referencedEntityName;
	}

    /** Associated persistent class on the "Many" side. e.g. if a parent has a one-to-many to it's children, the associated class will be the Child. */    
	public void setReferencedEntityName(String referencedEntityName) {
		this.referencedEntityName = referencedEntityName;
	}

	/**
	 * @return Returns the type.
	 */
	public Type getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(Type type) {
		this.type = type;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitOneToManyMapping(this,argument);
		return visitor.visitPersistentValueMapping(this,argument);
	}
//akuzmin 21/04/2005	
	public IPropertySource2 getPropertySource()
	{
		BeanPropertySourceBase bp = new BeanPropertySourceBase(this);
		bp.setPropertyDescriptors(getPropertyDescriptorHolder());		
		return bp;
	}
//	akuzmin 05/05/2005
	public PropertyDescriptorsHolder getPropertyDescriptorHolder() {
		return OneToManyMappingDescriptorsHolder.getInstance(getTable());
	}
//	akuzmin 24/05/2005
	public PropertyDescriptorsHolder getPropertyMappingDescriptorHolder() {
		return PropertyMappingPropertyDescriptorsHolder.getInstance();
	}

//	akuzmin 26.05.2005
	public void setTypeByString(String type) {
		if ((Type.getType(type)!=null)||(getFieldMapping().getPersistentField().getOwnerClass().getProjectMapping().findClass(type)!=null))
			setType(Type.getOrCreateType(type));
		}
//	akuzmin 26.05.2005	
	public String getTypeByString() {
		if (getType()==null)
		{
			if ((getFieldMapping()==null) ||
					(getFieldMapping().getPersistentField()==null) ||		
					(getFieldMapping().getPersistentField().getType()==null))
						return null;
			setType(Type.getOrCreateType(getFieldMapping().getPersistentField().getType()));			
		}
		return getType().getName();		
	}
	/**
	 * @return Returns the ignoreNotFound.
	 */
	public boolean isIgnoreNotFound() {
		return ignoreNotFound;
	}
	/**
	 * @param ignoreNotFound The ignoreNotFound to set.
	 */
	public void setIgnoreNotFound(boolean ignoreNotFound) {
		this.ignoreNotFound = ignoreNotFound;
	}
	
}
