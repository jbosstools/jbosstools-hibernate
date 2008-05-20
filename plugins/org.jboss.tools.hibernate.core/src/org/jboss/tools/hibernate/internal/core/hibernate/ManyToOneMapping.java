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
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.IManyToOneMapping;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.ManyToOneMappingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.PropertyMappingForManytoOneMapingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;



/**
 * A many-to-one association mapping
 */
public class ManyToOneMapping extends ToOneMapping implements IManyToOneMapping {
	private static final long serialVersionUID = 1L;
	
	private boolean ignoreNotFound=false;
	
    // added by Nick 04.07.2005
	private boolean proxied = true;
    // by Nick
    
	public ManyToOneMapping(IDatabaseTable table) {
		super(table);
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitManyToOneMapping(this,argument);
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
		return ManyToOneMappingDescriptorsHolder.getInstance(getTable());
	}
//	akuzmin 24/05/2005
	public PropertyDescriptorsHolder getPropertyMappingDescriptorHolder() {
		return PropertyMappingForManytoOneMapingDescriptorsHolder.getInstance();
	}
	
	
	public boolean isIgnoreNotFound() {
		return ignoreNotFound;
	}

	public void setIgnoreNotFound(boolean ignoreNotFound) {
		this.ignoreNotFound = ignoreNotFound;
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
	//akuzmin 27.09.2005
	public String getMappingColumn() {
		if (getColumnSpan()>0)
		{
			String keyColumns=null;
			Column fkcolumn=null;
			Iterator iter=getColumnIterator();
			while (iter.hasNext())
			{
				fkcolumn=(Column)iter.next();
				if (getTable().getColumn(fkcolumn.getName())!=null)
					if (keyColumns!=null) 
						keyColumns=keyColumns+","+fkcolumn.getName();
					else keyColumns=fkcolumn.getName();
			}
			return keyColumns;
			
		}
		return null;
	}
	
	//akuzmin 27.09.2005
	public void setMappingColumn(String keyColumn) {
		String[] keyNames = new String[0];
		if (keyColumn!=null)
			keyNames = keyColumn.split(",");
		if ((keyNames!=null)&&(keyNames.length>=0))
		{
			Column col;
			while(getColumnIterator().hasNext())
			{
				col=(Column)getColumnIterator().next();
				removeColumn(col);
			}
			for(int i=0;i<keyNames.length;i++)
				if (getTable().getColumn(keyNames[i])!=null)
					addColumn(getTable().getColumn(keyNames[i]));
		}		
	}
    
}
