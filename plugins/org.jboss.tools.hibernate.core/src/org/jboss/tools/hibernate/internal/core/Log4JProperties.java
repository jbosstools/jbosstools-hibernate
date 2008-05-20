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

import java.io.IOException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.hibernate.core.ILog4JProperties;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;

/**
 * @author alex
 *
 * Object oriented presentation of log4j.properties file
 */

public class Log4JProperties extends AbstractConfigurationResource implements ILog4JProperties {

	private IProject project;
	private static final String LOG4J_PROP_NAME = "log4j.properties";
	
	public Log4JProperties(IProject project) {
		this.project=project;
	}
	
	public IResource createResource() throws CoreException {
		IPath path=ScanProject.getSourcePath(project);
		if(path==null){
			path = project.getProjectRelativePath();
		}
		return project.getFile(path.append(LOG4J_PROP_NAME));
	}

	public IResource findResource() throws CoreException {
		return ScanProject.scannerCP("/"+LOG4J_PROP_NAME,project);
	}

	// add tau 14.02.2006
	public void save(boolean flagSaveMappingStorages) throws IOException, CoreException {
		save();
		
	}
}
