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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView.ProblemType;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.db.DatabaseRequirement.Database;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.menu.ShellMenuItem;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.tools.hibernate.reddeer.jdt.ui.jpa.process.wizard.HibernateJPAWizard;
import org.jboss.tools.hibernate.ui.bot.test.ProjectUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Test generates JPA annotations on plain POJO class
 * @author jpeterka
 *
 */
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
@Database
public class JpaAnnotationGenerationTest extends HibernateRedDeerTest {
	
	private final String PCKG = "org.test";
	
	@Parameter
	public String prj; 
	@Parameter(1)
	public String hbVersion;
	@Parameter(2)
	public String jpaVersion;
	
	@Parameters(name="hibernate {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{"mvn-hibernate35","3.5","2.0"},
        		{"mvn-hibernate36","3.6","2.0"},
        		{"mvn-hibernate40","4.0","2.0"},
        		{"mvn-hibernate43","4.3","2.1"},
        		{"mvn-hibernate50","5.0","2.1"},
        		{"mvn-hibernate51","5.1","2.1"},
        		{"mvn-hibernate52","5.2","2.1"},
           });
    }
	
	@Before 
	public void prepare() {
		importMavenProject(prj);
		try {
			Path dogLocation = new File("resources/classes/Dog.java").toPath();
			Path ownerLocation = new File("resources/classes/Owner.java").toPath();
			new File("target/"+prj+"/src/main/java/org/test").mkdirs();
			Files.copy(dogLocation, new FileOutputStream("target/"+prj+"/src/main/java/org/test/Dog.java"));
			Files.copy(ownerLocation, new FileOutputStream("target/"+prj+"/src/main/java/org/test/Owner.java"));
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unable to find pom "+prj);
		}
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.getProject(prj).refresh();
		
	}
	
	@After
	public void cleanUp() {
		deleteAllProjects();
	}
	
	@Test
	public void testGenerateJPAHibernateAnnotations() {		
		ProjectUtils.getItem(prj, PCKG, "Dog.java").select();
		new ContextMenuItem("Source","Generate Hibernate/JPA annotations...").select();
		postCheck("Dog");
		ProjectUtils.getItem(prj, PCKG, "Owner.java").select();
		new ShellMenuItem("Source","Generate Hibernate/JPA annotations...").select();		
		postCheck("Owner");
		
	}
	
	private void postCheck(String clazz) {
		HibernateJPAWizard jpaWizard = new HibernateJPAWizard();
		jpaWizard.next();
		jpaWizard.finish();
		
		ProjectUtils.getItem(prj, PCKG, clazz+".java").open();
		
		TextEditor editor = new TextEditor(clazz+".java");
		assertTrue(editor.getText().contains("@Entity"));
		ProblemsView pw = new ProblemsView();
		pw.open();
		assertEquals(0, pw.getProblems(ProblemType.ERROR).size());
		assertEquals(0, pw.getProblems(ProblemType.WARNING).size());	
	}
	
	
	
}