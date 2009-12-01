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
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.hamcrest.Matcher;
import org.jboss.tools.hibernate.ui.bot.testsuite.HibernateTest;
import org.jboss.tools.hibernate.ui.bot.testsuite.Project;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ConfigurationFileTest extends HibernateTest {

	@BeforeClass
	public static void setUp() {
		HibernateTest.createProject();
	}
	
	@AfterClass
	public static void tearDown() { 
		HibernateTest.clean();
	}	
	
	/**
	 * TC 02 - Create Hibernate Configuration file with predefined variables
	 */
	@Test
	public void createFile() {
		eclipse.createNew(EntityType.HIBERNATE_CONFIGURATION_FILE);
		
		eclipse.selectTreeLocation(Project.PROJECT_NAME,"src");
		bot.textWithLabel(IDELabel.HBConfigurationWizard.FILE_NAME).setText(Project.CONF_FILE_NAME2);
		bot.button(IDELabel.Button.NEXT).click();

		// Create new configuration file
		bot.comboBoxWithLabel(IDELabel.HBConsoleWizard.DATABASE_DIALECT).setSelection(Project.DB_DIALECT);
		bot.comboBoxWithLabel(IDELabel.HBConsoleWizard.DRIVER_CLASS).setSelection(Project.DRIVER_CLASS);
		bot.comboBoxWithLabel(IDELabel.HBConsoleWizard.CONNECTION_URL).setText(Project.JDBC_STRING);
		
		// Create console configuration
		Matcher<Button> matcher = WidgetMatcherFactory.withText(IDELabel.HBConsoleWizard.CREATE_CONSOLE_CONFIGURATION);
		Button button = bot.widget(matcher);		
		SWTBotCheckBox cb = new SWTBotCheckBox(button); 
				
		if (!cb.isChecked())
			cb.click();
		
		SWTBotShell shell = bot.activeShell();
		bot.button(IDELabel.Button.FINISH).click();
		eclipse.waitForClosedShell(shell);				
	}

	/**
	 * TC 13
	 */
	@Test
	public void editFile() {
		// TODO
	}

}
