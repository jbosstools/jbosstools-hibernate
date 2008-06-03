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
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.IPrimitiveArrayMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.PrimitiveArrayMappingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.PrimitiveArrayMappingDescriptorsHolderWithTable;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.PropertyMappingForPrimitiveArrayDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * A primitive array has a primary key consisting
 * of the key columns + index column.
 */
public class PrimitiveArrayMapping extends ArrayMapping implements IPrimitiveArrayMapping {
	private static final long serialVersionUID = 1L;
	
	public PrimitiveArrayMapping(IHibernateClassMapping owner) {
		super(owner);
	}

	public boolean isPrimitiveArray() {
		return true;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitPrimitiveArrayMapping(this,argument);
		return visitor.visitPersistentValueMapping(this,argument);
	}
//	akuzmin 11/07/2005
	public PropertyDescriptorsHolder getPropertyDescriptorHolder() {
		return PrimitiveArrayMappingDescriptorsHolderWithTable.getInstance(getCollectionTable(),getFieldMapping().getPersistentField());
	}
//  akuzmin 22/09/2005	
	public PropertyDescriptorsHolder getPropertyDescriptorHolderWithOutTable() {
		return PrimitiveArrayMappingDescriptorsHolder.getInstance(getCollectionTable());
	}
	
	
//	akuzmin 11/07/2005
	public PropertyDescriptorsHolder getPropertyMappingDescriptorHolder() {
		return PropertyMappingForPrimitiveArrayDescriptorsHolder.getInstance();
	}
	
}
