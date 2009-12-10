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

import static org.eclipse.swtbot.eclipse.finder.matchers.WithPartName.withPartName;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.core.runtime.Platform;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IViewReference;
import org.hamcrest.Matcher;
import org.hsqldb.Server;
import org.jboss.tools.hibernate.ui.bot.testcase.Activator;
import org.jboss.tools.hibernate.ui.bot.testcase.ConsoleTest;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.entity.JavaClassEntity;
import org.jboss.tools.ui.bot.ext.entity.JavaProjectEntity;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.helper.DatabaseHelper;
import org.jboss.tools.ui.bot.ext.types.DriverEntity;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.PerspectiveType;
import org.jboss.tools.ui.bot.ext.types.ViewType;

public class HibernateTest extends SWTTestExt {

	private static boolean finished = false;
	private static boolean classesCreated = false;
	private static boolean projectCreated = false;
	private static boolean databasePrepared = false;
	private static boolean dbRunning = false;
	
	private static Thread hsqlThread = null;  
	
	//private static Properties properties;	

	/**
	 * Prepare project and classes
	 */
	public static void prepare() {	
		prepareProject();
		prepareClasses();
	}
	
	/**
	 * Create testing classes for the project
	 */
	public static void prepareClasses() {
		
		if (classesCreated) return; 
		
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
	    bot.tree().expandNode(Project.PROJECT_NAME).expandNode("hsqldb.jar").select();
	    
	    bot.button(IDELabel.Button.OK).click();
	    bot.sleep(TIME_1S);
	    bot.button(IDELabel.Button.OK).click();
	    bot.sleep(TIME_1S);
	}
	
	/**
	 * Add HSQLDB driver into project
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void addDriverIntoProject() throws FileNotFoundException, IOException {
		File in = util.getResourceFile(Activator.PLUGIN_ID, "drv","hsqldb.jar");
		File out = new File(Platform.getLocation() + File.separator + Project.PROJECT_NAME + File.separator + "hsqldb.jar");
		
        FileChannel inChannel = null;
        FileChannel outChannel = null;

		inChannel = new FileInputStream(in).getChannel();
		outChannel = new FileOutputStream(out).getChannel();

    	inChannel.transferTo(0, inChannel.size(),	outChannel);

    	if (inChannel != null) inChannel.close();
    	if (outChannel != null) outChannel.close();
    	log.info("Driver hsqldb.jar copied");
	}

	/**
	 * Clean after tests
	 */
	public static void clean() {
		if (finished) return;
		
		log.info("Clean finished");
	}

	/**
	 * Run's console tests (prerequisite for other tests to be able to execute them separately)
	 */
	public static void prepareConsole() {
		ConsoleTest consoleTest = new ConsoleTest();
		consoleTest.createConsole();
	}
		
	/**
	 * Prepares database and insert test data by using connection profile and sql scrapbook
	 */
	public static void prepareDatabase()  {
		if (databasePrepared) return;
		
		runHSQLDBServer(Project.DB_FILE,Project.DB_NAME);
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(Platform.getLocation());
		stringBuilder.append(File.separator);
		stringBuilder.append(Project.PROJECT_NAME);
		stringBuilder.append(File.separator);
		stringBuilder.append("hsqldb.jar");
		
		try {
			DriverEntity entity = new DriverEntity();
			entity.setDrvPath(stringBuilder.toString());
			entity.setJdbcString("jdbc:hsqldb:hsql://localhost/xdb");
			DatabaseHelper.createDriver(entity);
		} catch (ConnectionProfileException e) {
			log.error("Unable to create HSQL Driver" + e);
			fail();			
		}

		eclipse.openPerspective(PerspectiveType.DB_DEVELOPMENT);
		eclipse.showView(ViewType.DATA_SOURCE_EXPLORER);		
		
		bot.sleep(TIME_1S);
		
		//bot.activetree().expandNode("Database Connections").select();
		
		Matcher<IViewReference> matcher = withPartName("Data Source Explorer");
		SWTBotView view = bot.view(matcher);
		
		bot.sleep(TIME_1S);
		SWTBotTree tree = view.bot().tree();

		// Open SQL Scrapbook 
		SWTBotTreeItem item = tree.expandNode("Database Connections").expandNode("DefaultDS");
		item.contextMenu("Connect").click();		
		item.contextMenu("Open SQL Scrapbook").click();
						
		// Set SQL Scrapbook		
		SWTBotEditor editor = bot.editorByTitle("SQL Scrapbook 0");
		editor.setFocus();
		bot.comboBoxWithLabelInGroup("Type:","Connection profile").setSelection("HSQLDB_1.8");
		bot.comboBoxWithLabelInGroup("Name:","Connection profile").setSelection("DefaultDS");
		bot.comboBoxWithLabelInGroup("Database:","Connection profile").setSelection("Default");

		// Insert SQL script into editor
		eclipse.setClassContentFromResource(false, Activator.PLUGIN_ID, "sql", "SQL Scrapbook 0");
		
		// Execute Script and close
		bot.editorByTitle("SQL Scrapbook 0").toTextEditor().contextMenu("Execute All").click();		
		editor.close();
		
		bot.sleep(TIME_5S);
	}
	
	/**
	 * Run HSQLDB database in server mode
	 * @param file
	 * @param dbname
	 */
	public static void runHSQLDBServer(final String file, final String dbname) {
		if (dbRunning) return;

		log.info("Starting HSQLDB");
		Runnable runable = new Runnable() {
			
			public void run() {
				Server.main(new String[] {"-database.0","file:" + file,"-dbname.0",dbname });
			}
		};
		
		hsqlThread = new Thread(runable);
		hsqlThread.start();
		log.info("HSQLDB started");
		dbRunning = true;
	}
	
	/**
	 * Stop HSQL Database by sending SHUTDOWN command
	 */
	public static void stopHSQLDBServer() {
		
		try {		
			Class.forName("org.hsqldb.jdbcDriver");
			
			Connection connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb");
			
			Statement statement = connection.createStatement();
			ResultSet resultset = statement.executeQuery("SHUTDOWN");
			
			resultset.close();
			statement.close();
			connection.close();
			
			
		} catch (SQLException e) {
			
		}
		catch (ClassNotFoundException e) {
			log.error("Unable to stop HSQLDB " + e);
		}

		
	}
}