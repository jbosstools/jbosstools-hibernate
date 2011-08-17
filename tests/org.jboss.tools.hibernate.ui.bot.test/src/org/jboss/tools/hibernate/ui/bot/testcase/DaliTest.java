/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.bot.testcase;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotMultiPageEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.hibernate.ui.bot.testsuite.HibernateTest;
import org.jboss.tools.hibernate.ui.bot.testsuite.Project;
import org.jboss.tools.ui.bot.ext.config.Annotations.DB;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.config.TestConfigurator;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.helper.DatabaseHelper;
import org.jboss.tools.ui.bot.ext.helper.StringHelper;
import org.jboss.tools.ui.bot.ext.parts.ContentAssistBot;
import org.jboss.tools.ui.bot.ext.parts.SWTBotEditorExt;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.PerspectiveType;
import org.jboss.tools.ui.bot.ext.view.ProjectExplorer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Version;

@Require( clearProjects = false,  db=@DB, perspective="JPA" , server=@Server(state = ServerState.Present))
public class DaliTest extends HibernateTest {

	private static boolean projectCreated = false;

	@BeforeClass
	public static void prepare() {
		eclipse.closeView(IDELabel.View.WELCOME);
		eclipse.openPerspective(PerspectiveType.JPA);
		util.waitForNonIgnoredJobs();
	}
	
	/**
	 * TC 22 - Test creates JPA Project
	 */
	@Test
	public void createJPAProject() {
		if (projectCreated)
			return;

		EntityType type = EntityType.JPA_PROJECT;
		eclipse.createNew(type);

		// JPA Project Page
		eclipse.waitForShell("New JPA Project");
		bot.textWithLabel("Project name:").setText(Project.JPA_PRJ_NAME); 
		bot.comboBoxInGroup("Target runtime").setSelection(TestConfigurator.currentConfig.getServer().getName());
		
		
		bot.button(IDELabel.Button.NEXT).click();

		// Java Page
		bot.button(IDELabel.Button.NEXT).click();

		// JPA Facet Page
		Version version = jbt.getJBTVersion();
		if ((version.getMajor() == 3) && (version.getMinor() == 1))		
			bot.comboBoxInGroup("Platform").setSelection("Hibernate");
		else
			bot.comboBoxInGroup("Platform").setSelection("Hibernate (JPA 2.x)");
		
		bot.comboBoxInGroup("JPA implementation").setSelection("Library Provided by Target Runtime");		
		bot.comboBoxInGroup("Connection").setSelection(TestConfigurator.currentConfig.getDB().name);

		// Finish
		bot.button(IDELabel.Button.FINISH).click();
		eclipse.waitForClosedShell(bot.shell("New JPA Project"));
		util.waitForNonIgnoredJobs();

		projectCreated = true;
	}

	/**
	 * Test open persistence.xml of JPA project
	 */
	@Test
	public void openPersitenceXML() {
		ProjectExplorer explorer = new ProjectExplorer();
		explorer.openFile(Project.JPA_PRJ_NAME, "JPA Content",
				"persistence.xml");
	}

	/**
	 * TC 24 - Test generates DDL file
	 */
	@Test
	public void generateDDL() {
		// Select project
		SWTBotView viewBot = bot.viewByTitle(IDELabel.View.PROJECT_EXPLORER);
		SWTBotTree tree = viewBot.bot().tree().select(Project.JPA_PRJ_NAME);

		// JPA Tools -> Generate Tables From Entities
		ContextMenuHelper.clickContextMenu(tree, "JPA Tools",
				"Generate Tables from Entities...");

		// DDL Generation Dialog
		String outputDir = Project.JPA_PRJ_NAME + "/" + Project.DDL_OUTPUT;
		bot.textWithLabel("Output directory:").setText(outputDir);
		bot.textWithLabel("File name").setText(Project.DDL_OUTPUT);

		bot.button(IDELabel.Button.FINISH).click();
	}

	/**
	 * TC 24 - Test for generating Entities from Tables
	 */
	@Test
	public void generateEntities() {
		// Select project
		SWTBotView viewBot = bot.viewByTitle(IDELabel.View.PROJECT_EXPLORER);
		SWTBotTree tree = viewBot.bot().tree().select(Project.JPA_PRJ_NAME);

		// JPA Tools -> Generate Tables From Entities
		ContextMenuHelper.clickContextMenu(tree, "JPA Tools",
				"Generate Entities from Tables...");

		// Generation Entities dialog
		bot.textWithLabel("Package:").setText(Project.ENTITIES_PACKAGE);
		bot.button(IDELabel.Button.FINISH).click();
		util.waitForNonIgnoredJobs();
		
		// Check generated entities
		packageExplorer.openFile(Project.JPA_PRJ_NAME, "src", Project.ENTITIES_PACKAGE, "Customers.java");
	}

	/**
	 * TC 23 - Check JPA perspective views
	 */
	@Test
	public void checkJPAPerspective() {
		bot.viewByTitle("JPA Structure").setFocus();
		bot.viewByTitle("JPA Details").setFocus();
		bot.viewByTitle("Data Source Explorer").setFocus();
	}

	
	/**
	 * TC 25 - Checking coding configuration in persistence.xml editor on xml page 
	 */
	@Test
	public void checkCAInConfigurationEditorXML() {
		SWTBotEditor editor = 	bot.editorByTitle("persistence.xml");
		editor.show();
		SWTBotMultiPageEditor mpe = new SWTBotMultiPageEditor(editor.getReference(), bot);
		mpe.activatePage("Source");
		
		// Code completion
		String text = mpe.toTextEditor().getText();
		StringHelper helper = new StringHelper(text);
		Point p = helper.getPositionBefore("</persistence-unit>");
		editor.toTextEditor().selectRange(p.y, p.x, 0);
		editor.save();
		SWTBotEditorExt editorExt = new SWTBotEditorExt(editor.getReference(), bot);
		ContentAssistBot ca = new ContentAssistBot(editorExt);
		ca.useProposal("class");	
	}


	/**
	 * TC 25 - Filling hibernate page on persistence.xml editor
	 */
	@Test
	public void fillHibernatePage() {
		SWTBotEditor editor = 	bot.editorByTitle("persistence.xml");
		editor.show();
		SWTBotMultiPageEditor mpe = new SWTBotMultiPageEditor(editor.getReference(), bot);
		mpe.activatePage("Hibernate");

		// Fill in 
		String dialect = DatabaseHelper.getDialect(TestConfigurator.currentConfig.getDB().dbType);
		bot.comboBoxWithLabel(IDELabel.HBConsoleWizard.DATABASE_DIALECT).setSelection(dialect);
		String drvClass = DatabaseHelper.getDriverClass(TestConfigurator.currentConfig.getDB().dbType);
		bot.comboBoxWithLabel(IDELabel.HBConsoleWizard.DRIVER_CLASS).setSelection(drvClass);		
		String jdbc = TestConfigurator.currentConfig.getDB().jdbcString;
		bot.comboBoxWithLabel(IDELabel.HBConsoleWizard.CONNECTION_URL).setText(jdbc);
		bot.textWithLabel("Username:").setText("sa");
 
		editor.save();
		mpe.activatePage("Source");

		// Check xml content
		String text = mpe.toTextEditor().getText();
		StringHelper helper = new StringHelper(text);		
		String str  =  "<property name=\"hibernate.dialect\" value=\"org.hibernate.dialect.HSQLDialect\"/>";
		helper.getPositionBefore(str);
		str  =  "<property name=\"hibernate.connection.driver_class\" value=\"org.hsqldb.jdbcDriver\"/>";
		helper.getPositionBefore(str);
		bot.sleep(TIME_10S);
	}
		
	@AfterClass
	public static void cleanup() {
		log.info("JPA DaliTest cleanup");
	}
}
