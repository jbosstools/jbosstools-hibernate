/*******************************************************************************
 * Copyright (c) 2005 Oracle. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Oracle. - initial API and implementation
 ******************************************************************************/        
package org.hibernate.eclipse.console;

import java.io.StringBufferInputStream; //used for v2

import org.eclipse.core.resources.IFile; //used for v2
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

//currently IType based, v2 is IFile based
public class SimpleTestProject {

	IProject project;
	IJavaProject javaProject;
	
	public static String PROJECT_NAME = "SimpleTestProject";
	public static String PACKAGE_NAME = "test";
	public static String TYPE_NAME = "TestClass";
	public static String FILE_NAME = "TestClass.java";
	
	
	public SimpleTestProject() {
		initialize();
	}

	void initialize(){
		try{
			buildSimpleTestProject();
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
	
	public String getFullyQualifiedTestClassName(){
		return PACKAGE_NAME + "." + TYPE_NAME;
	}
	
	public IType getTestClassType() {
		
		IType type = null;
		try {
			type = getIJavaProject().findType(getFullyQualifiedTestClassName());
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
		return type;
	}
	
	public IField getTestClassField() {
		return getTestClassType().getField("testField");
	}
	
	public void deleteIProject() {
		try {
			project.delete(true, true, null);
		} catch (CoreException ce) {
			throw new RuntimeException(ce);
		}

	}
	
	private void buildSimpleTestProject() throws JavaModelException, CoreException {
		project = buildNewProject(PROJECT_NAME);
		javaProject = buildJavaProject(project);

		IType primaryType = buildType(
				buildPackage(PACKAGE_NAME, project, javaProject), FILE_NAME);

		// v2 IFile file = buildTestClass(project);
		// v2 ICompilationUnit cu = (ICompilationUnit) JavaCore.create(file);

	}

	private IProject buildNewProject(String projectName) {

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

	private void createAndOpenProject(IProjectDescription description,
			IProject projectHandle) throws CoreException {

		projectHandle.create(description, null);
		projectHandle.open(IResource.BACKGROUND_REFRESH, null);
	}

	private IJavaProject buildJavaProject(IProject project) {

		IJavaProject javaProject = JavaCore.create(project);
		try {
			setJavaNature(project);
		} catch (CoreException ce) {
			throw new RuntimeException(ce);
		}
		
		javaProject.setOption(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
		javaProject.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
		javaProject.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
		return javaProject;
	}

	private void setJavaNature(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);
	}




	//for use with "v2" method of building a resource to work with
//	private IFile buildTestClass(IProject project) {
//		IFile file = project.getFile("TestClass2.java");
//
//		StringBuffer buf = new StringBuffer();
//		buf.append("public class TestClass2 {" + "\n");
//		buf.append("\n");
//		buf.append("}" + "\n");
//		try {
//			//I need an InputStream
//			file.create(new StringBufferInputStream(buf.toString()), true,null);
//		} catch (CoreException ce) {
//			throw new RuntimeException(ce);
//		}
//
//		return file;
//	}

	private IPackageFragmentRoot buildSourceFolder(IProject project,
			IJavaProject javaProject) throws CoreException {
		IFolder folder = project.getFolder("src");
		folder.create(false, true, null);
		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(folder);
		IClasspathEntry[] newEntries = { JavaCore
				.newSourceEntry(root.getPath()) };
		javaProject.setRawClasspath(newEntries, null);
		return root;
	}

	private IPackageFragment buildPackage(String name, IProject project,
			IJavaProject javaProject) throws CoreException {
		IPackageFragmentRoot sourceFolder = buildSourceFolder(project,
				javaProject);
		return sourceFolder.createPackageFragment(name, false, null);
	}

	private IType buildType(IPackageFragment pack, String cuName)
			throws JavaModelException {
		
		//create empty ICompilationUnit
		
		ICompilationUnit cu = pack.createCompilationUnit(cuName,
				"", false, null);
		
		cu.createPackageDeclaration(pack.getElementName(),null);
		IType type = cu.createType("public class " + TYPE_NAME + " {}",null,false,null);
		type.createField("private String testField;",null,false,null);
		type.createMethod("public String getTestField() {return this.testField;}",null,false,null);
		type.createMethod("public void setTestField(String testField) {this.testField = testField;}",null,false,null);
		return type;
	}

}

