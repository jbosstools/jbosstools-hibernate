/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.bot.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.reddeer.eclipse.ui.problems.Problem;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView.ProblemType;
import org.eclipse.reddeer.eclipse.ui.wizards.datatransfer.ExternalProjectImportWizardDialog;
import org.eclipse.reddeer.eclipse.ui.wizards.datatransfer.WizardProjectsImportPage;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.jboss.tools.common.reddeer.utils.ProjectHelper;
import org.jboss.tools.hibernate.ui.bot.test.factory.ResourceFactory;

/**
 * Project importer class provides handy methods to import project from
 * resources for further utilization by hibernate tests
 * 
 * @author Jiri Peterka
 *
 */
public class ProjectImporter {

	private static final String LIB_DIR="target/requirements/";
	private static final Logger log = Logger.getLogger(ProjectImporter.class);
	
	/**
	 * Import porject and requires no errors in problems log
	 * @param pluginId plug-in id of project where project resources are located
	 * @param projectName project name to import 
	 */
	public static void importProjectWithoutErrors(String pluginId, String projectName, Map<String, String>libraryPathMap) {
		
		importProject(pluginId, projectName);
		if(libraryPathMap != null){
			Map<String,String> fullPathJars = new HashMap<>();
			for(String jar: libraryPathMap.keySet()){
				if(libraryPathMap.get(jar) == null){
					fullPathJars.put(jar,LIB_DIR);
				} else {
					fullPathJars.put(jar,LIB_DIR+libraryPathMap.get(jar));
				}
			}
			ProjectHelper.addLibrariesIntoProject(projectName, fullPathJars);
		}
		new WaitUntil(new JobIsRunning(), TimePeriod.SHORT,false);
		new WaitWhile(new JobIsRunning(),TimePeriod.LONG);
		ProblemsView problemsView = new ProblemsView();
		problemsView.open();
		new WaitWhile(new JobIsRunning());
		
		List<Problem> problems = problemsView.getProblems(ProblemType.ERROR);
		for (Problem p : problems) {
			log.error("Unexpected "+ problems.size() + " problem(s):");
			log.dump("Problem: "+ p.toString());
		}
		
		assertTrue("No problems after import are expected", problems.size() == 0);
	}
	
	/**
	 * Import project
	 * @param pluginId plug-in id of project where project resources are located
	 * @param projectName project name to import 
	 */
	public static void importProject(String pluginId, String prjName) {
		ExternalProjectImportWizardDialog w = new ExternalProjectImportWizardDialog();
		w.open();
		WizardProjectsImportPage p1 = new WizardProjectsImportPage(w);
		p1.setRootDirectory(ResourceFactory.getResourcesLocation(pluginId, "prj"));
		p1.copyProjectsIntoWorkspace(true);
		p1.deselectAllProjects();
		p1.selectProjects(prjName);
		w.finish();
	}

	
}
