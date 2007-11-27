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

import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.IUnionSubclassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.UnionSubclassMappingPropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;


/**
 * A subclass in a table-per-concrete-class mapping
 */
public class UnionSubclassMapping extends SubclassMapping implements IUnionSubclassMapping {
	private static final long serialVersionUID = 1L;
	private IDatabaseTable table;

	public UnionSubclassMapping(IHibernateClassMapping superclass) {
		super(superclass);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClassMapping#setDatabaseTable(org.jboss.tools.hibernate.core.IDatabaseTable)
	 */
	public void setDatabaseTable(IDatabaseTable table) {
		this.table=table;
	}

	public IDatabaseTable getDatabaseTable() {
		return table;
	}

	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.SubclassMapping#isUnionSubclass()
	 */
	public boolean isUnionSubclass() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.SubclassMapping#isSubclass()
	 */
	public boolean isSubclass() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.SubclassMapping#isJoinedSubclass()
	 */
	public boolean isJoinedSubclass() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitUnionSubclassMapping(this,argument);
		return visitor.visitPersistentClassMapping(this,argument);
	}

	public IPropertySource2 getPropertySource()
	{
		BeanPropertySourceBase bp = new BeanPropertySourceBase(this);
		bp.setPropertyDescriptors(UnionSubclassMappingPropertyDescriptorsHolder.getInstance(this));
		return bp;
	}	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.SubclassMapping#copyFrom(org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping)
	 */
	public void copyFrom(IHibernateClassMapping hcm) {
		table=hcm.getDatabaseTable();
		super.copyFrom(hcm);
	}	

}
