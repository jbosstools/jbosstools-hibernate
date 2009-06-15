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
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.HibernateConsoleTestPlugin;
import org.hibernate.eclipse.console.test.mappingproject.Customization;
import org.hibernate.eclipse.console.test.utils.FilesTransfer;

/**
 * 
 * @author Dmitry Geraskov
 * @author Vitali Yemialyanchyk
 */
public class ConfigurableTestProject extends TestProject {

	public static final String RESOURCE_SRC_PATH = "res/project/src/".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String RESOURCE_LIB_PATH = "res/project/lib/".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$
	
	protected ArrayList<String> foldersList = new ArrayList<String>();

	protected int activePackage = -1;

	public ConfigurableTestProject(String projectName) {
		super(projectName);
	}

	protected void buildProject() throws JavaModelException, CoreException, IOException {
		super.buildProject();
		//final File srcFolder = getFolder(RESOURCE_SRC_PATH);
	   	long startCopyFiles = System.currentTimeMillis();
		IPackageFragmentRoot sourcePackageFragment = createSourceFolder();
		//FilesTransfer.copyFolder(srcFolder, (IFolder)sourcePackageFragment.getResource());
	   	long startCopyLibs = System.currentTimeMillis();
		final File libFolder = getFolder(RESOURCE_LIB_PATH);
		List<IPath> libs = copyLibs(libFolder);
	   	long startBuild = System.currentTimeMillis();
		generateClassPath(libs, sourcePackageFragment);
		fullBuild();
	   	long stopBuild = System.currentTimeMillis();
		if (Customization.USE_CONSOLE_OUTPUT){
			System.out.println("====================================================="); //$NON-NLS-1$
			System.out.println("copyFiles: " + ( ( startCopyLibs - startCopyFiles ) / 1000 )); //$NON-NLS-1$
			System.out.println("copyLibs: " + ( ( startBuild - startCopyLibs ) / 1000 )); //$NON-NLS-1$
			System.out.println("build: " + ( ( stopBuild - startBuild ) / 1000 )); //$NON-NLS-1$
		}
	}

	public void restartTestFolders() {
		activePackage = -1;
	}

	public boolean setupNextTestFolder() throws IOException, CoreException {
		activePackage++;
		if (activePackage >= foldersList.size()) {
			return false;
		}
		FilesTransfer.delete(new File(project.getLocation().append(SRC_FOLDER).toOSString()));
		final String pack = foldersList.get(activePackage);
		final File srcFolder = getFolder(RESOURCE_SRC_PATH + pack);
		IPackageFragmentRoot sourcePackageFragment = createFolder(SRC_FOLDER + File.separator + pack);
		FilesTransfer.copyFolder(srcFolder, (IFolder)sourcePackageFragment.getResource());
		return true;
	}

	protected File getFolder(String path) throws IOException {
		URL entry = HibernateConsoleTestPlugin.getDefault().getBundle().getEntry(path);
		URL resProject = FileLocator.resolve(entry);
		String resolvePath = FileLocator.resolve(resProject).getFile();
		File folder = new File(resolvePath);
		if (!folder.exists()) {
			String out = NLS.bind(ConsoleTestMessages.MappingTestProject_folder_not_found, path);
			throw new RuntimeException(out);
		}
		return folder;
	}

	public boolean createTestFoldersList(FileFilter filterFiles, FileFilter filterFolders) {
		activePackage = -1;
		foldersList = new ArrayList<String>();
		File srcFolder = null;
		try {
			srcFolder = getFolder(RESOURCE_SRC_PATH);
		} catch (IOException e) {
			// ignore
		}
		if (srcFolder == null) {
			return false;
		}
		FilesTransfer.collectFoldersWithFiles(srcFolder, filterFiles, filterFolders, foldersList);
		IPath base = Path.fromOSString(srcFolder.getPath());
		for (int i = 0; i < foldersList.size(); i++) {
			String str = foldersList.get(i);
			IPath path = Path.fromOSString(str);
			path = path.makeRelativeTo(base);
			foldersList.set(i, path.toOSString());
		}
		return true;
	}

	public boolean createTestFoldersList() {
		return createTestFoldersList(FilesTransfer.filterFilesJava, FilesTransfer.filterFolders);
	}

	public boolean useAllSources() {
		activePackage = -1;
		foldersList = new ArrayList<String>();
		File srcFolder = null;
		try {
			srcFolder = getFolder(RESOURCE_SRC_PATH);
		} catch (IOException e) {
			// ignore
		}
		IPackageFragmentRoot sourcePackageFragment = null;
		try {
			sourcePackageFragment = createSourceFolder();
		} catch (CoreException e) {
			// ignore
		}
		if (srcFolder != null && sourcePackageFragment != null) {
			FilesTransfer.copyFolder(srcFolder, (IFolder)sourcePackageFragment.getResource());
			foldersList.add(""); //$NON-NLS-1$
			return true;
		}
		return false;
	}

	public boolean useSelectedFolders() throws IOException, CoreException {
		activePackage = -1;
		if (foldersList == null) {
			return false;
		}
		FilesTransfer.delete(new File(project.getLocation().append(SRC_FOLDER).toOSString()));
		for (int i = 0; i < foldersList.size(); i++) {
			final String pack = foldersList.get(i);
			final File srcFolder = getFolder(RESOURCE_SRC_PATH + pack);
			IPackageFragmentRoot sourcePackageFragment = createFolder(SRC_FOLDER + File.separator + pack);
			FilesTransfer.copyFolder(srcFolder, (IFolder)sourcePackageFragment.getResource());
		}
		foldersList = new ArrayList<String>();
		foldersList.add(""); //$NON-NLS-1$
		return true;
	}

	public void fullBuild() throws CoreException {
		IPackageFragmentRoot sourcePackageFragment = createSourceFolder();
		sourcePackageFragment.getResource().refreshLocal(IResource.DEPTH_INFINITE, null);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}
}
