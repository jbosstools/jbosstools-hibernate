/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test.mappingproject;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.project.TestProject;
import org.hibernate.eclipse.console.test.utils.ConsoleConfigUtils;

/**
 * 
 */
public class MappingTestsCore extends MappingTestsBase {

	public MappingTestsCore(String name) {
		super(name);
	}

	protected void setUpConsoleConfig() throws Exception {
		IPath cfgFilePath = new Path(testProject.getIProject().getName() + File.separator +
				TestProject.SRC_FOLDER + File.separator + ConsoleConfigUtils.CFG_FILE_NAME);
		ConsoleConfigUtils.createConsoleConfig(consoleConfigName, 
				cfgFilePath, testProject.getIProject().getName());
	}

	public void testEachPackWithTestSet() throws CoreException, IOException {
	   	long start_time = System.currentTimeMillis();
	   	// 1) ---
	   	// here we use one BIG project configuration for testing - time consuming thing
		//boolean useAllRes = testProject.useAllSources();
		//assertTrue(useAllRes);
	   	// 1) ---
	   	// 2) +++
	   	// here we use many SMALL projects configurations for testing.
	   	// this case is essential better for run time.
		boolean createListRes = testProject.createTestFoldersList();
		assertTrue(createListRes);
	   	// 2) +++
		testProject.restartTestFolders();
		executions = 0;
		while (testProject.setupNextTestFolder()) {
			allTestsRunForProject();
		}
		if (Customization.USE_CONSOLE_OUTPUT) {
			System.out.println("====================================================="); //$NON-NLS-1$
			System.out.print(result.errorCount() + ConsoleTestMessages.HibernateAllMappingTests_errors + " \t"); //$NON-NLS-1$
			System.out.print(result.failureCount() + ConsoleTestMessages.HibernateAllMappingTests_fails + "\t");						 //$NON-NLS-1$
			System.out.print((System.currentTimeMillis() - start_time) / 1000 + ConsoleTestMessages.HibernateAllMappingTests_seconds + "\t" );	 //$NON-NLS-1$
			System.out.println(executions + ConsoleTestMessages.HibernateAllMappingTests_packages_tested );
		}
	}
}
