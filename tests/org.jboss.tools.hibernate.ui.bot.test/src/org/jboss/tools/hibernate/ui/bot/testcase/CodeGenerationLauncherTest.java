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

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.hibernate.ui.bot.testsuite.HibernateTest;
import org.jboss.tools.hibernate.ui.bot.testsuite.Project;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.PerspectiveType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class CodeGenerationLauncherTest extends HibernateTest {

	SWTBotShell mainShell = null;
	
	@BeforeClass
	public static void setUp() {

		prepareProject();
		prepareConsole();
	}
	
	/**
	 * Run code generation code
	 */
	@Test
	public void generate() {
		eclipse.openPerspective(PerspectiveType.HIBERNATE);
		
		createNewHibernateCodeGenerationConfiguration();
		
		fillMainTab();		
		fillExportersTab();
		fillRefreshTab();
		fillCommonTab();		
			
		bot.button(IDELabel.Button.RUN);
	}
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
	}

	/**
	 * TC 10
	 */
	public void fillExportersTab() {
		mainShell.activate();
		bot.cTabItem(IDELabel.HBLaunchConfigurationDialog.EXPORTERS_TAB).activate();
		bot.table().select("Domain code (.java)");
		bot.table().getTableItem(0).check();		
	}

	/**
	 * TC 11
	 */
	public void fillRefreshTab() {
		mainShell.activate();
		bot.cTabItem(IDELabel.HBLaunchConfigurationDialog.REFRESH_TAB).activate();

	}

	/**
	 * TC 12
	 */
	public void fillCommonTab() {
		mainShell.activate();
		bot.cTabItem(IDELabel.HBLaunchConfigurationDialog.COMMON_TAB).activate();
	}

}
