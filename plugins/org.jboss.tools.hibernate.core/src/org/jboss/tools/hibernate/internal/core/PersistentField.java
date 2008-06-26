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
package org.jboss.tools.hibernate.internal.core;

import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;


/**
 * @author alex
 *
 * A field from a persistent class
 */
public class PersistentField extends AbstractOrmElement implements
		IPersistentField {
	private static final long serialVersionUID = 1L;
	//by Nick (15.03.2005) - type property is String now
	private String type;
    //by Nick
	private IPersistentClass ownerClass;
	private IPersistentFieldMapping mapping;
	//added by Nick 18.04.2005
	private int accessorMask = ACCESSOR_NONE;
	//by Nick
	
	//added by Nick 18.04.2005
	public static final int ACCESSOR_NONE = 0;
	public static final int ACCESSOR_FIELD = 1;
	public static final int ACCESSOR_PROPERTY = 2;
	//by Nick

    private String[] generifiedTypes;
    
	//by Nick (15.03.2005) - type property is String now
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentField#getType()
	 */
	public String getType() {
		
		return type;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentField#getOwnerClass()
	 */
	public IPersistentClass getOwnerClass() {
		
		return ownerClass;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentField#getMapping()
	 */
	public IPersistentFieldMapping getMapping() {
		
		return mapping;
	}

	/**
	 * @param mapping The mapping to set.
	 */
	public void setMapping(IPersistentFieldMapping mapping) {
		this.mapping = mapping;
	}
	/**
	 * @param ownerClass The ownerClass to set.
	 */
	public void setOwnerClass(IPersistentClass ownerClass) {
		this.ownerClass = ownerClass;
	}

	//by Nick (15.03.2005) - type property is String now
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		return visitor.visitPersistentField(this,argument);
	}

	//added by Nick 18.04.2005
	public int addAccessorMask(int mask)
	{
		accessorMask = accessorMask | mask;
		return accessorMask;
	}
	
	public int removeAccessorMask(int mask)
	{
		accessorMask = accessorMask & ~mask;
		return accessorMask;
	}
	
	public int getAccessorMask() {
		return accessorMask;
	}
	//by Nick

    // added by Nick 13.06.2005
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IPersistentField#getMasterClass()
     */
    public IPersistentClass getMasterClass() {
        if (getOwnerClass() != null && getOwnerClass().getPersistentClassMapping() != null)
            return getOwnerClass().getPersistentClassMapping().getPersistentClass();
        else
            return null;
    }
    // by Nick

    public String[] getGenerifiedTypes() {
        return generifiedTypes;
    }

    public void setGenerifiedTypes(String[] generifiedTypes) {
        this.generifiedTypes = generifiedTypes;
    }
}
