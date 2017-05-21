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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.jboss.reddeer.eclipse.jdt.ui.packageexplorer.PackageExplorer;
import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.db.DatabaseConfiguration;
import org.jboss.reddeer.requirements.db.DatabaseRequirement;
import org.jboss.reddeer.requirements.db.DatabaseRequirement.Database;
import org.jboss.reddeer.workbench.impl.editor.DefaultEditor;
import org.jboss.tools.hibernate.reddeer.console.views.KnownConfigurationsView;
import org.jboss.tools.hibernate.reddeer.console.wizards.NewConfigurationFirstPage;
import org.jboss.tools.hibernate.reddeer.console.wizards.NewConfigurationWizard;
import org.jboss.tools.hibernate.reddeer.console.wizards.NewConfigurationWizardPage;
import org.jboss.tools.hibernate.reddeer.console.wizards.SelectConnectionProfileDialog;
import org.jboss.tools.hibernate.ui.bot.test.factory.ConnectionProfileFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.DriverDefinitionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
@Database(name="testdb")
public class ConsoleConfigurationFileTest extends HibernateRedDeerTest {

	
	private String HIBERNATE_CFG_FILE="hibernate.cfg.xml";
	
	@Parameter
	public String prjName;
	@Parameter(1)
	public String hbVersion;
	
	
	@Parameters(name="hibernate {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{"mvn-hibernate35","3.5"}, 
        		{"mvn-hibernate36","3.6"}, 
        		{"mvn-hibernate40","4.0"}, 
        		{"mvn-hibernate43","4.3"}, 
        		{"mvn-hibernate50","5.0"}, 
        		{"mvn-hibernate51","5.1"}, 
        		{"mvn-hibernate52","5.2"},
           });
    }
	
    @InjectRequirement
    private DatabaseRequirement dbRequirement;

	
	@Before 
	public void prepare() {
		importMavenProject(prjName);
	}
	
	@After 
	public void clean() {			
		deleteAllProjects();
	}
	
	
	@Test
	public void createConfigurationFileFromDatasource() {
		// Create datasource
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		DriverDefinitionFactory.createDatabaseDriverDefinition(cfg);
		ConnectionProfileFactory.createConnectionProfile(cfg);

		NewConfigurationWizard wizard = new NewConfigurationWizard();
		wizard.open();
		
		NewConfigurationFirstPage p1 = new NewConfigurationFirstPage();		
		p1.setLocation(prjName,"src");		
		wizard.next();
		
		NewConfigurationWizardPage p2 = new NewConfigurationWizardPage();
		SelectConnectionProfileDialog connectionDialog = p2.getValuesFromConnection();
		connectionDialog.setProfileName(cfg.getProfileName());
		connectionDialog.ok();
		
		// Check values
		p2.setHibernateVersion(hbVersion);
		assertTrue("jdbc must match", p2.getConnectionURL().equals(cfg.getJdbcString()));
		assertTrue("driver must match", p2.getDriveClass().equals(cfg.getDriverClass()));
		assertTrue("username must match", p2.getUsername().equals(cfg.getUsername()));
		
		wizard.finish();
		
		checkFile(false);
	}
		
	@Test
	public void testCreateConfigurationFile() {
		NewConfigurationWizard wizard = createConfigFile();	
		wizard.finish();
		
		checkFile(false);
	}
	
	@Test
	public void testCreateConfigurationFileWithConsole() {
		NewConfigurationWizard wizard = createConfigFile();	
		
		NewConfigurationWizardPage p2 = new NewConfigurationWizardPage();
		p2.setCreateConsoleConfiguration(true);
		wizard.finish();
		
		checkFile(true);
	}
	
	private NewConfigurationWizard createConfigFile(){
		NewConfigurationWizard wizard = new NewConfigurationWizard();
		wizard.open();
		NewConfigurationFirstPage p1 = new NewConfigurationFirstPage();
		p1.setLocation(prjName,"src");		
		wizard.next();

		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		NewConfigurationWizardPage p2 = new NewConfigurationWizardPage();
		p2.setHibernateVersion(hbVersion);
		p2.setConnectionURL(cfg.getJdbcString());
		p2.setUsername(cfg.getUsername());	
		return wizard;
	}
	
	private void checkFile(boolean generateConsole) {
		PackageExplorer pe = new PackageExplorer();
		pe.open();
		pe.getProject(prjName).getProjectItem("src",HIBERNATE_CFG_FILE).open();
		new DefaultEditor(HIBERNATE_CFG_FILE);
		
		if (generateConsole) {
			KnownConfigurationsView v = new KnownConfigurationsView();
			v.selectConsole(prjName);		
		}
	}

}
