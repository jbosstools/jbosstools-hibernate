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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.hibernate.ui.bot.testsuite.HibernateTest;
import org.jboss.tools.hibernate.ui.bot.testsuite.Project;
import org.jboss.tools.ui.bot.ext.config.Annotations.DB;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.BeforeClass;
import org.junit.Test;

@Require( db=@DB, perspective="Hibernate")
public class MappingsDiagramTest extends HibernateTest {

	@BeforeClass
	/**
	 * Setup prerequisites for this test
	 */
	public static void setUpTest() {

		eclipse.maximizeActiveShell();
		eclipse.closeView(IDELabel.View.WELCOME);
		
		prepareProject();
		prepareConsole();

		util.waitForNonIgnoredJobs();
	}
	
	/**
	 * Test mapping diagram
	 */
	@Test
	public void testDiagram() {
		
		CodeGenerationLauncherTest test = new CodeGenerationLauncherTest();
		test.generate();
		
		SWTBot viewBot = open.viewOpen(ActionItem.View.HibernateHibernateConfigurations.LABEL).bot();	
		SWTBotTreeItem console = viewBot.tree().expandNode(Project.PROJECT_NAME);
		bot.sleep(TIME_1S);	
		
		console.contextMenu("Mapping Diagram").click();
	}

}
