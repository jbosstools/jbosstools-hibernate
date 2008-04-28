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


import java.io.IOException;
import java.util.Iterator;
import java.util.List;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.hibernate.core.IMappingProperties;
import org.jboss.tools.hibernate.internal.core.AbstractConfigurationResource;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.HibernatePropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;


/**
 * @author alex
 *
 * Storage for hibernate properties
 * @see org.hibernate.cfg.Settings
 */
public class HibernateProperties extends AbstractConfigurationResource implements IMappingProperties{

	private IProject project;
	
	public HibernateProperties(IProject project) {
		this.project = project;
		this.setPropertyDescriptorsHolder(HibernatePropertyDescriptorsHolder.getInstance());
	}
	
	public HibernateProperties() {
		//this.project = project;
		this.setPropertyDescriptorsHolder(HibernatePropertyDescriptorsHolder.getInstance());
	}

	public IResource findResource() throws CoreException {
		List configs=ScanProject.getResources("hibernate.properties", project,ScanProject.SCOPE_SRC|ScanProject.SCOPE_ROOT);
		Iterator it=configs.iterator();
		if(it.hasNext()){
			return (IResource)it.next();
		}
		return null;
	}
	public IResource createResource() throws CoreException {
		IPath path=ScanProject.getSourcePath(project);
		if(path==null){
			path = project.getProjectRelativePath();
		}
		return project.getFile(path.append("hibernate.properties"));
	}
	
	// add tau 14.02.2006
	public void save(boolean flagSaveMappingStorages) throws IOException, CoreException {
		save();
		
	}
}