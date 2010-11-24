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
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.hibernate.eclipse.HibernatePlugin;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.utils.FilesTransfer;
import org.hibernate.eclipse.console.test.utils.ResourceReadUtils;

/**
 * Test project to execute codegeneration launch configuration.
 * 
 * @author Vitali Yemialyanchyk
 */
public class LaunchConfigTestProject2 extends TestProject {
	
	public static final String TEST_TMP_OUT_FOLDER = "temp_test_out_folder"; //$NON-NLS-1$
	public static final String TEST_TMP_OUT_FOLDER_ALL_EXPORTERS_EXTERNAL = "temp_test_out_folder_all_exporters_external"; //$NON-NLS-1$
	public static final String TEST_TMP_OUT_FOLDER_ALL_EXPORTERS_INTERNAL = "temp_test_out_folder_all_exporters_internal"; //$NON-NLS-1$
	public static final String META_INF_FOLDER = "src/META-INF".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String PROJECT_PATH = "res/project2/".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String RESOURCE_SRC_PATH = "res/project2/src/".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String RESOURCE_LIB_PATH = "res/project2/lib/".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String HIBERNATE_PLUGIN_LIB_PATH = "lib"; //$NON-NLS-1$
	
	public static final String LAUNCH_CODE_GEN_TEST_FILE_ALL_EXPORTERS_EXTERN = "testLaunchCfg_all_exporters_external.launch"; //$NON-NLS-1$
	public static final String LAUNCH_CODE_GEN_TEST_FILE_ALL_EXPORTERS_INTERN = "testLaunchCfg_all_exporters_internal.launch"; //$NON-NLS-1$
	public static final String LAUNCH_CODE_GEN_TEST_FILE_EXTERN = "testLaunchCfg_external.launch"; //$NON-NLS-1$
	public static final String LAUNCH_CODE_GEN_TEST_FILE_INTERN = "testLaunchCfg_internal.launch"; //$NON-NLS-1$
	public static final String LAUNCH_CONSOLE_CONFIG_TEST_FILE = "LaunchConfigTestProject2.launch"; //$NON-NLS-1$
	public static final String HIBERNATE_CONSOLE_PROPERTIES_FILE = "hibernate-console.properties"; //$NON-NLS-1$
	public static final String PERSISTENCE_XML_FILE = "persistence.xml"; //$NON-NLS-1$

	public LaunchConfigTestProject2() {
		super("LaunchConfigTestProject2"); //$NON-NLS-1$
	}

	public LaunchConfigTestProject2(String projectName) {
		super(projectName);
	}
	
	protected File getHibernatePluginFolder(String path) throws IOException {
		URL entry = HibernatePlugin.getDefault().getBundle().getEntry(path);
		URL resProject = FileLocator.resolve(entry);
		String resolvePath = FileLocator.resolve(resProject).getFile();
		File folder = new File(resolvePath);
		if (!folder.exists()) {
			String out = NLS.bind(ConsoleTestMessages.MappingTestProject_folder_not_found, path);
			throw new RuntimeException(out);
		}
		return folder;
	}
	
   	@SuppressWarnings("unused")
	protected void buildProject() throws JavaModelException, CoreException, IOException {
		super.buildProject();
		IPackageFragmentRoot tst = createFolder(TEST_TMP_OUT_FOLDER);
		tst.getResource().refreshLocal(IResource.DEPTH_INFINITE, null);
		tst = createFolder(TEST_TMP_OUT_FOLDER_ALL_EXPORTERS_EXTERNAL);
		tst.getResource().refreshLocal(IResource.DEPTH_INFINITE, null);
		tst = createFolder(TEST_TMP_OUT_FOLDER_ALL_EXPORTERS_INTERNAL);
		tst.getResource().refreshLocal(IResource.DEPTH_INFINITE, null);
		//
		importFileToProject(LAUNCH_CODE_GEN_TEST_FILE_ALL_EXPORTERS_EXTERN);
		importFileToProject(LAUNCH_CODE_GEN_TEST_FILE_ALL_EXPORTERS_INTERN);
		importFileToProject(LAUNCH_CODE_GEN_TEST_FILE_EXTERN);
		importFileToProject(LAUNCH_CODE_GEN_TEST_FILE_INTERN);
		importFileToProject(LAUNCH_CONSOLE_CONFIG_TEST_FILE);
		importFileToProject(HIBERNATE_CONSOLE_PROPERTIES_FILE);
		long startCopyFiles = System.currentTimeMillis();
		IPackageFragmentRoot sourcePackageFragment = createSourceFolder();
	   	long startCopyLibs = System.currentTimeMillis();
		final File libFolder = getFolder(RESOURCE_LIB_PATH);
		List<IPath> libs = copyLibs(libFolder);
		final File libFolderHibernatePlugin = getHibernatePluginFolder(HIBERNATE_PLUGIN_LIB_PATH);
		List<IPath> libsHibernatePlugin = copyLibs(libFolderHibernatePlugin);
		libs.addAll(libsHibernatePlugin);
	   	long startBuild = System.currentTimeMillis();
		generateClassPath(libs, sourcePackageFragment);
		setupSourceTestFolder();
		//
		tst = createFolder(META_INF_FOLDER);
		importFileToProject(META_INF_FOLDER + File.separator + PERSISTENCE_XML_FILE);
		//
		fullBuild();
	}
	
	public IFolder getTestFolder() {
		return project.getFolder(TEST_TMP_OUT_FOLDER);
	}
	
	public IFolder getTestFolderAllExportersExternal() {
		return project.getFolder(TEST_TMP_OUT_FOLDER_ALL_EXPORTERS_EXTERNAL);
	}
	
	public IFolder getTestFolderAllExportersInternal() {
		return project.getFolder(TEST_TMP_OUT_FOLDER_ALL_EXPORTERS_INTERNAL);
	}

	public boolean setupSourceTestFolder() throws IOException, CoreException {
		ArrayList<String> foldersList = new ArrayList<String>();
		File srcFolder = null;
		try {
			srcFolder = getFolder(RESOURCE_SRC_PATH);
		} catch (IOException e) {
			// ignore
		}
		if (srcFolder == null) {
			return false;
		}
		FilesTransfer.collectFoldersWithFiles(srcFolder, FilesTransfer.filterFilesJava, 
			FilesTransfer.filterFolders, foldersList);
		IPath base = Path.fromOSString(srcFolder.getPath());
		for (int i = 0; i < foldersList.size(); i++) {
			String str = foldersList.get(i);
			IPath path = Path.fromOSString(str);
			path = path.makeRelativeTo(base);
			foldersList.set(i, path.toOSString());
		}
		for (int i = 0; i < foldersList.size(); i++) {
			FilesTransfer.delete(new File(project.getLocation().append(SRC_FOLDER).toOSString()));
			final String pack = foldersList.get(i);
			srcFolder = getFolder(RESOURCE_SRC_PATH + pack);
			IPackageFragmentRoot sourcePackageFragment = createFolder(SRC_FOLDER + File.separator + pack);
			FilesTransfer.copyFolder(srcFolder, (IFolder)sourcePackageFragment.getResource());
		}
		return true;
	}

	public String getSample(String fileName) {
		return ResourceReadUtils.getSample(PROJECT_PATH + fileName);
	}

	public void importFileToProject(String fileName) throws CoreException {
		getIProject().getFile(fileName).create(
			new ByteArrayInputStream(getSample(fileName).getBytes()),
			false, new NullProgressMonitor());
	}
}

