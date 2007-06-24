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

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IPackage;
import org.jboss.tools.hibernate.core.IPersistentClass;


/**
 * @author alex
 *
 * A Java package
 */
public class Package extends AbstractOrmElement implements IPackage {
	private static final long serialVersionUID = 1L;

    private String projectQualifiedName = null;
    
	private IMapping projectMapping;
	private List classes=new ArrayList();
	private static final IPersistentClass[] CLASSES={};
	public Package(IMapping mapping){
		projectMapping=mapping;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPackage#getProjectMapping()
	 */
	public IMapping getProjectMapping() {
		return projectMapping;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPackage#getPersistentClasses()
	 */
	public IPersistentClass[] getPersistentClasses() {
		return (IPersistentClass[])classes.toArray(CLASSES);
	}
	
	public void addPersistentClass(IPersistentClass clazz){
		classes.add(clazz);
	}
	
	public boolean removePersistentClass(IPersistentClass clazz){
		return classes.remove(clazz);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		return visitor.visitPackage(this,argument);
	}
    
    // added by Nick 16.06.2005
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IPackage#hasMappedFields()
     */
    public boolean hasMappedFields() {
        boolean result = false;
        int i = 0;
        IPersistentClass[] classes = this.getPersistentClasses();
        if (classes != null)
        {
            while ( i < classes.length && !result)
            {
                result = classes[i].hasMappedFields();
                i++;
            }
        }
        
        return result;
    }
    // by Nick
    
    // added by Nick 30.06.2005
    public String getProjectQualifiedName() {
        return projectQualifiedName;
    }
    public void setProjectQualifiedName(String projectQualifiedName) {
        this.projectQualifiedName = projectQualifiedName;
    }
    // by Nick
}
