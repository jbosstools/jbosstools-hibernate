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

import org.eclipse.swt.widgets.TreeItem;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;


/**
 * @author Nick
 *
 */
public class PersistableProperty implements IOrmElement {
	private static final long serialVersionUID = 1L;
	private String name;
	private String type;
    private int accessorMask;
	private String[] generifiedTypes;
	
	public PersistableProperty(String name, String type, String[] generifiedTypes, int accessorMask)
	{
		this.name = name;
		this.type = type;
		this.generifiedTypes = generifiedTypes;
        this.accessorMask = accessorMask;
	}
	
/*	public PersistableProperty(IField field) throws CoreException
	{
		name = field.getElementName();
		IType owner = field.getDeclaringType();
		type = ClassUtils.getQualifiedNameFromSignature(owner,field.getTypeSignature());
		this.accessorMask = PersistentField.ACCESSOR_FIELD;
    }
*/
    
	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
    
    public int getAccessorMask()
    {
        return accessorMask;
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
     */
    public Object accept(IOrmModelVisitor visitor, Object argument) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter) {
        return null;
    }

    public String[] getGenerifiedTypes() {
        return generifiedTypes;
    }

	public String getQualifiedName(TreeItem item) {
		//return (String) accept(QualifiedNameVisitor.visitor,item);
		// add 02.12.2005
		return StringUtils.parentItemName(item, this.getName());		
	}
}
