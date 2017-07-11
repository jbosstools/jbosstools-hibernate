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

import java.util.Arrays;
import java.util.Collection;

import org.jboss.reddeer.common.exception.WaitTimeoutExpiredException;
import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.db.DatabaseConfiguration;
import org.jboss.reddeer.requirements.db.DatabaseRequirement;
import org.jboss.reddeer.requirements.db.DatabaseRequirement.Database;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;
import org.jboss.tools.hibernate.reddeer.console.EditConfigurationMainPage;
import org.jboss.tools.hibernate.reddeer.console.EditConfigurationMainPage.PredefinedConnection;
import org.jboss.tools.hibernate.reddeer.console.views.KnownConfigurationsView;
import org.jboss.tools.hibernate.reddeer.console.wizards.NewConfigurationFirstPage;
import org.jboss.tools.hibernate.reddeer.console.wizards.NewConfigurationWizard;
import org.jboss.tools.hibernate.reddeer.console.wizards.NewConfigurationWizardPage;
import org.jboss.tools.hibernate.reddeer.console.EditConfigurationShell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Console configuration test
 * Creates Hibernate Configuration file (cfg.xml)
 * @author Jiri Peterka
 *
 */
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
@Database(name="testdb")
public class ConsoleConfigurationTest extends HibernateRedDeerTest {
	
	private Logger log = Logger.getLogger(ConsoleConfigurationTest.class);

	@Parameter
	public String prjName;
	
	@Parameter(1)
	public String hbVersion;
	
	private String HIBERNATE_CFG_FILE="src/hibernate.cfg.xml";
	private String CONSOLE_NAME="hibernateconsoletest";
	
	
	
	
	
	@Parameters(name="hibernate {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{"mvn-hibernate35", "3.5"}, 
        		{"mvn-hibernate36", "3.6"}, 
        		{"mvn-hibernate40", "4.0"}, 
        		{"mvn-hibernate43", "4.3"}, 
        		{"mvn-hibernate50", "5.0"}, 
        		{"mvn-hibernate51", "5.1"},
        		{"mvn-hibernate52", "5.2"}
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
		KnownConfigurationsView v = new KnownConfigurationsView();
		v.open();
		v.deleteConsoleConfiguration(CONSOLE_NAME);
		
		deleteAllProjects();
	}
	
	@Test 
	public void testConsoleConfiguration() {
		prepareConsoleConfigurationFile(hbVersion);
		prepareConsoleConfiguration(hbVersion);
	}
	
	public void prepareConsoleConfigurationFile(String hibernateVersion) {		
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		
		NewConfigurationWizard wizard = new NewConfigurationWizard();
		wizard.open();
		NewConfigurationFirstPage p1 = new NewConfigurationFirstPage();
		p1.setLocation(prjName,"src");
		wizard.next();

		NewConfigurationWizardPage p2 = new NewConfigurationWizardPage();
		p2.setDatabaseDialect("H2");
		p2.setDriverClass(cfg.getDriverClass());
		p2.setConnectionURL(cfg.getJdbcString());
		p2.setUsername(cfg.getUsername());
		p2.setHibernateVersion(hibernateVersion);
		wizard.finish();
	}

	public void prepareConsoleConfiguration(String hibernateVersion) {
		KnownConfigurationsView v = new KnownConfigurationsView();
		v.open();
		EditConfigurationShell s = v.addConfiguration();
		
		s.setName(CONSOLE_NAME);		
				
		EditConfigurationMainPage p = s.getMainPage();		
				
		p.setProject(prjName);
		p.setDatabaseConnection(PredefinedConnection.HIBERNATE_CONFIGURED_CONNECTION);		
		p.setConfigurationFile("/"+prjName+"/"+HIBERNATE_CFG_FILE);
		p.setHibernateVersion(hibernateVersion);
		//ANY ERROR IN WIZARD ??
		s.ok();
		
		v.open();
		EditConfigurationShell s2 = v.openConsoleConfiguration(CONSOLE_NAME);
		s2.close();
		
		v.open();
		try{
			v.selectNode(CONSOLE_NAME,"Database","SAKILA.PUBLIC","ACTOR");
		} catch (WaitTimeoutExpiredException e) {
			log.info("Wait timeout occured, try rebuilding console config");
			v.selectConsole(CONSOLE_NAME);
			new ContextMenu("Rebuild configuration").select();
			new WaitWhile(new JobIsRunning());
			v.selectNode(CONSOLE_NAME,"Database","SAKILA.PUBLIC","ACTOR");
		}
	}
}
