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
import java.io.FileFilter;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.project.TestProject;
import org.hibernate.eclipse.console.test.utils.ConsoleConfigUtils;

/**
 * 
 * @author vitali
 */
public class MappingTestsJpa extends MappingTestsBase {

	public MappingTestsJpa(String name) {
		super(name);
	}

	protected void setUpConsoleConfig() throws Exception {
		ConsoleConfigUtils.createJpaConsoleConfig(consoleConfigName, 
				testProject.getIProject().getName(), "PetClinic"); //$NON-NLS-1$
	}

	static final String jpaMarkerStr = TestProject.SRC_FOLDER + File.separator + "jpa"; //$NON-NLS-1$
	static final String jpaMarkerMetaInf = TestProject.SRC_FOLDER + File.separator + "meta-inf"; //$NON-NLS-1$

	public static final FileFilter filterFoldersJpa = new FileFilter() {
		public boolean accept(File f) {
			return f.exists() && f.isDirectory() && !f.isHidden() &&
				(f.getAbsolutePath().toLowerCase().contains(jpaMarkerStr) || 
						f.getAbsolutePath().toLowerCase().contains(jpaMarkerMetaInf));
		}
	};

	static final String jpaMarkerCfgXml = TestProject.SRC_FOLDER + File.separator + "hibernate.cfg.xml"; //$NON-NLS-1$

	public static final FileFilter filterFilesJavaXmlPlus = new FileFilter() {
		public boolean accept(File f) {
			return f.exists() && f.isFile() && !f.isHidden() && 
				((f.getName().toLowerCase().endsWith(".java") || f.getName().toLowerCase().endsWith(".xml")) && //$NON-NLS-1$ //$NON-NLS-2$
				!(f.getAbsolutePath().toLowerCase().contains(jpaMarkerCfgXml)));
		}
	};

	public void testEachPackWithTestSet() throws CoreException, IOException {
	   	long start_time = System.currentTimeMillis();
		boolean createListRes = testProject.createTestFoldersList(
				filterFilesJavaXmlPlus, filterFoldersJpa);
		assertTrue(createListRes);
		boolean useSelRes = testProject.useSelectedFolders();
		assertTrue(useSelRes);
		testProject.restartTestFolders();
		executions = 0;
		allTestsRunForProject();
		if (Customization.USE_CONSOLE_OUTPUT) {
			System.out.println("====================================================="); //$NON-NLS-1$
			System.out.print(result.errorCount() + ConsoleTestMessages.HibernateAllMappingTests_errors + " \t"); //$NON-NLS-1$
			System.out.print(result.failureCount() + ConsoleTestMessages.HibernateAllMappingTests_fails + "\t");						 //$NON-NLS-1$
			System.out.print((System.currentTimeMillis() - start_time) / 1000 + ConsoleTestMessages.HibernateAllMappingTests_seconds + "\t" );	 //$NON-NLS-1$
			System.out.println(executions + ConsoleTestMessages.HibernateAllMappingTests_packages_tested );
		}
	}

	protected void customizeCfgXml(IPackageFragment pack) {
		// do not customize cfg hml
	}
}
