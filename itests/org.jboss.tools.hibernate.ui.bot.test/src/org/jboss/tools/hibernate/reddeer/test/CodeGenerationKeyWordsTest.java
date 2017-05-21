/*******************************************************************************
 * Copyright (c) 2016-2017 Red Hat, Inc.
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

import java.util.Arrays;
import java.util.Collection;

import org.jboss.reddeer.common.exception.RedDeerException;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.eclipse.jdt.ui.packageexplorer.PackageExplorer;
import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.jboss.reddeer.requirements.db.DatabaseConfiguration;
import org.jboss.reddeer.requirements.db.DatabaseRequirement;
import org.jboss.reddeer.requirements.db.DatabaseRequirement.Database;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.workbench.impl.editor.Marker;
import org.jboss.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.tools.hibernate.reddeer.condition.EntityIsGenerated;
import org.jboss.tools.hibernate.reddeer.console.wizards.NewReverseEngineeringFileWizard;
import org.jboss.tools.hibernate.reddeer.console.wizards.TableFilterWizardPage;
import org.jboss.tools.hibernate.reddeer.dialog.LaunchConfigurationsDialog;
import org.jboss.tools.hibernate.reddeer.mapper.editors.ReverseEngineeringEditor;
import org.jboss.tools.hibernate.ui.bot.test.factory.HibernateToolsFactory;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
@Database(name = "testdb")

//https://issues.jboss.org/browse/JBIDE-22283 works for hibernate >= 4.3
public class CodeGenerationKeyWordsTest extends HibernateRedDeerTest {

	@InjectRequirement
	private DatabaseRequirement dbRequirement;

	@Parameter
	public String prj;
	@Parameter(1)
	public String hbVersion;
	
	@Parameters(name="hibernate {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{"mvn-hibernate43","4.3"},
        		{"mvn-hibernate50","5.0"},
        		{"mvn-hibernate51","5.1"},
        		{"mvn-hibernate52","5.2"}
           });
    }
    
    @After
    public void deleteAll(){
    	new CleanWorkspaceRequirement().fulfill();
    }

	@Test
	public void testHibernateGenerateConfiguration() {
		prepareMvn();
		createAndRunHibernateGenerationConfiguration(true, "src/main/java");
		checkGeneratedEntities("src/main/java");
	}

	private void prepareMvn() {
		importMavenProject(prj);
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		HibernateToolsFactory.createConfigurationFile(cfg, prj, "hibernate.cfg.xml", true);
		HibernateToolsFactory.setHibernateVersion(prj, hbVersion);
	}

	private void createAndRunHibernateGenerationConfiguration(boolean reveng, String src) {
		if (reveng) {
			createRevengFile();
		}
		LaunchConfigurationsDialog dlg = new LaunchConfigurationsDialog();
		dlg.open();
		dlg.createNewConfiguration();
		dlg.selectConfiguration(prj);
		dlg.setOutputDir("/" + prj + "/" + src);
		dlg.setPackage("org.gen");
		dlg.setReverseFromJDBC(true);
		if (reveng)
			dlg.setRevengFile(prj, "hibernate.reveng.xml");
		new DefaultShell(LaunchConfigurationsDialog.DIALOG_TITLE).setFocus();
		dlg.selectExporter(0);
		dlg.selectExporter(1);
		dlg.apply();
		dlg.run();
	}

	private void checkGeneratedEntities(String src) {
		PackageExplorer pe = new PackageExplorer();
		pe.open();
		try {
			new WaitUntil(new EntityIsGenerated(prj, src, "org.gen", "Actor.java"));
			pe.getProject(prj).getProjectItem(src, "org.gen", "Actor.java").open();
		} catch (RedDeerException e) {
			fail("Entities not generated, possible cause https://issues.jboss.org/browse/JBIDE-19217");
		}
		TextEditor actorEditor = new TextEditor("Actor.java");
		assertTrue(actorEditor.getText().contains("implements_"));
		for (Marker m : actorEditor.getMarkers()) {
			if (m.getType().equals("org.eclipse.jdt.ui.error")) {
				fail("Actor has error markers");
			}
		}

	}

	private void createRevengFile() {
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.selectProjects(prj);

		NewReverseEngineeringFileWizard wizard = new NewReverseEngineeringFileWizard();
		wizard.open();
		wizard.next();
		TableFilterWizardPage page = new TableFilterWizardPage();
		page.setConsoleConfiguration(prj);
		page.refreshDatabaseSchema();
		page.pressInclude();
		wizard.finish();

		ReverseEngineeringEditor re = new ReverseEngineeringEditor();
		re.activateSourceTab();

		DefaultStyledText ds = new DefaultStyledText();
		ds.selectPosition(ds.getPositionOfText("</hibernate-reverse-engineering>"));
		ds.insertText("<table catalog=\"SAKILA\" schema=\"PUBLIC\" name=\"ACTOR\">"
				+ "<column name=\"FIRST_NAME\" property=\"implements\" type=\"string\" /></table>");

		re.save();
	}

}
