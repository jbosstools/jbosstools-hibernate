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


import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;

/**
 * @author alex
 *
 * A persistent class interface represents a Java class that has an object-relational mapping.
 */
public interface IPersistentClass extends IOrmElement {
	public IMapping getProjectMapping(); 
	public void setProjectMapping(IMapping mapping); 
	/**
	 * Returns class name without package
	 * */
	public String getShortName();
	public IPackage getPackage();
	public ICompilationUnit getSourceCode();
	public void setSourceCode(ICompilationUnit cu);
	//added by Nick 01.06.2005
    public IType getType();
    //by Nick
    public IPersistentClass getSuperClass();
	public IPersistentClass getRootClass();
	public IPersistentField[] getFields();
	//added by Nick 1.04.2005
	//public IPersistentField[] getAllFields();
	//by Nick
	public IPersistentClassMapping getPersistentClassMapping();
	public void setPersistentClassMapping(IPersistentClassMapping mapping);
	public void clearMapping();
	public IPersistentField getField(String name);
	public void deleteField(String fieldName);
	public void renameField(IPersistentField field, String newName);
	
	public void refresh();
	public boolean isResourceChanged();	
    
    // added by Nick 16.06.2005
	public boolean hasMappedFields();
    // by Nick
	
// added by yk 14.07.2005
	public void removeField(String name);
// added by yk 14.07.2005 stop
	
	// add tau 24.04.2006
	public IDatabaseTable getDatabaseTable();
	public IMappingStorage getPersistentClassMappingStorage();

}
