package org.hibernate.eclipse.console.test.project;

import java.io.IOException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class SimpleTestProject extends TestProject {
	
	public static final String PACKAGE_NAME = "test"; //$NON-NLS-1$
	public static final String TYPE_NAME = "TestClass"; //$NON-NLS-1$
	public static final String FILE_NAME = "TestClass.java"; //$NON-NLS-1$
	
	public SimpleTestProject() {
		super("HibernateToolsTestProject"); //$NON-NLS-1$
	}

	public SimpleTestProject(String projectName) {
		super(projectName);
	}
	
	public String getFullyQualifiedTestClassName(){
		return PACKAGE_NAME + "." + TYPE_NAME; //$NON-NLS-1$
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
		return getTestClassType().getField("testField"); //$NON-NLS-1$
	}
	
	protected void buildProject() throws JavaModelException, CoreException, IOException {
		super.buildProject();
		buildType(buildPackage(PACKAGE_NAME, project, javaProject), FILE_NAME);
	}

	private IPackageFragmentRoot buildSourceFolder(IProject project,
			IJavaProject javaProject) throws CoreException {
		IFolder folder = project.getFolder(SRC_FOLDER);
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

	protected IType buildType(IPackageFragment pack, String cuName)
			throws JavaModelException {
		
		//create empty ICompilationUnit
		
		ICompilationUnit cu = pack.createCompilationUnit(cuName,
				"", false, null); //$NON-NLS-1$
		
		cu.createPackageDeclaration(pack.getElementName(),null);
		IType type = cu.createType("public class " + TYPE_NAME + " {}",null,false,null);  //$NON-NLS-1$//$NON-NLS-2$
		type.createField("private String testField;",null,false,null); //$NON-NLS-1$
		type.createMethod("public String getTestField() {return this.testField;}",null,false,null); //$NON-NLS-1$
		type.createMethod("public void setTestField(String testField) {this.testField = testField;}",null,false,null); //$NON-NLS-1$
		return type;
	}

}

