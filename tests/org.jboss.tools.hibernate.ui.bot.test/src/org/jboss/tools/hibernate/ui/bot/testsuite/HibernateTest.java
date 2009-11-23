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

import org.jboss.tools.hibernate.ui.bot.testcase.Activator;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.entity.JavaClassEntity;
import org.jboss.tools.ui.bot.ext.entity.JavaProjectEntity;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.PerspectiveType;

public class HibernateTest extends SWTTestExt {

	private static boolean prepared = false;
	//private static Properties properties;	

	public static void prepare() {

		if (prepared == true)
			return;
		
		log.info("Hibernate test preparation started");
		// Load project properties (not needed yet)
		// loadProperties(Activator.PLUGIN_ID);
		
		// Show perspective and view
		eclipse.closeView(IDELabel.View.WELCOME);
		eclipse.openPerspective(PerspectiveType.JAVA);

		// Create needed project
		// Prepare entity
		JavaProjectEntity projectEntity = new JavaProjectEntity();
		projectEntity.setProjectName(Project.PROJECT_NAME);
		eclipse.createJavaProject(projectEntity);

		// Create classes
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

		prepared = true;
		log.info("Hibernate test preparation finished");
	}

	public static void clean() {	
		log.info("Clean finished");
		bot.sleep(TIME_10S);
	}
}
