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

import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.db.DatabaseConfiguration;
import org.jboss.reddeer.requirements.db.DatabaseRequirement;
import org.jboss.reddeer.requirements.db.DatabaseRequirement.Database;
import org.jboss.tools.hibernate.ui.bot.test.factory.ConnectionProfileFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.DriverDefinitionFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.ProjectConfigurationFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Set JPA Facets for JPA project
 * 
 * @author Jiri Peterka
 */
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
@Database(name = "testdb")
public class JPAFacetTest extends HibernateRedDeerTest {

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
    
	@InjectRequirement
	private DatabaseRequirement dbRequirement;

	@Before
	public void testConnectionProfile() {
		importMavenProject(prj);
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		DriverDefinitionFactory.createDatabaseDriverDefinition(cfg);
		ConnectionProfileFactory.createConnectionProfile(cfg);
	}

	@After
	public void cleanUp() {
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		ConnectionProfileFactory.deleteConnectionProfile(cfg.getProfileName());
		deleteAllProjects();
	}

	@Test
	public void testSetJPAFacets() {
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		ProjectConfigurationFactory.setProjectFacetForDB(prj, cfg, jpaVersion);
	}

}