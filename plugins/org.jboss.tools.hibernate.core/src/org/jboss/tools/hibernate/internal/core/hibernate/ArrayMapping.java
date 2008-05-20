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

import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.hibernate.IArrayMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.ArrayMappingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.ArrayMappingDescriptorsHolderWithTable;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * An array mapping has a primary key consisting of
 * the key columns + index column.
 */
public class ArrayMapping extends ListMapping implements IArrayMapping {
	private String elementClassName;

	/**
	 * Constructor for Array.
	 * @param owner
	 */
	public ArrayMapping(IHibernateClassMapping owner) {
		super(owner);
	}


	public boolean isArray() {
		return true;
	}

	/**
	 * @return Returns the elementClassName.
	 */
	public String getElementClassName() {
		return elementClassName;
	}
	/**
	 * @param elementClassName The elementClassName to set.
	 */
	public void setElementClassName(String elementClassName) {
		this.elementClassName = elementClassName;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitArrayMapping(this,argument);
		return visitor.visitPersistentValueMapping(this,argument);
	}

//	akuzmin 30/05/2005
	public PropertyDescriptorsHolder getPropertyDescriptorHolder() {
		return ArrayMappingDescriptorsHolderWithTable.getInstance(getCollectionTable(),getFieldMapping().getPersistentField());
	}
//  akuzmin 22/09/2005	
	public PropertyDescriptorsHolder getPropertyDescriptorHolderWithOutTable() {
		return ArrayMappingDescriptorsHolder.getInstance(getCollectionTable());
	}
	
	
}
