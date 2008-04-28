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
package org.jboss.tools.hibernate.core.hibernate;

import java.util.Iterator;

import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;

/**
 * @author alex
 *
 */
public interface IPropertyMapping extends IPersistentFieldMapping {
	
	public int getColumnSpan();

	public Iterator<IDatabaseColumn> getColumnIterator();

	public boolean isUpdateable();

	public boolean isComposite();

	public String getCascade();

	/**
	 * Sets the cascade.
	 * @param cascade The cascade to set
	 */
	public void setCascade(String cascade);

	/**
	 * Sets the mutable.
	 * @param mutable The mutable to set
	 */
	public void setUpdateable(boolean mutable);

	/**
	 * Returns the insertable.
	 * @return boolean
	 */
	public boolean isInsertable();

	/**
	 * Sets the insertable.
	 * @param insertable The insertable to set
	 */
	public void setInsertable(boolean insertable);

	public String getPropertyAccessorName();

	public void setPropertyAccessorName(String string);

	public boolean isBasicPropertyAccessor();

	public java.util.Map getMetaAttributes();

	public IMetaAttribute getMetaAttribute(String attributeName);

	public void setMetaAttributes(java.util.Map metas);

	public void setLazy(boolean lazy);

	public boolean isLazy();

	public boolean isOptimisticLocked();

	public void setOptimisticLocked(boolean optimisticLocked);

	public boolean isOptional();

	public void setOptional(boolean optional);

	/**
	 * @return Returns the value.
	 */
	public IHibernateValueMapping getValue();

	/**
	 * @param value The value to set.
	 */
	public void setValue(IHibernateValueMapping value);

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentFieldMapping#getPersistentValueMapping()
	 */
	public IPersistentValueMapping getPersistentValueMapping();
	//akuzmin 17.05.2005
	public IPropertySource2 getPropertySource(IPersistentValueMapping elemvalue);
    //	akuzmin 04.07.2005
	public boolean getUnique();
	public void setUnique(boolean unique);
	//akuzmin 06.07.2005
	public String getToOneLazy();
	public void setToOneLazy(String lazy);
	// added by Nick 10.06.2005
	public IPropertyMappingHolder getPropertyMappingHolder();
    public void setPropertyMappingHolder(IPropertyMappingHolder holder);
    // by Nick
    // added by yk 26.08.2005
    public boolean 	isNaturalID();
    public void 	setNaturalID(final boolean value);
    // added by yk 26.08.2005.

}