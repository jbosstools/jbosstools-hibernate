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
package org.jboss.tools.hibernate.ui.bot.test.factory;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.eclipse.ui.problems.Problem;
import org.jboss.reddeer.eclipse.ui.problems.ProblemsView;
import org.jboss.reddeer.eclipse.ui.problems.ProblemsView.ProblemType;
import org.jboss.tools.hibernate.reddeer.wizard.JpaFacetInstallPage;
import org.jboss.tools.hibernate.reddeer.wizard.JPAProjectWizard;
import org.jboss.tools.hibernate.reddeer.wizard.JPAProjectWizardFirstPage;

/**
 * JPA Project RedDeer factory
 * 
 * @author Jiri Peterka
 * 
 */
public class JPAProjectFactory {
	
	private static final Logger log = Logger.getLogger(JPAProjectFactory.class);
	
	/**
	 * Creates JPA Project
	 * @param prj projec name
	 * @param version JPA version
	 * @param platform JPA platform
	 */
	public static void createProject(String prj, String version, String platform) {

		log.step("Open JPA Project Wizard");
		JPAProjectWizard wizard = new JPAProjectWizard();
		wizard.open();

		JPAProjectWizardFirstPage firstPage = new JPAProjectWizardFirstPage();
		firstPage.setProjectName(prj);
		firstPage.setJPAVersion(version);

		wizard.next();
		wizard.next();

		log.step("Disable hibernate configuration");
		JpaFacetInstallPage facetPage = new JpaFacetInstallPage();
		facetPage.setPlatform(platform);
		facetPage.setJpaImplementation("Disable Library Configuration");

		log.step("Click finish");
		wizard.finish();

		new WaitWhile(new JobIsRunning());
		ProblemsView problemsView = new ProblemsView();
		problemsView.open();
		List<Problem> allErrors = problemsView.getProblems(ProblemType.ERROR);
		problemsView.open();
		assertTrue("No problems are expected (JBIDE-17855)", allErrors.size() == 0);
	}
	
}
