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
package org.jboss.tools.hibernate.reddeer.test;

import static org.junit.Assert.*;

import java.util.List;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ProblemExists;
import org.eclipse.reddeer.eclipse.jdt.ui.packageview.PackageExplorerPart;
import org.eclipse.reddeer.eclipse.ui.markers.matcher.MarkerDescriptionMatcher;
import org.eclipse.reddeer.eclipse.ui.problems.Problem;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView.ProblemType;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.junit.screenshot.CaptureScreenshotException;
import org.eclipse.reddeer.junit.screenshot.ScreenshotCapturer;
import org.eclipse.reddeer.requirements.db.DatabaseRequirement.Database;
import org.eclipse.reddeer.requirements.autobuilding.AutoBuildingRequirement.AutoBuilding;
import org.jboss.tools.hibernate.ui.bot.test.HibernateTestException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests checks if entity validation is working
 * @author Jiri Peterka
 *
 */
@RunWith(RedDeerSuite.class)
@Database
@AutoBuilding(value=true,cleanup=true)
public class EntityValidationTest extends HibernateRedDeerTest {

	//TODO filter pom to add newest deps
	private String PROJECT_NAME = "mvn-jpa21-hibernate55";
		
	@Before 
	public void prepare() {
		importMavenProject(PROJECT_NAME);
	}
	
	@After 
	public void clean() {			
		deleteAllProjects();
	}

	
	@Test
	public void embeddedEntityValidationTest() {		
		ProblemsView pv = new ProblemsView();
		pv.open();

		List<Problem> problems = pv.getProblems(ProblemType.ERROR);
		assertTrue(problems.isEmpty());
		
		PackageExplorerPart pe = new PackageExplorerPart();
		pe.open();
		pe.getProject(PROJECT_NAME).getProjectItem("src/main/java","org.hibernate.ui.test.model","Address.java").delete();
		
		pv.activate();
		String expectedProblem = "org.hibernate.ui.test.model.Address is not mapped as an embeddable";
		new WaitUntil(new ProblemExists(ProblemType.ERROR, new MarkerDescriptionMatcher(expectedProblem)));
		problems = pv.getProblems(ProblemType.ERROR, new MarkerDescriptionMatcher(expectedProblem));
		assertTrue(expectedProblem + " error is expected", problems.size() == 2);
	}
		
	
	//known issue JBIDE-19526
	@Test(expected=HibernateTestException.class)
	public void userIdentifierGeneratorValidationTest() {		
		ProblemsView pv = new ProblemsView();
		pv.open();
		List<Problem> problems = pv.getProblems(ProblemType.ERROR);
		assertTrue(problems.isEmpty());
		PackageExplorerPart pe = new PackageExplorerPart();
		pe.open();
		pe.getProject(PROJECT_NAME).getProjectItem("src/main/java","org.hibernate.ui.test.model","UserIdGenerator.java").delete();
		
		try {
			ScreenshotCapturer.getInstance().captureScreenshot("entity_validation");
		} catch (CaptureScreenshotException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pv.activate();
		String expectedProblem = "Strategy class \"org.hibernate.ui.test.model.UserIdGenerator\" could not be found.";
		new WaitUntil(new ProblemExists(ProblemType.ERROR, new MarkerDescriptionMatcher(expectedProblem)), TimePeriod.DEFAULT, false);
		
		problems = pv.getProblems(ProblemType.ERROR, new MarkerDescriptionMatcher(expectedProblem));
		if(problems.size() != 1){
			throw new HibernateTestException();
		}
	}
}
