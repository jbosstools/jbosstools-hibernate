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


import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.db.DatabaseConfiguration;
import org.jboss.reddeer.requirements.db.DatabaseRequirement;
import org.jboss.reddeer.requirements.db.DatabaseRequirement.Database;
import org.jboss.tools.hibernate.reddeer.console.wizards.NewConfigurationFirstPage;
import org.jboss.tools.hibernate.reddeer.console.wizards.NewConfigurationWizard;
import org.jboss.tools.hibernate.reddeer.console.wizards.NewConfigurationWizardPage;
import org.jboss.tools.hibernate.reddeer.console.wizards.NewReverseEngineeringFileWizard;
import org.jboss.tools.hibernate.reddeer.console.wizards.TableFilterWizardPage;
import org.jboss.tools.hibernate.reddeer.mapper.editors.ReverseEngineeringEditor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Reverse Engineering File (reveng.xml) file test
 * Creates file
 * @author jpeterka
 *
 */
@RunWith(RedDeerSuite.class)
@Database(name="testdb")
public class RevengFileTest extends HibernateRedDeerTest {

	
	//TODO use latest
	private String PROJECT_NAME = "mvn-hibernate52";
	private String hbVersion = "5.2";
	@InjectRequirement
    private DatabaseRequirement dbRequirement;
	
	@Before 
	public void prepare() {
		importMavenProject(PROJECT_NAME);
		prepareConsoleConfiguration();
	}
	
	public void prepareConsoleConfiguration() {
		NewConfigurationWizard wizard = new NewConfigurationWizard();
		wizard.open();
		NewConfigurationFirstPage p1 = new NewConfigurationFirstPage();
		p1.setLocation(PROJECT_NAME,"src","main","java");		
		wizard.next();

		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		NewConfigurationWizardPage p2 = new NewConfigurationWizardPage();
		p2.setDatabaseDialect("H2");
		p2.setDriverClass(cfg.getDriverClass());
		p2.setConnectionURL(cfg.getJdbcString());
		p2.setUsername(cfg.getUsername());		
		p2.setCreateConsoleConfiguration(true);
		p2.setHibernateVersion(hbVersion);
		
		wizard.finish();
	}
	
	@Test
	public void testCreateRevengFile() {
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.selectProjects(PROJECT_NAME);		
		
		NewReverseEngineeringFileWizard wizard = new NewReverseEngineeringFileWizard();
		wizard.open();
		wizard.next();
		TableFilterWizardPage page = new TableFilterWizardPage();
		page.setConsoleConfiguration(PROJECT_NAME);
		page.refreshDatabaseSchema();
		page.pressInclude();
		wizard.finish();
		
		ReverseEngineeringEditor re = new ReverseEngineeringEditor();
		re.activateDesignTab();
		re.activateOverviewTab();
		re.activateSourceTab();
		re.activateTableFiltersTab();
		re.activateTypeMappingsTab();		
	}
}
