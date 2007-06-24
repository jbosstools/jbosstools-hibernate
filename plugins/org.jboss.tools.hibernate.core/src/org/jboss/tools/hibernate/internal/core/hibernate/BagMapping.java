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
import org.jboss.tools.hibernate.core.hibernate.IBagMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.BagMappingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.BagMappingDescriptorsHolderWithTable;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * A bag permits duplicates, so it has no primary key
 */
public class BagMapping extends CollectionMapping implements IBagMapping {
	public BagMapping(IHibernateClassMapping owner) {
		super(owner);
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitBagMapping(this,argument);
		return visitor.visitPersistentValueMapping(this,argument);
	}
//	akuzmin 07/07/2005
	public PropertyDescriptorsHolder getPropertyDescriptorHolder() {
		return BagMappingDescriptorsHolderWithTable.getInstance(getFieldMapping().getPersistentField());
	}
//  akuzmin 22/09/2005	
	public PropertyDescriptorsHolder getPropertyDescriptorHolderWithOutTable() {
		return BagMappingDescriptorsHolder.getInstance();

	}

}
