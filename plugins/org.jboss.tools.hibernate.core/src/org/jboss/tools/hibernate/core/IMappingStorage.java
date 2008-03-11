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

import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.hibernate.internal.core.hibernate.FilterDef;


/**
 * @author alex
 *
 * Represents a resource where mapping is stored.
 */
public interface IMappingStorage extends IOrmElement {
	public IMapping getProjectMapping();
	public IResource getResource();
	public void setResource(IResource res); // added yan 20051014
	public IPersistentClassMapping[] getPersistentClassMappings();
	public int getPersistentClassMappingCount(); // added yan 20051003
	public int getNamedQueryMappingCount(); // added yan 20051025
	public INamedQueryMapping[] getNamedQueryMappings();
	public void addNamedQueryMapping(INamedQueryMapping mapping);
	public void removeNamedQueryMapping(INamedQueryMapping mapping);
	public void addPersistentClassMapping(IPersistentClassMapping mapping); 
	public void removePersistentClassMapping(IPersistentClassMapping mapping); 
	public void reload()throws IOException, CoreException;
	public void save() throws IOException ,CoreException;
	
	// add tau 28.03.2006	
	public void save(boolean force) throws IOException ,CoreException;
	public boolean isDirty();	
	public void setDirty(boolean dirty);	
	
    public boolean isResourceChanged();	
    //added 19.05.2005 by Nick
    public boolean resourceChanged();
    //by Nick
    
    // added by yk 19.10.2005
    public void addFilterDef(final String fdname, Properties prop);
    public int getFilterDefSize();
    public FilterDef getFilterDef(final String key);
    // added by yk 19.10.2005.

}
