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
package org.hibernate.eclipse.console.test.project;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.HibernateConsoleTestPlugin;
import org.hibernate.eclipse.console.test.mappingproject.Customization;
import org.hibernate.eclipse.console.test.utils.FilesTransfer;
import org.hibernate.eclipse.console.test.utils.TestUtilsCommon;


/**
 * 
 * 
 * @author Dmitry Geraskov
 */
public class ConfigurableTestProject {

	private static ConfigurableTestProject singleton = null;

	public static final String PROJECT_NAME = "ConfigurableTestProject"; //$NON-NLS-1$
	public static final String RESOURCE_PATH = "res/project/"; //$NON-NLS-1$

	private IProject project;
	private IJavaProject javaProject;

	public static ConfigurableTestProject getTestProject(){
		if (singleton == null){
			singleton = new ConfigurableTestProject();
		}
		return singleton;
	}

	private ConfigurableTestProject() {
		initialize();
	}

	private void initialize(){
		try{
			buildBigTestProject();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	public IProject getIProject(){
		return this.project;
	}

	public IJavaProject getIJavaProject(){
		return this.javaProject;
	}

	public void deleteIProject(boolean deleteContent) {
		try {
			project.delete(deleteContent, true, null);
		} catch (CoreException ce) {
			throw new RuntimeException(ce);
		}
	}

	private void buildBigTestProject() throws JavaModelException, CoreException, IOException {
		TestUtilsCommon commonUtil = new TestUtilsCommon();
		project = commonUtil.buildNewProject(PROJECT_NAME);
		javaProject = commonUtil.buildJavaProject(project);

		IPath resourcePath = new Path(RESOURCE_PATH);
		File resourceFolder = resourcePath.toFile();
		URL entry = HibernateConsoleTestPlugin.getDefault().getBundle().getEntry(RESOURCE_PATH);
		URL resProject = FileLocator.resolve(entry);
		String tplPrjLcStr= FileLocator.resolve(resProject).getFile();
		resourceFolder = new File(tplPrjLcStr);
		if (!resourceFolder.exists()) {
			String out = NLS.bind(ConsoleTestMessages.MappingTestProject_folder_not_found,
					RESOURCE_PATH);
			throw new RuntimeException(out);
		}

	   	long startCopyFiles = System.currentTimeMillis();
		IPackageFragmentRoot sourceFolder = commonUtil.createSourceFolder(project, javaProject);
		FilesTransfer.copyFolder(resourceFolder, (IFolder) sourceFolder.getResource());
	   	long startCopyLibs = System.currentTimeMillis();
		List<IPath> libs = commonUtil.copyLibs(project, javaProject, resourceFolder);
	   	long startBuild = System.currentTimeMillis();
		commonUtil.generateClassPath(javaProject, libs, sourceFolder);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	   	long stopBuild = System.currentTimeMillis();
		if (Customization.USE_CONSOLE_OUTPUT){
			System.out.println("====================================================="); //$NON-NLS-1$
			System.out.println("copyFiles: " + ( ( startCopyLibs - startCopyFiles ) / 1000 )); //$NON-NLS-1$
			System.out.println("copyLibs: " + ( ( startBuild - startCopyLibs ) / 1000 )); //$NON-NLS-1$
			System.out.println("build: " + ( ( stopBuild - startBuild ) / 1000 )); //$NON-NLS-1$
		}
	}

}
