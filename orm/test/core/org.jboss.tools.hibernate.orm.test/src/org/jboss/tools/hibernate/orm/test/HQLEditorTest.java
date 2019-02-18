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
package org.jboss.tools.hibernate.orm.test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPage;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryInputModel;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.views.QueryParametersPage;
import org.hibernate.eclipse.console.views.QueryParametersView;
import org.hibernate.eclipse.hqleditor.HQLCompletionProcessor;
import org.hibernate.eclipse.hqleditor.HQLEditor;
import org.jboss.tools.hibernate.orm.test.utils.ConsoleConfigUtils;
import org.jboss.tools.hibernate.orm.test.utils.TestConsoleConfigurationPreferences;
import org.jboss.tools.hibernate.orm.test.utils.project.SimpleTestProject;
import org.jboss.tools.hibernate.orm.test.utils.project.SimpleTestProjectWithMapping;
import org.jboss.tools.hibernate.orm.test.utils.project.TestProject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Dmitry Geraskov
 *
 */
public class HQLEditorTest {
	
	private static final String HIBERNATE_CFG_XML = 
			"<!DOCTYPE hibernate-configuration PUBLIC                               " +
			"	'-//Hibernate/Hibernate Configuration DTD 3.0//EN'                  " +
			"	'http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd'> " +
			"                                                                       " +
			"<hibernate-configuration>                                              " +
			"	<session-factory/>                                                  " + 
			"</hibernate-configuration>                                             " ;		
		
	private static final String PROJ_NAME = "HQLEditorTest"; //$NON-NLS-1$
	private static final String CONSOLE_NAME = PROJ_NAME;
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
		
	private File cfgXmlFile = null;
	private SimpleTestProjectWithMapping project = null;	
	private TestConsoleConfigurationPreferences consolePrefs;
	private ConsoleConfiguration consoleConfiguration;
	
	@Before
	public void setUp() throws Exception {
		cfgXmlFile = new File(temporaryFolder.getRoot(), "hibernate.cfg.xml");
		FileWriter fw = new FileWriter(cfgXmlFile);
		fw.write(HIBERNATE_CFG_XML);
		fw.close();
		consolePrefs = new TestConsoleConfigurationPreferences(cfgXmlFile);
		consoleConfiguration = new ConsoleConfiguration(consolePrefs);
		KnownConfigurations.getInstance().addConfiguration(consoleConfiguration, false);
	}

	@After
	public void tearDown() throws Exception {
		consolePrefs = null;
		consoleConfiguration = null;
		KnownConfigurations.getInstance().removeAllConfigurations();
		cleanUpProject();
	}
	
	@Test
	public void testHQLEditorOpen(){
		IEditorPart editorPart = HibernateConsolePlugin.getDefault()
			.openScratchHQLEditor(consoleConfiguration.getName(), ""); //$NON-NLS-1$
		Assert.assertNotNull("Editor was not opened", editorPart); //$NON-NLS-1$
		Assert.assertTrue("Opened editor is not HQLEditor", editorPart instanceof HQLEditor); //$NON-NLS-1$
		
		HQLEditor editor = (HQLEditor)editorPart;		
		QueryInputModel model = editor.getQueryInputModel();
		Assert.assertNotNull("Model is NULL", model); //$NON-NLS-1$
	}
	
	@Test
	public void testSingleLineCommentsCutOff() throws PartInitException{
		String query = "from pack.Article a\n" + //$NON-NLS-1$
				"where a.articleid in (:a, :b) --or a.articleid = :c"; //$NON-NLS-1$
		IEditorPart editorPart = HibernateConsolePlugin.getDefault()
			.openScratchHQLEditor(consoleConfiguration.getName(), query);
		Assert.assertTrue("Opened editor is not HQLEditor", editorPart instanceof HQLEditor); //$NON-NLS-1$
		
		HQLEditor editor = (HQLEditor)editorPart;
		Assert.assertEquals(editor.getEditorText(), query);
		Assert.assertFalse("Comments were not cut off", editor.getQueryString().contains("--")); //$NON-NLS-1$ //$NON-NLS-2$
		
		QueryInputModel model = editor.getQueryInputModel();
		Assert.assertTrue(model.getParameterCount() == 0);
		
		IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			.getActivePage().showView("org.hibernate.eclipse.console.views.QueryParametersView"); //$NON-NLS-1$
		Assert.assertNotNull("View was not opened", view); //$NON-NLS-1$
		Assert.assertTrue("Opened view is not QueryParametersView", view instanceof QueryParametersView); //$NON-NLS-1$
		
		QueryParametersView paramView = (QueryParametersView)view;
		IPage ipage = paramView.getCurrentPage();
		Assert.assertNotNull("Current Page is NULL", ipage); //$NON-NLS-1$
		Assert.assertTrue("Page is not Query Parameters Page", ipage instanceof QueryParametersPage); //$NON-NLS-1$
		
		QueryParametersPage page = (QueryParametersPage)ipage;
		IToolBarManager manager = page.getSite().getActionBars().getToolBarManager();
		IContributionItem[] items = manager.getItems();
		ActionContributionItem addParamItem = null;
		for (int i = 0; i < items.length; i++) {
			ActionContributionItem item = (ActionContributionItem) items[i];
			if (item.getAction().getClass().getName().endsWith("NewRowAction")){ //$NON-NLS-1$
				addParamItem = item;
				break;
			}
		}
		Assert.assertNotNull(HibernateConsoleMessages.QueryParametersPage_add_query_parameter_tooltip
				+ " item not found", addParamItem); //$NON-NLS-1$
		
		addParamItem.getAction().run();//add query parameters automatically
		Assert.assertTrue(model.getParameterCount() == 2);//a and b
		
	}

	@Test
	public void testHQLEditorCodeCompletionWithTabs() throws CoreException, NoSuchFieldException, IllegalAccessException{
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
		Assert.assertNotNull("Console Configuration not found", cc); //$NON-NLS-1$
		cc.build();

		final String codeCompletionPlaceMarker = " from "; //$NON-NLS-1$
		final String query = "select\t \tt1." + codeCompletionPlaceMarker +  //$NON-NLS-1$
			project.getFullyQualifiedTestClassName() + " t1"; //$NON-NLS-1$
		IEditorPart editorPart = HibernateConsolePlugin.getDefault()
			.openScratchHQLEditor(CONSOLE_NAME, query);
		Assert.assertTrue("Opened editor is not HQLEditor", editorPart instanceof HQLEditor); //$NON-NLS-1$
		
		HQLEditor editor = (HQLEditor)editorPart;
		Assert.assertEquals(editor.getEditorText(), query);
		
		QueryInputModel model = editor.getQueryInputModel();
		Assert.assertTrue(model.getParameterCount() == 0);
		
		editor.setConsoleConfigurationName(CONSOLE_NAME);
		IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		
		HQLCompletionProcessor processor = new HQLCompletionProcessor(editor);
		
		int position = query.indexOf(codeCompletionPlaceMarker);
		ICompletionProposal[] proposals = processor.computeCompletionProposals(doc, position);
		Assert.assertTrue(proposals.length > 0);
		cc.reset();
	}
	
	private void cleanUpProject() {
		if (project != null) {
			project.deleteIProject();
			project = null;
		}
	}

}
