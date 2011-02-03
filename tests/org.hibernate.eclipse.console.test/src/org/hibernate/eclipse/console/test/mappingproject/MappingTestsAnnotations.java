/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test.mappingproject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.project.ConfigurableTestProject;
import org.hibernate.eclipse.console.test.project.TestProject;
import org.hibernate.eclipse.console.test.utils.ConsoleConfigUtils;

/**
 * 
 * @author vitali
 */
public class MappingTestsAnnotations extends MappingTestsBase {

	public MappingTestsAnnotations(String name) {
		super(name);
	}

	protected void setUpConsoleConfig() throws Exception {
		createCfgXml();
		final String projName = testProject.getIProject().getName();
		ConsoleConfigUtils.createAnnotationsConsoleConfig(consoleConfigName, 
			projName, File.separator + projName + File.separator + annotationsMarkerCfgXml);
	}

	public static final String annotationsMarkerStr = TestProject.SRC_FOLDER + File.separator + "annotations"; //$NON-NLS-1$

	public static final FileFilter filterFoldersAnnotations = new FileFilter() {
		public boolean accept(File f) {
			return f.exists() && f.isDirectory() && !f.isHidden() &&
				(f.getAbsolutePath().toLowerCase().contains(annotationsMarkerStr));
		}
	};

	static final String annotationsMarkerCfgXml = TestProject.SRC_FOLDER + File.separator + ConsoleConfigUtils.CFG_FILE_NAME;
	static final String annotationsMarkerAnnotationsCfgXml = TestProject.SRC_FOLDER + File.separator + "annotations.hibernate.cfg.xml"; //$NON-NLS-1$

	public static final FileFilter filterFilesJavaXmlPlus = new FileFilter() {
		public boolean accept(File f) {
			return f.exists() && f.isFile() && !f.isHidden() && 
				((f.getName().toLowerCase().endsWith(".java") || f.getName().toLowerCase().endsWith(".xml")) && //$NON-NLS-1$ //$NON-NLS-2$
				!(f.getAbsolutePath().toLowerCase().contains(annotationsMarkerCfgXml)) &&
				!(f.getAbsolutePath().toLowerCase().contains(annotationsMarkerAnnotationsCfgXml)));
		}
	};

	public void testEachPackWithTestSet() throws CoreException, IOException {
	   	long start_time = System.currentTimeMillis();
		boolean createListRes = testProject.createTestFoldersList(
				filterFilesJavaXmlPlus, filterFoldersAnnotations);
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
		createCfgXml();
	}

	protected void createCfgXml() {
		File fileHibernateCfgXml = null;
		try {
			fileHibernateCfgXml = TestProject.getFolder(ConfigurableTestProject.RESOURCE_SRC_PATH + 
					File.separator + "annotations.hibernate.cfg.xml"); //$NON-NLS-1$
		} catch (IOException e) {
			// ignore
		}
		if (fileHibernateCfgXml == null) {
			return;
		}
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			fis = new FileInputStream(fileHibernateCfgXml);
			bis = new BufferedInputStream(fis);
			//
			IFolder srcFolder = (IFolder) testProject.createSourceFolder().getResource();
			IFile iFile = srcFolder.getFile(ConsoleConfigUtils.CFG_FILE_NAME);
			if (iFile.exists()) {
				iFile.delete(true, null);
			}
			iFile.create(bis, true, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {}
			}
		}
	}
}
