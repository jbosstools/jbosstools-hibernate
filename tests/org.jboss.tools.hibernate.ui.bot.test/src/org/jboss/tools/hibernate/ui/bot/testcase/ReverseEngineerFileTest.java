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

import org.jboss.tools.hibernate.ui.bot.testsuite.HibernateTest;
import org.jboss.tools.hibernate.ui.bot.testsuite.Project;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@Require(clearProjects = false,  perspective="Hibernate")
public class ReverseEngineerFileTest extends HibernateTest {

	@BeforeClass
	public static void prepare() {
		prepareProject();
	}
	/**
	 * TC 08
	 */
	@Test
	public void createFile() {
		
		bot.viewByTitle(IDELabel.View.PACKAGE_EXPLORER).setFocus();
		packageExplorer.selectProject(Project.PROJECT_NAME);
		
		// Create reveng file
		eclipse.createNew(EntityType.HIBERNATE_REVERSE_FILE);
		bot.clickButton(IDELabel.Button.NEXT);
		bot.clickButton("Include...");
		bot.clickButton(IDELabel.Button.FINISH);				
	}

	/**
	 * TC 15
	 */
	@Test
	public void editFile() {
		// TBD
	}
	
	@AfterClass
	public static void finish() {
	}
}
