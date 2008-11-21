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
package org.hibernate.eclipse.jdt.ui.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.osgi.util.NLS;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.mappingproject.TestUtilsCommon;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.jdt.ui.internal.jpa.collect.AllEntitiesInfoCollector;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.Utils;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.AllEntitiesProcessor;

import junit.framework.TestCase;

/**
 * 
 * 
 * @author Vitali
 */
public class JPAMapTest extends TestCase {

	public static String PROJECT_NAME = "TestProject"; //$NON-NLS-1$
	public static String RESOURCE_PATH = "res/project/"; //$NON-NLS-1$
	public static String SPECIMEN_PATH = "res/specimen/"; //$NON-NLS-1$
	public static String TESTRESOURCE_PATH = "testresources"; //$NON-NLS-1$
	static {
		RESOURCE_PATH.replaceAll("//", File.separator); //$NON-NLS-1$
		SPECIMEN_PATH.replaceAll("//", File.separator); //$NON-NLS-1$
	}

	protected AllEntitiesInfoCollector collector = new AllEntitiesInfoCollector();
	protected AllEntitiesProcessor processor = new AllEntitiesProcessor();

	protected IProject project;
	protected IJavaProject javaProject;

	protected void setUp() throws Exception {
		try {
			createTestProject();
		} catch (JavaModelException e1) {
			fail(e1.getMessage());
		} catch (CoreException e1) {
			fail(e1.getMessage());
		} catch (IOException e1) {
			fail(e1.getMessage());
		}
		assertNotNull(project);
		assertNotNull(javaProject);
	}

	protected void tearDown() throws Exception {
		try {
			project.delete(true, true, null);
			project = null;
			javaProject = null;
		} catch (CoreException e) {
			fail(e.getMessage());
		}
		assertNull(project);
		assertNull(javaProject);
	}

	public void testTransformer() {
		javaProject = ProjectUtils.findJavaProject(PROJECT_NAME);
		assertNotNull(javaProject);
		try {
			javaProject.getProject().open(null);
		} catch (CoreException e) {
			fail(e.getMessage());
		}
		ICompilationUnit icu = Utils.findCompilationUnit(javaProject,
				"test.annotated.Passport"); //$NON-NLS-1$
		ICompilationUnit icu2 = Utils.findCompilationUnit(javaProject,
				"test.annotated.Staff"); //$NON-NLS-1$
		//ICompilationUnit icu = Utils.findCompilationUnit(javaProject,
		//		"test.annotated.Foto"); //$NON-NLS-1$
		//ICompilationUnit icu2 = Utils.findCompilationUnit(javaProject,
		//		"test.annotated.Person"); //$NON-NLS-1$
		assertNotNull(icu);
		assertNotNull(icu2);
		collector.initCollector(javaProject);
		collector.collect(icu);
		collector.collect(icu2);
		collector.resolveRelations();
		processor.modify(javaProject, collector.getMapCUs_Info(), false);
		//
		checkItem("Document"); //$NON-NLS-1$
		checkItem("Foto"); //$NON-NLS-1$
		checkItem("Passport"); //$NON-NLS-1$
		checkItem("Person"); //$NON-NLS-1$
		checkItem("Staff"); //$NON-NLS-1$
	}

	protected void checkItem(String strCheckItem) {
		ASTNode specimen, generated;
		specimen = null;
		try {
			specimen = getSpecimen(strCheckItem);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(specimen);
		generated = getGenerated(strCheckItem);
		assertNotNull(generated);
		assertEquals(specimen.toString(), generated.toString());
	}

	protected ASTNode getGenerated(String strName) {
		ICompilationUnit icu = Utils.findCompilationUnit(javaProject,
				"test.annotated." + strName); //$NON-NLS-1$
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(icu);
		ASTNode astNode = parser.createAST(null);
		return astNode;
	}

	protected ASTNode getSpecimen(String strName) throws IOException {
		File resourceFile = getResourceItem(SPECIMEN_PATH
				+ "test" + File.separator + "annotated" + File.separator + strName + ".java"); //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
		if (!resourceFile.exists()) {
			return null;
		}
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		StringBuffer cbuf = new StringBuffer((int) resourceFile.length());
		try {
			String ls = System.getProperties().getProperty("line.separator", "\n");  //$NON-NLS-1$//$NON-NLS-2$
			BufferedReader in = new BufferedReader(new FileReader(resourceFile));
			String str;
			while ((str = in.readLine()) != null) {
				cbuf.append(str + ls);
			}
			in.close();
		} catch (IOException e) {
		}

		parser.setSource(cbuf.toString().toCharArray());
		ASTNode astNode = parser.createAST(null);
		return astNode;
	}

	protected File getResourceItem(String strResPath) throws IOException {
		IPath resourcePath = new Path(strResPath);
		File resourceFolder = resourcePath.toFile();
		URL entry = HibernateJDTuiTestPlugin.getDefault().getBundle().getEntry(
				strResPath);
		URL resProject = FileLocator.resolve(entry);
		String tplPrjLcStr = FileLocator.resolve(resProject).getFile();
		resourceFolder = new File(tplPrjLcStr);
		return resourceFolder;
	}

	protected void createTestProject() throws JavaModelException,
			CoreException, IOException {
		TestUtilsCommon commonUtil = new TestUtilsCommon();
		project = commonUtil.buildNewProject(PROJECT_NAME);
		javaProject = commonUtil.buildJavaProject(project);
		File resourceFolder = getResourceItem(RESOURCE_PATH);
		if (!resourceFolder.exists()) {
			String out = NLS.bind(
					ConsoleTestMessages.MappingTestProject_folder_not_found,
					RESOURCE_PATH);
			throw new RuntimeException(out);
		}
		IPackageFragmentRoot sourceFolder = commonUtil.createSourceFolder(
				project, javaProject);
		commonUtil.recursiveCopyFiles(resourceFolder, (IFolder) sourceFolder
				.getResource());
		File resourceFolderLib = getResourceItem(TESTRESOURCE_PATH);
		if (!resourceFolderLib.exists()) {
			String out = NLS.bind(
					ConsoleTestMessages.MappingTestProject_folder_not_found,
					RESOURCE_PATH);
			throw new RuntimeException(out);
		}
		List<IPath> libs = commonUtil.copyLibs2(project, javaProject,
				resourceFolderLib.getAbsolutePath());
		commonUtil.generateClassPath(javaProject, libs, sourceFolder);
	}

}
