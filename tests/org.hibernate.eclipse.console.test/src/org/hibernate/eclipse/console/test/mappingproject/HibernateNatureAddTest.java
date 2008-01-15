/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test.mappingproject;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaProject;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import junit.framework.TestCase;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateNatureAddTest extends TestCase {
	public void testHibernateNatureAdd() throws BackingStoreException, CoreException {
		MappingTestProject project = MappingTestProject.getTestProject();
		IJavaProject prj = project.getIJavaProject();
		IScopeContext scope = new ProjectScope(prj.getProject() );
		//fail("HibernateNatureAdd testfail");
		Preferences node = scope.getNode("org.hibernate.eclipse.console");

		node.putBoolean("hibernate3.enabled", true );
		node.put("default.configuration", "testcfg" );
		node.flush();

		ProjectUtils.addProjectNature(prj.getProject(), "org.hibernate.eclipse.console.hibernateNature", new NullProgressMonitor() );
		//ProjectUtils.removeProjectNature(prj.getProject(), "org.hibernate.eclipse.console.hibernateNature", new NullProgressMonitor() );
	}
}
