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

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.hibernate.ui.bot.testsuite.HibernateTest;
import org.jboss.tools.hibernate.ui.bot.testsuite.Project;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.PerspectiveType;
import org.jboss.tools.ui.bot.ext.view.ProjectExplorer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Version;

@RunWith(SWTBotJunit4ClassRunner.class)
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
		bot.button(IDELabel.Button.NEXT).click();

		// Java Page
		bot.button(IDELabel.Button.NEXT).click();

		// JPA Facet Page
		Version version = jbt.getJBTVersion();
		if ((version.getMajor() == 3) && (version.getMinor() == 1))		
			bot.comboBoxInGroup("Platform").setSelection("Hibernate");
		else
			bot.comboBoxInGroup("Platform").setSelection("Hibernate (JPA 2.x)");
		
		bot.comboBoxInGroup("JPA implementation").setSelection("Disable Library Configuration");

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

		// TODO
		// Check file
		// packageExplorer.openFile(Project.JPA_PRJ_NAME, Project.DDL_OUTPUT, Project.DDL_FILENAME);
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
	 * TC 25
	 */
	@Test
	public void checkCAInConfigurationEditor() {
		bot.editorByTitle("persistence.xml").show();

		// TODO - Multipage editor bot support needed first
	}

	/**
	 * TC 25
	 */
	@Test
	public void checkCAInMappingEditor() {
		// TODO - Multipage editor bot support needed first

	}

	@AfterClass
	public static void cleanup() {
		log.info("JPA DaliTest cleanup");
		bot.sleep(TIME_5S);
	}
}
