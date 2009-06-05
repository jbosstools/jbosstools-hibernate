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

import org.eclipse.jdt.core.IPackageFragment;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.project.ConfigurableTestProject;

import junit.framework.TestSuite;

/**
 * @author Dmitry Geraskov
 *
 */
public class TestSet {
	
	/**
	 * Creates test suite for configurable project
	 * @param consoleConfigName
	 * @param testPackage
	 * @param testProject
	 * @return
	 */
	public static TestSuite createTestSuite(String consoleConfigName,
			IPackageFragment testPackage, ConfigurableTestProject testProject) {
		TestSuite suite = new TestSuite(ConsoleTestMessages.TestSet_test_for_mappingtestproject);
		HbmExportExceptionTest test1 = new HbmExportExceptionTest("testHbmExportExceptionTest"); //$NON-NLS-1$
		test1.setConsoleConfigName(consoleConfigName);
		test1.setTestPackage(testPackage);
		test1.setTestProject(testProject);
		/**/
		OpenSourceFileTest test2 = new OpenSourceFileTest("testOpenSourceFileTest"); //$NON-NLS-1$
		test2.setConsoleConfigName(consoleConfigName);
		test2.setTestPackage(testPackage);
		OpenMappingFileTest test3 = new OpenMappingFileTest("testOpenMappingFileTest"); //$NON-NLS-1$
		test3.setConsoleConfigName(consoleConfigName);
		test3.setTestPackage(testPackage);
		OpenMappingDiagramTest test4 = new OpenMappingDiagramTest("testOpenMappingDiagram"); //$NON-NLS-1$
		test4.setConsoleConfigName(consoleConfigName);
		test4.setTestPackage(testPackage);
		/**/
		//
		suite.addTest(test1);
		/**/
		suite.addTest(test2);
		suite.addTest(test3);
		suite.addTest(test4);
		/**/
		return suite;
	}
}
