package org.hibernate.eclipse.console.test.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
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
		try {
			project.delete(deleteContent, true, null);
		} catch (CoreException ce) {
			throw new RuntimeException(ce);
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
}
