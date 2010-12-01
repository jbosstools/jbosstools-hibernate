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

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.IEditorPart;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryInputModel;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.test.project.ConfigurableTestProject;
import org.hibernate.eclipse.console.test.project.TestProject;
import org.hibernate.eclipse.console.test.utils.ConsoleConfigUtils;

/**
 * @author Dmitry Geraskov
 *
 */
public class CriteriaEditorTest extends TestCase {
	
	private static final String PROJ_NAME = "CriteriaTest";
	private static final String CONSOLE_NAME = PROJ_NAME;
	
	private ConfigurableTestProject project = null;
	
	protected void setUp() throws Exception {
		project = new ConfigurableTestProject(PROJ_NAME);
	}

	protected void tearDown() throws Exception {
		project.deleteIProject();
		project = null;
	}
	
	public void testCriteriaEditorOpen(){
		IEditorPart editorPart = HibernateConsolePlugin.getDefault()
			.openCriteriaEditor(null, "");
		assertNotNull("Criteria Editor was not opened", editorPart);
		assertTrue("Opened editor is not CriteriaEditor", editorPart instanceof CriteriaEditor);
		
		CriteriaEditor editor = (CriteriaEditor)editorPart;		
		QueryInputModel model = editor.getQueryInputModel();
		assertNotNull("Model is NULL", model);
	}
	
	public void testCriteriaCodeCompletion() throws CoreException, NoSuchFieldException, IllegalAccessException, IOException{
		//setup console configuration
		IPath cfgFilePath = new Path(project.getIProject().getName() + File.separator +
				TestProject.SRC_FOLDER + File.separator + ConsoleConfigUtils.CFG_FILE_NAME);
		ConsoleConfigUtils.createConsoleConfig(PROJ_NAME, cfgFilePath, CONSOLE_NAME);
		boolean createListRes = project.createTestFoldersList();
		assertTrue(createListRes);
		project.setupNextTestFolder();
		
		IPackageFragment packFragment = project.getCurrentPackage();
		assertNotNull(packFragment);
		ConsoleConfigUtils.customizeCfgXmlForPack(packFragment);
		project.fullBuild();
		
		ConsoleConfiguration cc = KnownConfigurations.getInstance().find(CONSOLE_NAME);
		assertNotNull("Console Configuration not found", cc);
		
		String query = "Object o = new Object();\n" + 
				"System.out.print(o.toString());";
		IEditorPart editorPart = HibernateConsolePlugin.getDefault()
			.openCriteriaEditor(CONSOLE_NAME, query);
		assertTrue("Opened editor is not CriteriaEditor", editorPart instanceof CriteriaEditor);
		
		CriteriaEditor editor = (CriteriaEditor)editorPart;
		assertEquals(editor.getEditorText(), query);
		
		QueryInputModel model = editor.getQueryInputModel();
		assertTrue(model.getParameterCount() == 0);
		
		JavaCompletionProcessor processor = new JavaCompletionProcessor(editor);
		
		int position = query.indexOf("toString()");
		ICompletionProposal[] proposals = processor.computeCompletionProposals(null, position);
		assertTrue("Class java.lang.Object has at least 9 methods. But " + proposals.length
				+ " code completion proposals where provided.", proposals.length >= 9);
		cc.reset();
	}

}
