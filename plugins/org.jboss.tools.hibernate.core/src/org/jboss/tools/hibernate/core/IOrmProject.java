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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.views.properties.IPropertySource2;

/**
 * @author alex
 *
 * An OrmProject represents a view of project resources in terms of ORM elements 
 * such as persistent class, database table and persistent class mapping.
 */
public interface IOrmProject extends IOrmElement {
	public IProject getProject();
	
	public IMapping[] getMappings();
	public IOrmConfiguration getOrmConfiguration();
	public IMapping getMapping(String id);
	/*
	 * XXX: Add Log4J properties and Java logging properties
	 * */
	public ILog4JProperties getLog4jProperties();
	public IJavaLoggingProperties getJavaLoggingProperties();
	
	public void addChangedListener(IOrmProjectChangedListener listener);
	public void removeChangedListener(IOrmProjectChangedListener listener);
	
	// del tau 02.12.2005
	//public void addBeforeChangeListener(IOrmProjectBeforeChangeListener listener);
	//public void removeBeforeChangeListener(IOrmProjectBeforeChangeListener listener);
	
	
	/**
	 * Create new mapping for given project nature e.g. Hibernate, JDO etc.
	 * The newly created mapping also accessible through getMappings()
	 */
	public IMapping createMapping(String natureId);
	public void moveMapping(IMapping mapping, IPath newPath) throws CoreException ;
	public void removeMapping(IMapping mapping);
	/**
	 * Get existing or create mapping for given project nature. 
	 */
	public IMapping getInitialMapping(String natureId);
	// edit by tau 19.07.2005 add "Object source"	
	public void fireProjectChanged(Object source, boolean flagUpdate);
	// del tau 02.12.2005
	//public void fireProjectBeforeChange(Object source);	
	
	// add tau 17.05.2005, edit tau 17.11.2005 - add boolean doMappingsUpdate
	public void refresh(boolean doMappingsUpdate);

    //added by Nick 19.05.2005
    public void resourcesChanged();
    //by Nick
    
    // added by Nick 07.09.2005
    IProject[] getReferencedProjects();
    // by Nick
    //add akuzmin 15.09.2005 
	public IPropertySource2 getSpringConfiguration();

    public void setDirty(boolean dirty);	
	//public IJavaElement [] getAllPackageFragments() throws JavaModelException; del tau 06.03.2006
	public IJavaElement[] getPackageFragments(String packageName, boolean findAll) throws JavaModelException;
    
}
