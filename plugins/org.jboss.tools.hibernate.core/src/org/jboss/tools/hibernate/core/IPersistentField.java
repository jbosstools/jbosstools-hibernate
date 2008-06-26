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
package org.jboss.tools.hibernate.core;

/**
 * @author alex
 *
 * A persistent field represent a field of a Java class 
 * that may has an object-relational mapping or may not
 */
public interface IPersistentField extends IOrmElement {
	public String getName();
	//by Nick (15.03.2005) - getType() method now returns String 
	public String getType();
	//by Nick
	public IPersistentClass getOwnerClass();
	public IPersistentFieldMapping getMapping();
	public void setMapping(IPersistentFieldMapping mapping);
	//added by Nick 18.04.2005
	public int getAccessorMask();
	//by Nick
    // added by Nick 06.06.2005
	public void setOwnerClass(IPersistentClass clazz);
    //by Nick

	// added by Nick 13.06.2005
	public IPersistentClass getMasterClass();
    // by Nick
    
    // added by Nick 29.07.2005
    public String[] getGenerifiedTypes();
    // by Nick
}
