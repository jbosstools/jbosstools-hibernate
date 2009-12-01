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

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.core.runtime.Platform;
import org.jboss.tools.hibernate.ui.bot.testcase.Activator;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.entity.JavaClassEntity;
import org.jboss.tools.ui.bot.ext.entity.JavaProjectEntity;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.PerspectiveType;
import org.jboss.tools.ui.bot.ext.types.ViewType;

public class HibernateTest extends SWTTestExt {

	private static boolean finished = false;
	private static boolean classesCreated = false;
	private static boolean projectCreated = false;
	
	//private static Properties properties;	
	
	

	public static void prepare() {	
		createProject();
		createClasses();
	}
	
	public static void createClasses() {
		
		if (classesCreated) return; 
		
		// Class 1
		JavaClassEntity classEntity = new JavaClassEntity();
		classEntity.setClassName(Project.CLASS1);
		classEntity.setPackageName(Project.PACKAGE_NAME);
		eclipse.createJavaClass(classEntity);
		eclipse.setClassContentFromResource(Activator.PLUGIN_ID, "src", Project.PACKAGE_NAME, Project.CLASS1+".java");
		
		// Class 2		
		classEntity.setClassName(Project.CLASS2);
		classEntity.setPackageName(Project.PACKAGE_NAME);
		eclipse.createJavaClass(classEntity);
		eclipse.setClassContentFromResource(Activator.PLUGIN_ID, "src", Project.PACKAGE_NAME, Project.CLASS2+".java");

		classesCreated = true;		
	}
	
	public static void createProject() {
		
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
	
	public static void addDriverClassPath() {
		// TODO add hsqldb into classpath
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
	}

	public static void clean() {
		if (finished) return;
		
		log.info("Clean finished");
		bot.sleep(TIME_10S);
	}
}