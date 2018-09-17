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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.HibernateConsoleTestPlugin;
import org.hibernate.eclipse.console.test.project.xpl.JavaProjectHelper;
import org.hibernate.eclipse.console.test.utils.FilesTransfer;

/**
 * Class wraps functionality of eclipse project creation,
 * configuration and deletion.  
 */
public class TestProject {

	public static final String SRC_FOLDER = "src"; //$NON-NLS-1$
	public static final String LIB_FOLDER = "lib"; //$NON-NLS-1$

	public static final Path JRE_CONTAINER = new Path(
		"org.eclipse.jdt.launching.JRE_CONTAINER"); //$NON-NLS-1$
	
	protected IProject project;
	protected IJavaProject javaProject;

	final protected String projectName;

	public TestProject(String projectName) {
		this.projectName = projectName;
		initialize();
	}

	public void initialize() {
		try {
			buildProject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void buildProject() throws JavaModelException, CoreException, IOException {
		project = buildNewProject(projectName);
		javaProject = buildJavaProject(project);
	}

	public IProject getIProject() {
		return project;
	}

	public IJavaProject getIJavaProject() {
		return javaProject;
	}
	
	public void deleteIProject() {
		deleteIProject(true);
	}

	public void deleteIProject(boolean deleteContent) {
		Exception ex = null;
		final int maxTryNum = 20;
		for (int i = 0; i < maxTryNum; i++) {
			ex = null;
			try {
				IContainer container = project.getParent();
				project.delete(deleteContent, true, null);
				container.refreshLocal(IResource.DEPTH_ONE, null);
				i = maxTryNum;
			} catch (CoreException e) {
				ex = e;
				if (i + 1 < maxTryNum) {
					try {
						Thread.sleep((i + 1) * 500);
					} catch (InterruptedException e1) {
					}
				}
			}
		}
		if (ex != null) {
			throw new RuntimeException(ex);
		}
		javaProject = null;
		project = null;
	}

	public void addJavaNature() throws CoreException {
		if (!project.hasNature(JavaCore.NATURE_ID)) {
			JavaProjectHelper.addNatureToProject(project, JavaCore.NATURE_ID, null);
		}
	}

	protected IJavaProject buildJavaProject(IProject project) {
		IJavaProject javaProject = JavaCore.create(project);
		try {
			addJavaNature();
		} catch (CoreException ce) {
			throw new RuntimeException(ce);
		}
		javaProject.setOption(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
		javaProject.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
		javaProject.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
		return javaProject;
	}

	public IProject buildNewProject(String projectName) {
		// get a project handle
		final IProject newProjectHandle = ResourcesPlugin.getWorkspace()
				.getRoot().getProject(projectName);
		// get a project descriptor
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace
				.newProjectDescription(newProjectHandle.getName());
		try {
			createAndOpenProject(description, newProjectHandle);
		} catch (CoreException ce) {
			throw new RuntimeException(ce);
		}
		return newProjectHandle;
	}

	protected void createAndOpenProject(IProjectDescription description,
			IProject projectHandle) throws CoreException {
		projectHandle.create(description, null);
		projectHandle.open(IResource.BACKGROUND_REFRESH, null);
	}

	public IPackageFragmentRoot createFolder(String strFolder) throws CoreException {
		IPath path = Path.fromOSString(strFolder);
		for (int i = 0; i < path.segmentCount(); i++) {
			IFolder folder = project.getFolder(path.uptoSegment(i + 1).toOSString());
			if (!folder.exists()) {
				folder.create(true, true, null);
			}
		}
		IFolder folder = project.getFolder(strFolder);
		if (!folder.exists()) {
			folder.create(true, true, null);
		}
		return javaProject.getPackageFragmentRoot(folder);
	}

	public IPackageFragmentRoot createSourceFolder() throws CoreException {
		return createFolder(SRC_FOLDER);
	}

	public List<IPath> copyLibs(File res) throws CoreException {
		return copyLibs2(res.getAbsolutePath());
	}

	public List<IPath> copyLibs2(String absolutePath) throws CoreException {
		IFolder dst = project.getFolder(LIB_FOLDER);
		if (!dst.exists()) {
			dst.create(true, true, null);
			javaProject.getPackageFragmentRoot(dst);
		}
		File libFolder = new File(absolutePath);
		if (!libFolder.exists()) {
			String out = NLS.bind(
					ConsoleTestMessages.MappingTestProject_folder_not_found,
					absolutePath);
			throw new RuntimeException(out);
		}
		List<IPath> libs = new ArrayList<IPath>();
		FilesTransfer.copyFolder(libFolder, dst, FilesTransfer.filterFilesJar, 
				FilesTransfer.filterFolders, libs);
		return libs;
	}

	public void generateClassPath(List<IPath> libs,
			IPackageFragmentRoot sourceFolder) throws JavaModelException {
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		//entries.addAll(Arrays.asList(javaProject.getRawClasspath()));
		for (IPath lib_path : libs) {
			entries.add(JavaCore.newLibraryEntry(lib_path, null, null));
		}
		if (sourceFolder != null) {
			entries.add(JavaCore.newSourceEntry(sourceFolder.getPath()));
		}
		entries.add(JavaCore.newContainerEntry(JRE_CONTAINER));
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[0]), null);
	}
	
	static public File getFolder(String path) throws IOException {
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

	@SuppressWarnings("unused")
	public void fullBuild() throws CoreException {
		IPackageFragmentRoot sourcePackageFragment = createSourceFolder();
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		////sourcePackageFragment.getResource().refreshLocal();
		//project.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		//project.refreshLocal(IResource.DEPTH_INFINITE, null);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}
}
