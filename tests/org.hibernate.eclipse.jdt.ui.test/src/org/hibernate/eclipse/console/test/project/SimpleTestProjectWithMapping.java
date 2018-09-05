/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test.project;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.JavaModelException;
import org.hibernate.eclipse.console.properties.HibernatePropertiesConstants;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class SimpleTestProjectWithMapping extends SimpleTestProject {

	public static final String HMB_CONTENT = 
		"<?xml version=\"1.0\"?>\n" + //$NON-NLS-1$
		"<!DOCTYPE hibernate-mapping PUBLIC\n" + //$NON-NLS-1$
		"\"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\n" + //$NON-NLS-1$
		"\"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">\n" + //$NON-NLS-1$
		"<hibernate-mapping package=\"" + PACKAGE_NAME + "\">\n" + //$NON-NLS-1$ //$NON-NLS-2$
		"<class name=\"" + TYPE_NAME + "\">\n" + //$NON-NLS-1$ //$NON-NLS-2$
		"<id type=\"java.lang.Long\"/>\n" + //$NON-NLS-1$
		"<property name=\"testField\"/>\n" + //$NON-NLS-1$
		"</class>\n" + //$NON-NLS-1$
		"</hibernate-mapping>\n"; //$NON-NLS-1$
	
	public SimpleTestProjectWithMapping(String projName) {
		super(projName);
	}

	@Override
	protected void buildProject() throws JavaModelException, CoreException, IOException {
		super.buildProject();
		final String path = SRC_FOLDER + "/" + //$NON-NLS-1$
			PACKAGE_NAME + "/" +  //$NON-NLS-1$
			TYPE_NAME + ".hbm.xml"; //$NON-NLS-1$
		getIProject().getFile(path).create(
			new ByteArrayInputStream(HMB_CONTENT.getBytes()),
			false, new NullProgressMonitor());
		getIProject().findMember(path);
		getIProject().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}
	
	public void addHibernateNature() throws CoreException{
		ProjectUtils.addProjectNature(getIProject(), HibernatePropertiesConstants.HIBERNATE_NATURE, new NullProgressMonitor() );
	}

	public void setDefaultConsoleConfiguration(String ccName) throws BackingStoreException, CoreException{
		IScopeContext scope = new ProjectScope(getIProject() );
		Preferences node = scope.getNode(HibernatePropertiesConstants.HIBERNATE_CONSOLE_NODE);
		
		node.putBoolean(HibernatePropertiesConstants.HIBERNATE3_ENABLED, true );
		node.put(HibernatePropertiesConstants.DEFAULT_CONFIGURATION, ccName ); //$NON-NLS-1$
		node.flush();
		addHibernateNature();
	}
}
