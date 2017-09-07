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

import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.db.DatabaseConfiguration;
import org.eclipse.reddeer.requirements.db.DatabaseRequirement;
import org.eclipse.reddeer.requirements.db.DatabaseRequirement.Database;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;
import org.jboss.tools.hibernate.reddeer.wizard.NewDSXMLWizard;
import org.jboss.tools.hibernate.reddeer.wizard.WizardNewDSXMLFileCreationPage;
import org.jboss.tools.hibernate.ui.bot.test.factory.ConnectionProfileFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.DriverDefinitionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests JBoss Datasource   
 * @author Jiri Peterka
 *
 */
@RunWith(RedDeerSuite.class)
@Database
public class JBossDatasourceTest extends HibernateRedDeerTest {
	
	
	//TODO use latest version
	public static final String PRJ = "mvn-hibernate35";

    @InjectRequirement
    DatabaseRequirement dbRequirement;
    
	@Before
	public void prepare() {
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		DriverDefinitionFactory.createDatabaseDriverDefinition(cfg);
		ConnectionProfileFactory.createConnectionProfile(cfg);
		importMavenProject(PRJ);
	}
	
	@After
	public void cleanUp() {
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		ConnectionProfileFactory.deleteConnectionProfile(cfg.getProfileName());
	}
	
	@Test
	public void jbossDatasourceTest() {
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();

		NewDSXMLWizard wizard = new NewDSXMLWizard();
		wizard.open();
		WizardNewDSXMLFileCreationPage page =  new WizardNewDSXMLFileCreationPage(wizard);
		page.setConnectionProfile(cfg.getProfileName());
		page.setParentFolder("/" + PRJ + "/src/main/resources");
		wizard.finish();

		String dsFileName = cfg.getProfileName() + "-ds.xml";
		
		assertFalse(new DefaultEditor(dsFileName).isDirty());
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		assertTrue(pe.getProject(PRJ).containsResource("src","main","resources",dsFileName));
	
	}
		

}
