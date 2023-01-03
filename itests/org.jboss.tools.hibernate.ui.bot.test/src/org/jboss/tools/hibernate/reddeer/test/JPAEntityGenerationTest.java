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

import org.eclipse.reddeer.common.exception.RedDeerException;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.jdt.ui.packageview.PackageExplorerPart;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.db.DatabaseConfiguration;
import org.eclipse.reddeer.requirements.db.DatabaseRequirement;
import org.eclipse.reddeer.requirements.db.DatabaseRequirement.Database;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;
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
@Database
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
        		{"mvn-hibernate36-ent","3.6","2.0", true}, {"mvn-hibernate36-ent","3.6","2.0", false}, 
        		{"mvn-hibernate43-ent","4.3","2.1", true}, {"mvn-hibernate43-ent","4.3","2.1", false},
//        		{"mvn-hibernate50-ent","5.0","2.1", true}, {"mvn-hibernate50-ent","5.0","2.1", false},
//        		{"mvn-hibernate54-ent","5.4","2.1", true}, {"mvn-hibernate54-ent","5.4","2.1", false},
//        		{"mvn-hibernate55-ent","5.5","2.2", true}, {"mvn-hibernate55-ent","5.5","2.2", false},
        		{"mvn-hibernate56-ent","5.6","2.2", true}, {"mvn-hibernate56-ent","5.6","2.2", false},
        		{"mvn-hibernate60-ent","6.0","2.2", true}, {"mvn-hibernate60-ent","6.0","2.2", false},
        		{"mvn-hibernate61-ent","6.1","2.2", true}, {"mvn-hibernate61-ent","6.1","2.2", false},
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
    	
    	PackageExplorerPart pe = new PackageExplorerPart();    
    	pe.open();
    	try{
    		new WaitUntil(new EntityIsGenerated(prj, "src/main/java","org.gen","Actor.java"));
    		pe.getProject(prj).getProjectItem("src/main/java","org.gen","Actor.java").open();
    	} catch (RedDeerException e) {
    		e.printStackTrace();
    		fail("Entities not generated, possible cause https://issues.jboss.org/browse/JBIDE-19175");
    	}
    	new DefaultEditor("Actor.java");
    }
    
	
}