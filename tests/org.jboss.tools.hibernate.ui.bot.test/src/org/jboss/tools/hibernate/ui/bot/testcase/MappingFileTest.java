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

import java.util.List;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.hibernate.ui.bot.testsuite.HibernateTest;
import org.jboss.tools.hibernate.ui.bot.testsuite.Project;
import org.jboss.tools.ui.bot.ext.parts.ObjectMultiPageEditorBot;
import org.jboss.tools.ui.bot.ext.parts.SWTBotEditorExt;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class MappingFileTest extends HibernateTest {

	@BeforeClass
	public static void setUpTest() {
		HibernateTest.prepare();
	}
	
	@AfterClass
	public static void tearDownTest() { 
		HibernateTest.clean();
	}

	/**
	 * TC 01 - Create Hibernate Mapping file (when package is selected)
	 * Create new .hbm.xml
	 * ASSERT: No exception
	 * Select Class to map 
	 * ASSERT: .hbm.xml must appears in JBoss Editor
	 * Note: 2 Way check Created from package and classes
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
		bot.viewByTitle(IDELabel.View.PACKAGE_EXPLORER).show();
		bot.viewByTitle(IDELabel.View.PACKAGE_EXPLORER).setFocus();
		SWTBotTreeItem item = viewBot.tree().expandNode(Project.PROJECT_NAME).expandNode("src");	
		item = item.expandNode(Project.PACKAGE_NAME).select();
		
		// Create mapping files 
		eclipse.createNew(EntityType.HIBERNATE_MAPPING_FILE);
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
		// Open Hibernate Mapping File (ObjectMultiPageEditor on source tab)
		eclipse.openFile(Project.PROJECT_NAME,"src",Project.PACKAGE_NAME,Project.CLASS1 + ".hbm.xml");
		ObjectMultiPageEditorBot pageBot = new ObjectMultiPageEditorBot(Project.CLASS1 + ".hbm.xml");
		pageBot.selectPage("Source");
		
		// Check code completion
		SWTBotEditorExt editor = bot.swtBotEditorExtByTitle(Project.CLASS1 + ".hbm.xml");
		
		String search = "</id>";  
		List<String> lines = editor.getLines();
		
		int index = 0;
		for (String line : lines ) {
			index++;
			if (line.trim().equals(search)) break;
		}

		log.info("Line index: " + index);

		// Insert tag for cc check
		String newLine = "<property name=\"\"> ";
		int col = newLine.indexOf("\"\"");
		editor.selectRange(index, 0, 0);
		editor.insertText("\n");
		editor.insertText(newLine);
		editor.selectRange(index, col + 1, 0);
		
		// TODO autocomplete proposal check
		
		editor.save();		
	}
}
