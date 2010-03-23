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
import org.jboss.tools.hibernate.ui.bot.testsuite.HibernateTest;
import org.jboss.tools.hibernate.ui.bot.testsuite.Project;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.PerspectiveType;
import org.jboss.tools.ui.bot.ext.view.ProjectExplorer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class DaliTest extends HibernateTest {
	
	static boolean projectCreated = false;
	
	@BeforeClass
	public static void prepare() {
		bot.viewByTitle("Welcome").close();		
		eclipse.openPerspective(PerspectiveType.JPA);
		util.waitForNonIgnoredJobs();
	}
	
	/**
	 * TC 22
	 */
	@Test
	public void createJPAProject() {
		if (projectCreated) return;
		
		EntityType type = EntityType.JPA_PROJECT;		
		eclipse.createNew(type);
		
		// JPA Project Page
		eclipse.waitForShell("New JPA Project");
		bot.textWithLabel("Project name:").setText(Project.JPA_PRJ_NAME);
		bot.button(IDELabel.Button.NEXT).click();
		
		// Java Page
		bot.button(IDELabel.Button.NEXT).click();
		
		// JPA Facet Page		
		bot.comboBoxInGroup("Platform").setSelection("Hibernate");

		// Finish
		bot.button(IDELabel.Button.FINISH).click();
		eclipse.waitForClosedShell(bot.shell("New JPA Project"));
		util.waitForNonIgnoredJobs();
		
		projectCreated = true;
	}

	
	@Test 
	public void openPersitenceXML() {
		ProjectExplorer explorer = new ProjectExplorer();
		explorer.openFile(Project.JPA_PRJ_NAME, "JPA Content", "persistence.xml");
	}
	
	/**
	 * TC 24
	 */
	@Test
	public void generateDDL() {
		

	}

	/**
	 * TC 24
	 */
	@Test
	public void generateEntities() {

	}

	/**
	 * TC 23
	 */
	@Test
	public void checkJPAPerspective() {

	}

	/**
	 * TC 25
	 */
	@Test
	public void checkCAInConfigurationEditor() {

	}

	/**
	 * TC 25
	 */
	@Test
	public void checkCAInMappingEditor() {

	}
	
	public static boolean isPRojectCreated() {
		if (projectCreated) {
			log.info("JPA Project is already created");
		}
		return projectCreated;
	}

	@AfterClass
	public static void cleanup() {
		
	}
	
}
