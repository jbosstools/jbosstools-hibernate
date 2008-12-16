/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;

/**
 * 
 * 
 * @author Vitali
 */
public class TestUtilsCommon {

	public static final String SRC_FOLDER = "src"; //$NON-NLS-1$
	public static final String LIB_FOLDER = "lib"; //$NON-NLS-1$

	public static final Path JRE_CONTAINER = new Path(
			"org.eclipse.jdt.launching.JRE_CONTAINER"); //$NON-NLS-1$

	protected FileFilter fileFilter = new FileFilter() {
		public boolean accept(File pathname) {
			return !pathname.isDirectory();
		}
	};

	protected FileFilter dirFilter = new FileFilter() {
		public boolean accept(File pathname) {
			// exclude ".svn" and other unnessesary folders
			if (pathname.getName().charAt(0) == '.')
				return false;
			if (LIB_FOLDER.equals(pathname.getName()))
				return false;
			return pathname.isDirectory();
		}
	};

	protected FileFilter jarFilter = new FileFilter() {
		public boolean accept(File pathname) {
			return !pathname.isDirectory()
					|| pathname.getName().endsWith(".jar"); //$NON-NLS-1$
		}
	};

	public FileFilter getFileFilter() {
		return fileFilter;
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public FileFilter getDirFilter() {
		return dirFilter;
	}

	public void setDirFilter(FileFilter dirFilter) {
		this.dirFilter = dirFilter;
	}

	public FileFilter getJarFilter() {
		return jarFilter;
	}

	public void setJarFilter(FileFilter jarFilter) {
		this.jarFilter = jarFilter;
	}

	public void recursiveCopyFiles(File src, IFolder dst) {
		File[] files = src.listFiles(fileFilter);
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (!file.exists()) {
				continue;
			}
			IFile iFile = dst.getFile(file.getName());
			try {
				if (iFile.exists()) {
					iFile.delete(true, null);
				}
				iFile.create(new FileInputStream(file), true, null);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				continue;
			} catch (CoreException e) {
				e.printStackTrace();
				continue;
			}
		}

		File[] dirs = src.listFiles(dirFilter);
		for (int i = 0; i < dirs.length; i++) {
			File dir = dirs[i];
			if (!dir.exists()) {
				continue;
			}
			IFolder iFolder = dst.getFolder(dir.getName());
			try {
				if (!iFolder.exists()) {
					iFolder.create(true, true, null);
				}
				recursiveCopyFiles(dir, iFolder);
			} catch (CoreException e) {
				e.printStackTrace();
				continue;
			}
		}
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

	public void createAndOpenProject(IProjectDescription description,
			IProject projectHandle) throws CoreException {

		try {
			projectHandle.create(description, null);
		} catch (ResourceException re) {
			// if the project exist - ignore exception
			if (re.getStatus().getCode() != 374 || re.getStatus().getSeverity() != IStatus.ERROR ||
					!"org.eclipse.core.resources".equals(re.getStatus().getPlugin())) { //$NON-NLS-1$
				throw re;
			}
		}
		projectHandle.open(IResource.BACKGROUND_REFRESH, null);
	}

	public IJavaProject buildJavaProject(IProject project) {
		IJavaProject javaProject = JavaCore.create(project);
		try {
			setJavaNature(project);
		} catch (CoreException ce) {
			throw new RuntimeException(ce);
		}

		javaProject.setOption(JavaCore.COMPILER_COMPLIANCE,
				JavaCore.VERSION_1_5);
		javaProject.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
				JavaCore.VERSION_1_5);
		javaProject.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
		return javaProject;
	}

	public void setJavaNature(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);
	}

	public IPackageFragmentRoot createFolder(IProject project,
			IJavaProject javaProject, String strFolder) throws CoreException {
		IFolder folder = project.getFolder(strFolder);
		if (!folder.exists()) {
			folder.create(false, true, null);
			IPackageFragmentRoot root = javaProject
					.getPackageFragmentRoot(folder);
			/*
			 * IClasspathEntry[] newEntries = { JavaCore
			 * .newSourceEntry(root.getPath()) , JavaCore
			 * .newContainerEntry(JRE_CONTAINER)};
			 * javaProject.setRawClasspath(newEntries, null);
			 */
			return root;
		}
		return javaProject.getPackageFragmentRoot(folder);
	}

	public IPackageFragmentRoot createSourceFolder(IProject project,
			IJavaProject javaProject) throws CoreException {
		return createFolder(project, javaProject, SRC_FOLDER);
	}

	public List<IPath> copyLibs(IProject project, IJavaProject javaProject,
			File res) throws CoreException {
		return copyLibs2(project, javaProject,
				res.getAbsolutePath() + File.separator + TestUtilsCommon.LIB_FOLDER);
	}

	public List<IPath> copyLibs2(IProject project, IJavaProject javaProject,
			String absolutePath) throws CoreException {
		IFolder dst = project.getFolder(TestUtilsCommon.LIB_FOLDER);
		if (!dst.exists()) {
			dst.create(false, true, null);
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

		File[] files = libFolder.listFiles(jarFilter);
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (!file.exists()) {
				continue;
			}
			IFile iFile = dst.getFile(file.getName());
			if (iFile.exists()) {
				iFile.delete(true, null);
			}
			try {
				iFile.create(new FileInputStream(file), true, null);
				libs.add(iFile.getFullPath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				continue;
			}
		}
		return libs;
	}

	public void generateClassPath(IJavaProject javaProject, List<IPath> libs,
			IPackageFragmentRoot sourceFolder) throws JavaModelException {
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		// entries.addAll(Arrays.asList(javaProject.getRawClasspath()));
		for (IPath lib_path : libs) {
			entries.add(JavaCore.newLibraryEntry(lib_path, null, null));
		}
		entries.add(JavaCore.newSourceEntry(sourceFolder.getPath()));
		entries.add(JavaCore.newContainerEntry(JRE_CONTAINER));
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[0]),
				null);
	}
}
