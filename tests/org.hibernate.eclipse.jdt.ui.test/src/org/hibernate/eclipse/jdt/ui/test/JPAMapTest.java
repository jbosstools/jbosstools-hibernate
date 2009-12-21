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
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IFolder;
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
import org.hibernate.eclipse.console.test.project.TestProject;
import org.hibernate.eclipse.console.test.utils.FilesTransfer;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.jdt.ui.internal.jpa.collect.AllEntitiesInfoCollector;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.Utils;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.AllEntitiesProcessor;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.AnnotStyle;

import junit.framework.TestCase;

/**
 * 
 * 
 * @author Vitali Yemialyanchyk
 */
@SuppressWarnings("restriction")
public class JPAMapTest extends TestCase {

	public static final String PROJECT_NAME = "TestProject"; //$NON-NLS-1$
	public static final String RESOURCE_PATH = "res/project/".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String SPECIMEN_PATH = "res/specimen/".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String TESTRESOURCE_PATH = "testresources"; //$NON-NLS-1$
	public static final String TEST_FIELDS = "fields"; //$NON-NLS-1$
	public static final String TEST_GETTERS = "getters"; //$NON-NLS-1$

	protected AllEntitiesInfoCollector collector = new AllEntitiesInfoCollector();
	protected AllEntitiesProcessor processor = new AllEntitiesProcessor();

	protected TestProject project = null;
	protected String testSelection;

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
		assertNotNull(project);
		project.deleteIProject();
		project = null;
	}

	public void testTransformerFields() {
		testSelection = TEST_FIELDS;
		processor.setDefaultStrLength(200);
		processor.setAnnotationStyle(AnnotStyle.FIELDS);
		startTestTransformer();
	}

	public void testTransformerGetters() {
		testSelection = TEST_GETTERS;
		processor.setAnnotationStyle(AnnotStyle.GETTERS);
		startTestTransformer();
	}

	public void startTestTransformer() {
		try {
			createTestProject();
		} catch (CoreException e1) {
			fail(e1.getMessage());
		} catch (IOException e1) {
			fail(e1.getMessage());
		}
		assertNotNull(project);
		//
		IJavaProject javaProject = ProjectUtils.findJavaProject(PROJECT_NAME);
		assertNotNull(javaProject);
		try {
			javaProject.getProject().open(null);
		} catch (CoreException e) {
			fail(e.getMessage());
		}
		//ICompilationUnit icu = Utils.findCompilationUnit(javaProject,
		//		"test.annotated." + testSelection + ".Document"); //$NON-NLS-1$ //$NON-NLS-2$
		ICompilationUnit icu = Utils.findCompilationUnit(javaProject,
				"test.annotated." + testSelection + ".Passport"); //$NON-NLS-1$ //$NON-NLS-2$
		ICompilationUnit icu2 = Utils.findCompilationUnit(javaProject,
				"test.annotated." + testSelection + ".Staff"); //$NON-NLS-1$ //$NON-NLS-2$
		ICompilationUnit icu3 = Utils.findCompilationUnit(javaProject,
				"test.annotated." + testSelection + ".FotoXPerson"); //$NON-NLS-1$ //$NON-NLS-2$
		ICompilationUnit icu4 = Utils.findCompilationUnit(javaProject,
				"test.annotated." + testSelection + ".ZTypesComplex"); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			icu4.becomeWorkingCopy(null);
		} catch (JavaModelException e) {
			// ignore
		}
		//ICompilationUnit icu = Utils.findCompilationUnit(javaProject,
		//		"test.annotated." + testSelection + ".Foto"); //$NON-NLS-1$ //$NON-NLS-2$
		//ICompilationUnit icu2 = Utils.findCompilationUnit(javaProject,
		//		"test.annotated." + testSelection + ".Person"); //$NON-NLS-1$ //$NON-NLS-2$
		ICompilationUnit icu44 = null;
		if (testSelection.equals(TEST_GETTERS)) {
			icu44 = Utils.findCompilationUnit(javaProject,
					"test.annotated." + testSelection + ".FotoXPerson"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		assertNotNull(icu);
		assertNotNull(icu2);
		assertNotNull(icu3);
		assertNotNull(icu4);
		collector.initCollector();
		collector.collect(icu);
		collector.collect(icu2);
		collector.collect(icu3);
		collector.collect(icu4);
		if (icu44 != null) {
			collector.collect(icu44);
		}
		collector.resolveRelations();
		processor.setEnableOptLock(true);
		processor.modify(collector.getMapCUs_Info(), false, null);
		//
		checkItem("DocumentBase"); //$NON-NLS-1$
		checkItem("Document"); //$NON-NLS-1$
		checkItem("Foto"); //$NON-NLS-1$
		checkItem("Passport"); //$NON-NLS-1$
		checkItem("Person"); //$NON-NLS-1$
		checkItem("Staff"); //$NON-NLS-1$
		checkItem("FotoXPerson"); //$NON-NLS-1$
		checkItem("PersonXFoto"); //$NON-NLS-1$
		checkItem("Country"); //$NON-NLS-1$
		checkItem("Visa"); //$NON-NLS-1$
		checkItem("ZTypesComplex"); //$NON-NLS-1$
		if (icu44 != null) {
			checkItem("Entity"); //$NON-NLS-1$
		}
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
		ICompilationUnit icu = Utils.findCompilationUnit(project.getIJavaProject(),
				"test.annotated." + testSelection + //$NON-NLS-1$ 
				"." + strName); //$NON-NLS-1$
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(icu);
		ASTNode astNode = parser.createAST(null);
		return astNode;
	}

	protected ASTNode getSpecimen(String strName) throws IOException {
		File resourceFile = getResourceItem(SPECIMEN_PATH
				+ "test" + File.separator  //$NON-NLS-1$
				+ "annotated" + File.separator   //$NON-NLS-1$
				+ testSelection + File.separator + strName 
				+ ".java"); //$NON-NLS-1$
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
		project = new TestProject(PROJECT_NAME);
		File resourceFolder = getResourceItem(RESOURCE_PATH);
		if (!resourceFolder.exists()) {
			String out = NLS.bind(
					ConsoleTestMessages.MappingTestProject_folder_not_found,
					RESOURCE_PATH);
			throw new RuntimeException(out);
		}
		IPackageFragmentRoot sourceFolder = project.createSourceFolder();
		FilesTransfer.copyFolder(resourceFolder, (IFolder) sourceFolder
				.getResource());
		File resourceFolderLib = getResourceItem(TESTRESOURCE_PATH);
		if (!resourceFolderLib.exists()) {
			String out = NLS.bind(
					ConsoleTestMessages.MappingTestProject_folder_not_found,
					RESOURCE_PATH);
			throw new RuntimeException(out);
		}
		List<IPath> libs = project.copyLibs2(resourceFolderLib.getAbsolutePath());
		project.generateClassPath(libs, sourceFolder);
	}

}
