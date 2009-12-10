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

import org.eclipse.swt.widgets.Button;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.hamcrest.Matcher;
import org.jboss.tools.hibernate.ui.bot.testsuite.HibernateTest;
import org.jboss.tools.hibernate.ui.bot.testsuite.Project;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.PerspectiveType;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class CodeGenerationLauncherTest extends HibernateTest {

	SWTBotShell mainShell = null;
	public static boolean generationDone = false;
	
	@BeforeClass
	/**
	 * Setup prerequisites for this test
	 */
	public static void setUp() {

		prepareProject();
		prepareConsole();
		prepareDatabase();
	}
	
	/**
	 * Run code generation code
	 */
	@Test
	public void generate() {
		if (generationDone) return;
		
		log.info("HB Code Generation STARTED");
		eclipse.openPerspective(PerspectiveType.HIBERNATE);
		
		createNewHibernateCodeGenerationConfiguration();
		
		fillMainTab();		
		fillExportersTab();
		fillRefreshTab();
		fillCommonTab();		
				
		bot.button(IDELabel.Button.RUN).click();	
		log.info("HB Code Generation FINISHED");
		util.waitForNonIgnoredJobs();
		
		checkGeneratedFiles();
		
		generationDone = true;			
	}
	/**
	 * Checks existence generated files after code generation 
	 */
	private void checkGeneratedFiles() {
	
		SWTBot viewBot = eclipse.showView(ViewType.PROJECT_EXPLORER);
		SWTBotTreeItem item;

		item = eclipse.selectTreeLocation(viewBot, Project.PROJECT_NAME,"gen","org","test","Customers.java");
		item.doubleClick();
		item = eclipse.selectTreeLocation(viewBot, Project.PROJECT_NAME,"gen","org","test","Employees.java");
		item.doubleClick();
		item = eclipse.selectTreeLocation(viewBot, Project.PROJECT_NAME,"gen","org","test","Offices.java");
		item.doubleClick();			
		
		log.info("Generated files check DONE");
		bot.sleep(TIME_10S);
	}

	/**
	 * 
	 */
	private void createNewHibernateCodeGenerationConfiguration() {
		SWTBotMenu menu = null;
		menu = bot.menu("Run");
		menu = menu.menu(IDELabel.Menu.HIBERNATE_CODE_GENERATION);
		menu = menu.menu(IDELabel.Menu.HIBERNATE_CODE_GENERATION_CONF).click();
		
		mainShell = bot.activeShell();
	}

	/**
	 * TC 09
	 */
	public void fillMainTab() {		
		
		bot.tree().expandNode("Hibernate Code Generation").select();
		bot.toolbarButtonWithTooltip("New launch configuration").click();
		
		eclipse.selectTreeLocation("Hibernate Code Generation","New_configuration");
		bot.textWithLabel("Name:").setText("HSQL Configuration");
		
		// Console Configuration
		bot.comboBoxWithLabel("Console configuration:").setSelection(Project.PROJECT_NAME);
		
		// Output directory
		bot.button("Browse...").click();
		bot.shell("Select output directory").activate();
		eclipse.selectTreeLocation(Project.PROJECT_NAME);
		bot.button("Create New Folder...").click();
		bot.shell("New Folder").activate();
		bot.textWithLabel("Folder name:").setText("gen");
		bot.button(IDELabel.Button.OK).click();
		eclipse.selectTreeLocation(Project.PROJECT_NAME,"gen");
		bot.button(IDELabel.Button.OK).click();
		
		// Create console configuration
		Matcher<Button> matcher = WidgetMatcherFactory.withText("Reverse engineer from JDBC Connection");
		Button button = bot.widget(matcher);		
		SWTBotCheckBox cb = new SWTBotCheckBox(button); 
				
		if (!cb.isChecked())
			cb.click();
		
		bot.textWithLabel("Package:").setText("org.test");
		log.info("HB Code Generation Main tab DONE");
		bot.sleep(TIME_1S);
	}

	/**
	 * TC 10
	 */
	public void fillExportersTab() {
		mainShell.activate();
		bot.cTabItem(IDELabel.HBLaunchConfigurationDialog.EXPORTERS_TAB).activate();
		bot.table().select("Domain code (.java)");
		bot.table().getTableItem(0).check();
		log.info("HB Code Generation Exporters tab DONE");
		bot.sleep(TIME_1S);
	}

	/**
	 * TC 11
	 */
	public void fillRefreshTab() {
		mainShell.activate();
		bot.cTabItem(IDELabel.HBLaunchConfigurationDialog.REFRESH_TAB).activate();
		log.info("HB Code Generation Refresh tab DONE");
		bot.sleep(TIME_1S);
	}

	/**
	 * TC 12
	 */
	public void fillCommonTab() {
		mainShell.activate();
		bot.cTabItem(IDELabel.HBLaunchConfigurationDialog.COMMON_TAB).activate();
		log.info("HB Code Generation Common tab DONE");
		bot.sleep(TIME_1S);
	}
}
