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

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateNatureAddTest extends TestCase {
	public void testHibernateNatureAdd() throws BackingStoreException, CoreException {
		MappingTestProject project = MappingTestProject.getTestProject();
		IJavaProject prj = project.getIJavaProject();
		
		ProjectUtils.toggleHibernateOnProject(project.getIProject(), true, "testcfg");		
	}
}
