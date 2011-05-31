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
package org.jboss.tools.hibernate.ui.bot.testsuite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.hibernate.ui.bot.testcase.Activator;
import org.jboss.tools.hibernate.ui.bot.testcase.ConsoleTest;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.config.TestConfigurator;
import org.jboss.tools.ui.bot.ext.entity.JavaClassEntity;
import org.jboss.tools.ui.bot.ext.entity.JavaProjectEntity;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.PerspectiveType;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.junit.BeforeClass;

public class HibernateTest extends SWTTestExt {

	private static boolean classesCreated = false;
	private static boolean projectCreated = false;
	
	/**
	 * Prepare project and classes
	 */
	@BeforeClass	
	public static void prepare() {	
		log.info("Hibernate All Test Started");
		util.waitForNonIgnoredJobs();
	}
	
	/**
	 * Create testing classes for the project
	 */
	public static void prepareClasses() {
		
		if (classesCreated) return; 

		// Package Explorer
		SWTBot viewBot = eclipse.showView(ViewType.PACKAGE_EXPLORER);
		SWTEclipseExt.selectTreeLocation(viewBot, Project.PROJECT_NAME,"src");

		// Class 1
		JavaClassEntity classEntity = new JavaClassEntity();
		classEntity.setClassName(Project.CLASS1);
		classEntity.setPackageName(Project.PACKAGE_NAME);
		eclipse.createJavaClass(classEntity);
		eclipse.setClassContentFromResource(true, Activator.PLUGIN_ID, "src", Project.PACKAGE_NAME, Project.CLASS1+".java");
		
		// Class 2		
		classEntity.setClassName(Project.CLASS2);
		classEntity.setPackageName(Project.PACKAGE_NAME);
		eclipse.createJavaClass(classEntity);
		eclipse.setClassContentFromResource(true, Activator.PLUGIN_ID, "src", Project.PACKAGE_NAME, Project.CLASS2+".java");

		classesCreated = true;		
	}
	
	/**
	 * Create Java project for testing hibernate features
	 */
	public static void prepareProject() {
		
		if (projectCreated) return;
		
		eclipse.openPerspective(PerspectiveType.JAVA);
		eclipse.showView(ViewType.PACKAGE_EXPLORER);
		bot.activeView();
	
		System.out.println("View Activated");
		
		// Show perspective and view
		eclipse.closeView(IDELabel.View.WELCOME);
		eclipse.openPerspective(PerspectiveType.JAVA);

		// Create needed project
		// Prepare entity
		JavaProjectEntity projectEntity = new JavaProjectEntity();
		projectEntity.setProjectName(Project.PROJECT_NAME);
		eclipse.createJavaProject(projectEntity);
		
		// add HSQLDB Driver
		addDriver();
		
		projectCreated = true;
	}
	
	/**
	 * Copy driver into project and add to classpath
	 */
	public static void addDriver() {
		try {
			addDriverIntoProject();
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		addDriverClassPath();		
	}

	/**
	 * Add Driver to classpath
	 */
	public static void addDriverClassPath() {
			
		eclipse.showView(ViewType.PROJECT_EXPLORER);
		SWTBotView view = bot.viewByTitle("Project Explorer");
		view.show();
		view.setFocus();
		SWTBot packageExplorerBot = view.bot();		
		SWTBotTree tree = packageExplorerBot.tree();

		// Bot workaround for Bot menu
		ContextMenuHelper.prepareTreeItemForContextMenu(tree);	    
	    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,"Refresh",false)).click();

		ContextMenuHelper.prepareTreeItemForContextMenu(tree);	    
	    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,"Properties",false)).click();
	    
	    // Set build path
	    bot.tree().expandNode("Java Build Path").select();
	    bot.tabItem("Libraries").activate();
	    bot.button("Add JARs...").click();
	    bot.sleep(TIME_500MS);
	    String file = getDriverFileName(TestConfigurator.currentConfig.getDB().driverPath);
	    bot.tree().expandNode(Project.PROJECT_NAME).expandNode(file).select();
	    
	    bot.button(IDELabel.Button.OK).click();
	    bot.sleep(TIME_1S);
	    bot.button(IDELabel.Button.OK).click();
	    bot.sleep(TIME_1S);
	}	
	
	public static String getDriverFileName(String filePath) {
		String[] fragments = filePath.split(File.separator);
		return fragments[fragments.length - 1];
	}
	
	/**
	 * Add HSQLDB driver into project
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void addDriverIntoProject() throws FileNotFoundException, IOException {
		File in = null;
		if (TestConfigurator.currentConfig.getDB().internal) {		
			in = util.getResourceFile(Activator.PLUGIN_ID, "drv","hsqldb.jar");
		}
		else {
			in = new File(TestConfigurator.currentConfig.getDB().driverPath);
		}
		
		File out = new File(Platform.getLocation() + File.separator + Project.PROJECT_NAME + File.separator + "hsqldb.jar");
		
        FileChannel inChannel = null;
        FileChannel outChannel = null;

		inChannel = new FileInputStream(in).getChannel();
		outChannel = new FileOutputStream(out).getChannel();

    	inChannel.transferTo(0, inChannel.size(),	outChannel);

    	if (inChannel != null) inChannel.close();
    	if (outChannel != null) outChannel.close();
    	log.info("JDBC Driver copied");
	}

	/**
	 * Clean after tests
	 */
	public static void clean() {
		log.info("Hibernate All Test finished");
	}

	/**
	 * Run's console tests (prerequisite for other tests to be able to execute them separately)
	 */
	public static void prepareConsole() {
		ConsoleTest consoleTest = new ConsoleTest();
		consoleTest.createConsole();
	}
}