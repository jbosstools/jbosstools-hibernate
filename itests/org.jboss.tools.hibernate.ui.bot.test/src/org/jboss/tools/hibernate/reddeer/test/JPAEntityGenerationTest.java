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

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;

import org.jboss.reddeer.common.exception.RedDeerException;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.db.DatabaseConfiguration;
import org.jboss.reddeer.requirements.db.DatabaseRequirement;
import org.jboss.reddeer.requirements.db.DatabaseRequirement.Database;
import org.jboss.reddeer.workbench.impl.editor.DefaultEditor;
import org.jboss.tools.hibernate.reddeer.condition.EntityIsGenerated;
import org.jboss.tools.hibernate.ui.bot.test.factory.ConnectionProfileFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.DriverDefinitionFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.EntityGenerationFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.ProjectConfigurationFactory;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;


/**
 * Test prepares project and generate JPA entities from database 
 * @author Jiri Peterka
 */
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
@Database(name="testdb")
public class JPAEntityGenerationTest extends HibernateRedDeerTest {

	@Parameter
	public String prj;
	@Parameter(1)
	public String hbVersion;
	@Parameter(2)
	public String jpaVersion;
	@Parameter(3)
	public boolean useConsole;
	
	@Parameters(name="hibernate {1} use console: {3}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{"mvn-hibernate35-ent","3.5","2.0", true}, {"mvn-hibernate35-ent","3.5","2.0", false}, 
        		{"mvn-hibernate36-ent","3.6","2.0", true}, {"mvn-hibernate36-ent","3.6","2.0", false}, 
        		{"mvn-hibernate40-ent","4.0","2.0", true}, {"mvn-hibernate40-ent","4.0","2.0", false},
        		{"mvn-hibernate43-ent","4.3","2.1", true}, {"mvn-hibernate43-ent","4.3","2.1", false},
        		{"mvn-hibernate50-ent","5.0","2.1", true}, {"mvn-hibernate50-ent","5.0","2.1", false},
        		{"mvn-hibernate51-ent","5.1","2.1", true}, {"mvn-hibernate51-ent","5.1","2.1", false},
        		{"mvn-hibernate52-ent","5.2","2.1", true}, {"mvn-hibernate52-ent","5.2","2.1", false}
           });
    }
	 
    @InjectRequirement    
    private DatabaseRequirement dbRequirement;
    
    @After
	public void cleanUp() {
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		ConnectionProfileFactory.deleteConnectionProfile(cfg.getProfileName());
		
		deleteAllProjects();
	}
    
    
	private void prepare() {
    	importMavenProject(prj);
    	
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		DriverDefinitionFactory.createDatabaseDriverDefinition(cfg);
		ConnectionProfileFactory.createConnectionProfile(cfg);
		ProjectConfigurationFactory.setProjectFacetForDB(prj, cfg, jpaVersion);		
	}
    
    @Test
    public void testEntityGeneration() {
    	testEntityGeneration(useConsole);
    }
        
    private void testEntityGeneration(boolean useHibernateConsole) {
    	prepare();
    	
    	DatabaseConfiguration cfg = dbRequirement.getConfiguration();
    	EntityGenerationFactory.generateJPAEntities(cfg,prj,"org.gen",hbVersion,useHibernateConsole);
    	
    	ProjectExplorer pe = new ProjectExplorer();    
    	pe.open();
    	try{
    		new WaitUntil(new EntityIsGenerated(prj, "src/main/java","org.gen","Actor.java"));
    		pe.getProject(prj).getProjectItem("Java Resources","src/main/java","org.gen","Actor.java").open();
    	} catch (RedDeerException e) {
    		e.printStackTrace();
    		fail("Entities not generated, possible cause https://issues.jboss.org/browse/JBIDE-19175");
    	}
    	new DefaultEditor("Actor.java");
    }
    
	
}