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
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.IOneToOneMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.OneToOneMappingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.PropertyMappingForOnetoOneMapingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;



/**
 * A one-to-one association mapping
 */
public class OneToOneMapping extends ToOneMapping implements IOneToOneMapping {
	private static final long serialVersionUID = 1L;
	private boolean constrained;
	//private ForeignKeyDirection foreignKeyType;
	private IHibernateKeyMapping identifier;
    // added by Nick 04.07.2005
    private boolean proxied = true;
    // by Nick

	public OneToOneMapping(IDatabaseTable table, IHibernateKeyMapping identifier) {
		super(table);
		this.identifier = identifier;
	}
	public void clear() {
		super.clear();
		if(identifier!=null) identifier.clear();
	}

	public java.util.List<IDatabaseColumn> getConstraintColumns() {
		ArrayList<IDatabaseColumn> list = new ArrayList<IDatabaseColumn>();
		Iterator<IDatabaseColumn> iter = identifier.getColumnIterator();
		while ( iter.hasNext() ) list.add( iter.next() );
		return list;
	}
	/**
	 * Returns the constrained.
	 * @return boolean
	 */
	public boolean isConstrained() {
		return constrained;
	}

	/**
	 * Returns the identifier.
	 * @return Value
	 */
	public IHibernateKeyMapping getIdentifier() {
		return identifier;
	}

	/**
	 * Sets the constrained.
	 * @param constrained The constrained to set
	 */
	public void setConstrained(boolean constrained) {
		this.constrained = constrained;
	}


	/**
	 * Sets the identifier.
	 * @param identifier The identifier to set
	 */
	public void setIdentifier(IHibernateKeyMapping identifier) {
		this.identifier = identifier;
	}

	public boolean isNullable() {
		return !constrained;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitOneToOneMapping(this,argument);
		return visitor.visitPersistentValueMapping(this,argument);
	}

//	akuzmin 21/04/2005	
	public IPropertySource2 getPropertySource()
	{
		BeanPropertySourceBase bp = new BeanPropertySourceBase(this);
		bp.setPropertyDescriptors(getPropertyDescriptorHolder());		
		return bp;
	}
//	akuzmin 05/05/2005
	public PropertyDescriptorsHolder getPropertyDescriptorHolder() {
		return OneToOneMappingDescriptorsHolder.getInstance(getFieldMapping().getPersistentField().getOwnerClass().getProjectMapping().findClass(getReferencedEntityName()),getFieldMapping().getPersistentField().getOwnerClass().getProjectMapping());
		
	}	
//	akuzmin 24/05/2005
	public PropertyDescriptorsHolder getPropertyMappingDescriptorHolder() {
		return PropertyMappingForOnetoOneMapingDescriptorsHolder.getInstance();
	}

    // added by Nick 04.07.2005
    public boolean isProxied()
    {
        return proxied;
    }
    
    public void setProxied(boolean proxied)
    {
        this.proxied = proxied;
    }
    // by Nick
}
