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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.hibernate.ui.bot.testsuite.HibernateTest;
import org.jboss.tools.hibernate.ui.bot.testsuite.Project;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class MappingFileTest extends HibernateTest {

	@BeforeClass
	public static void setUp() {
		HibernateTest.prepare();
	}
	
	@AfterClass
	public static void tearDown() { 
		HibernateTest.clean();
	}

	/**
	 * TC 01 - Create Hibernate Mapping file (when package is selected)
	 */
	@Test
	public void createFile() {	
		createFilesFromPackage();
		
		// Remove files generated in the first round
		eclipse.removeFile(Project.PROJECT_NAME,"src",Project.PACKAGE_NAME,Project.CLASS1 + ".hbm.xml");
		eclipse.removeFile(Project.PROJECT_NAME,"src",Project.PACKAGE_NAME,Project.CLASS2 + ".hbm.xml");
				
		createFilesFromClasses();
	}

	/**
	 * Create Hibernate Mapping file from selected classes
	 */
	private void createFilesFromClasses() {
		// Select Both classes
		SWTBot viewBot = bot.viewByTitle(IDELabel.View.PACKAGE_EXPLORER).bot();
		SWTBotTreeItem item = viewBot.tree().expandNode(Project.PROJECT_NAME).expandNode("src");	
		item = item.expandNode(Project.PACKAGE_NAME).select();
		item.select(Project.CLASS1+".java",Project.CLASS2+".java");			
		
		// Create mapping files 
		eclipse.createNew(EntityType.HIBERNATE_MAPPING_FILE);
		eclipse.waitForShell(IDELabel.Shell.NEW_HIBERNATE_MAPPING_FILE);
		bot.button(IDELabel.Button.NEXT).click();
		bot.button(IDELabel.Button.FINISH).click();
		util.waitForNonIgnoredJobs();
		
		// Check if new mapping files exists
		eclipse.openFile(Project.PROJECT_NAME,"src",Project.PACKAGE_NAME,Project.CLASS1 + ".hbm.xml" );
		eclipse.openFile(Project.PROJECT_NAME,"src",Project.PACKAGE_NAME,Project.CLASS2 + ".hbm.xml" );
	}
	
	/**
	 * Create Hibernate mapping files from selected package
	 */
	private void createFilesFromPackage() {
		// Select Package file
		SWTBot viewBot = bot.viewByTitle(IDELabel.View.PACKAGE_EXPLORER).bot();
		SWTBotTreeItem item = viewBot.tree().expandNode(Project.PROJECT_NAME).expandNode("src");	
		item = item.expandNode(Project.PACKAGE_NAME).select();
		
		// Create mapping files 
		eclipse.createNew(EntityType.HIBERNATE_MAPPING_FILE);
		eclipse.waitForShell(IDELabel.Shell.NEW_HIBERNATE_MAPPING_FILE);
		bot.button(IDELabel.Button.FINISH).click();
		util.waitForNonIgnoredJobs();
				
		// Check if new mapping files exists
		eclipse.openFile(Project.PROJECT_NAME,"src",Project.PACKAGE_NAME,Project.CLASS1 + ".hbm.xml" );
		eclipse.openFile(Project.PROJECT_NAME,"src",Project.PACKAGE_NAME,Project.CLASS2 + ".hbm.xml" );
}
	
	/**
	 * TC 14 - Editing Hibernate Mapping file
	 */
	@Test
	public void editFile() {
	}
}
