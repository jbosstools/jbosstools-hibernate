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
package org.hibernate.eclipse.criteriaeditor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.IEditorPart;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryInputModel;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.test.project.SimpleTestProject;
import org.hibernate.eclipse.console.test.project.SimpleTestProjectWithMapping;
import org.hibernate.eclipse.console.test.project.TestProject;
import org.hibernate.eclipse.console.test.utils.ConsoleConfigUtils;

/**
 * @author Dmitry Geraskov
 *
 */
public class CriteriaEditorTest extends TestCase {
	
	private static final String PROJ_NAME = "CriteriaTest"; //$NON-NLS-1$
	private static final String CONSOLE_NAME = PROJ_NAME;
	
	private SimpleTestProjectWithMapping project = null;
	
	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
		cleanUpProject();
	}
	
	protected void cleanUpProject() {
		if (project != null) {
			project.deleteIProject();
			project = null;
		}
	}
	
	public void testCriteriaEditorOpen(){
		IEditorPart editorPart = HibernateConsolePlugin.getDefault()
			.openCriteriaEditor(null, ""); //$NON-NLS-1$
		assertNotNull("Criteria Editor was not opened", editorPart); //$NON-NLS-1$
		assertTrue("Opened editor is not CriteriaEditor", editorPart instanceof CriteriaEditor); //$NON-NLS-1$
		
		CriteriaEditor editor = (CriteriaEditor)editorPart;		
		QueryInputModel model = editor.getQueryInputModel();
		assertNotNull("Model is NULL", model); //$NON-NLS-1$
	}
	
	public void testCriteriaCodeCompletion() throws CoreException, NoSuchFieldException, IllegalAccessException, IOException{
		cleanUpProject();
		project = new SimpleTestProjectWithMapping(PROJ_NAME);
		
		IPackageFragmentRoot sourceFolder = project.createSourceFolder();
		IPackageFragment pf = sourceFolder.createPackageFragment(SimpleTestProject.PACKAGE_NAME, false, null);
		ConsoleConfigUtils.customizeCfgXmlForPack(pf);
		List<IPath> libs = new ArrayList<IPath>();
		project.generateClassPath(libs, sourceFolder);
		project.fullBuild();
		
		//setup console configuration
		IPath cfgFilePath = new Path(project.getIProject().getName() + File.separator +
			TestProject.SRC_FOLDER + File.separator + ConsoleConfigUtils.CFG_FILE_NAME);
		ConsoleConfigUtils.createConsoleConfig(PROJ_NAME, cfgFilePath, CONSOLE_NAME);
		ConsoleConfiguration cc = KnownConfigurations.getInstance().find(CONSOLE_NAME);
		assertNotNull("Console Configuration not found", cc); //$NON-NLS-1$
		cc.build();
		
		String query = "Object o = new Object();\n" +  //$NON-NLS-1$
			"System.out.print(o.toString());"; //$NON-NLS-1$
		IEditorPart editorPart = HibernateConsolePlugin.getDefault()
			.openCriteriaEditor(CONSOLE_NAME, query);
		assertTrue("Opened editor is not CriteriaEditor", editorPart instanceof CriteriaEditor); //$NON-NLS-1$
		
		CriteriaEditor editor = (CriteriaEditor)editorPart;
		assertEquals(editor.getEditorText(), query);
		
		QueryInputModel model = editor.getQueryInputModel();
		assertTrue(model.getParameterCount() == 0);
		
		editor.setConsoleConfigurationName(CONSOLE_NAME);
		
		JavaCompletionProcessor processor = new JavaCompletionProcessor(editor);
		
		int position = query.indexOf("toString()"); //$NON-NLS-1$
		ICompletionProposal[] proposals = processor.computeCompletionProposals(null, position);
		assertTrue("Class java.lang.Object has at least 9 methods. But " + proposals.length //$NON-NLS-1$
			+ " code completion proposals where provided.", proposals.length >= 9); //$NON-NLS-1$
		cc.reset();
	}

}
