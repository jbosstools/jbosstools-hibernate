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
import org.jboss.tools.hibernate.core.hibernate.IListMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.ListMappingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.ListMappingDescriptorsHolderWithTable;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * A list mapping has a primary key consisting of
 * the key columns + index column.
 */
public class ListMapping extends IndexedCollectionMapping implements IListMapping {
	private int baseIndex;
	
	public ListMapping(IHibernateClassMapping owner) {
		super(owner);
	}
	
	public int getBaseIndex() {
		return baseIndex;
	}
	
	public void setBaseIndex(int baseIndex) {
		this.baseIndex = baseIndex;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitListMapping(this,argument);
		return visitor.visitPersistentValueMapping(this,argument);
	}
	
//	akuzmin 30/05/2005
	public PropertyDescriptorsHolder getPropertyDescriptorHolder() {
		return ListMappingDescriptorsHolderWithTable.getInstance(getCollectionTable(),getFieldMapping().getPersistentField());
	}
//  akuzmin 22/09/2005	
	public PropertyDescriptorsHolder getPropertyDescriptorHolderWithOutTable() {
		return ListMappingDescriptorsHolder.getInstance(getCollectionTable());
	}
	

}
