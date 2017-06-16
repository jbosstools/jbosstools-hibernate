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

import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.eclipse.condition.ProblemExists;
import org.jboss.reddeer.eclipse.jdt.ui.packageexplorer.PackageExplorer;
import org.jboss.reddeer.eclipse.ui.problems.Problem;
import org.jboss.reddeer.eclipse.ui.problems.ProblemsView;
import org.jboss.reddeer.eclipse.ui.problems.ProblemsView.ProblemType;
import org.jboss.reddeer.eclipse.ui.problems.matcher.ProblemsDescriptionMatcher;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.junit.screenshot.CaptureScreenshotException;
import org.jboss.reddeer.junit.screenshot.ScreenshotCapturer;
import org.jboss.reddeer.requirements.db.DatabaseRequirement.Database;
import org.jboss.reddeer.swt.impl.menu.ShellMenu;
import org.jboss.reddeer.requirements.autobuilding.AutoBuildingRequirement.AutoBuilding;
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
@Database(name="testdb")
@AutoBuilding(value=true,cleanup=true)
public class EntityValidationTest extends HibernateRedDeerTest {

	//TODO filter pom to add newest deps
	private String PROJECT_NAME = "mvn-jpa21-hibernate43";
		
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
		
		PackageExplorer pe = new PackageExplorer();
		pe.open();
		pe.getProject(PROJECT_NAME).getProjectItem("src/main/java","org.hibernate.ui.test.model","Address.java").delete();
		
		pv.activate();
		String expectedProblem = "org.hibernate.ui.test.model.Address is not mapped as an embeddable";
		new WaitUntil(new ProblemExists(ProblemType.ERROR, new ProblemsDescriptionMatcher(expectedProblem)));
		problems = pv.getProblems(ProblemType.ERROR, new ProblemsDescriptionMatcher(expectedProblem));
		assertTrue(expectedProblem + " error is expected", problems.size() == 2);
	}
		
	
	//known issue JBIDE-19526
	@Test(expected=HibernateTestException.class)
	public void userIdentifierGeneratorValidationTest() {		
		ProblemsView pv = new ProblemsView();
		pv.open();
		List<Problem> problems = pv.getProblems(ProblemType.ERROR);
		assertTrue(problems.isEmpty());
		PackageExplorer pe = new PackageExplorer();
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
		new WaitUntil(new ProblemExists(ProblemType.ERROR, new ProblemsDescriptionMatcher(expectedProblem)), TimePeriod.NORMAL, false);
		
		problems = pv.getProblems(ProblemType.ERROR, new ProblemsDescriptionMatcher(expectedProblem));
		if(problems.size() != 1){
			throw new HibernateTestException();
		}
	}
	
	private void buildProject(){
		PackageExplorer pe = new PackageExplorer();
		pe.open();
		pe.getProject(PROJECT_NAME).select();
		new ShellMenu("Project","Build Project").select();
		new WaitWhile(new JobIsRunning());
	}
}
