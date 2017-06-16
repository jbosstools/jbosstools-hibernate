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

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;

import org.jboss.reddeer.common.exception.RedDeerException;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.eclipse.jdt.ui.packageexplorer.PackageExplorer;
import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.autobuilding.AutoBuildingRequirement;
import org.jboss.reddeer.requirements.autobuilding.AutoBuildingRequirement.AutoBuilding;
import org.jboss.reddeer.requirements.db.DatabaseConfiguration;
import org.jboss.reddeer.requirements.db.DatabaseRequirement;
import org.jboss.reddeer.requirements.db.DatabaseRequirement.Database;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;
import org.jboss.reddeer.swt.impl.menu.ShellMenu;
import org.jboss.reddeer.workbench.impl.editor.DefaultEditor;
import org.jboss.tools.hibernate.reddeer.console.EditConfigurationMainPage;
import org.jboss.tools.hibernate.reddeer.console.EditConfigurationShell;
import org.jboss.tools.hibernate.reddeer.console.views.KnownConfigurationsView;
import org.jboss.tools.hibernate.ui.bot.test.factory.ConnectionProfileFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.DriverDefinitionFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.ProjectConfigurationFactory;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;


/**
 * Test Mapping Diagram for multiple Hibernate versions
 * @author Jiri Peterka
 */
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
@Database(name="testdb")
public class MappingDiagramTest extends HibernateRedDeerTest {

	@Parameter
	public String prj; 
	@Parameter(1)
	public String hbVersion;
	@Parameter(2)
	public String jpaVersion;
	
	@Parameters(name="hibernate {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{"mvn-hibernate35-ent","3.5","2.0"},
        		{"mvn-hibernate36-ent","3.6","2.0"},
        		{"mvn-hibernate40-ent","4.0","2.0"},
        		{"mvn-hibernate43-ent","4.3","2.1"},
        		{"mvn-hibernate50-ent","5.0","2.1"},
        		{"mvn-hibernate51-ent","5.1","2.1"},
        		{"mvn-hibernate52-ent","5.2","2.1"},
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

	@Test
    public void testMappingDiagram() {
		prepareMavenProject();
		checkMappingDiagram();
    }
    
	public void prepareMavenProject() {
    	importMavenProject(prj);
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		DriverDefinitionFactory.createDatabaseDriverDefinition(cfg);
		ConnectionProfileFactory.createConnectionProfile(cfg);
		ProjectConfigurationFactory.setProjectFacetForDB(prj, cfg, jpaVersion);

		KnownConfigurationsView v = new KnownConfigurationsView();
		v.open();
		EditConfigurationShell confShell = v.openConsoleConfiguration(prj);
		
		EditConfigurationMainPage mainPage = confShell.getMainPage();
		mainPage.setProject(prj);
		mainPage.setType("JPA (jdk 1.5+)");
		mainPage.setDatabaseConnection("[JPA Project Configured Connection]");
		mainPage.setHibernateVersion(hbVersion);
		confShell.ok();
	}

	private void checkMappingDiagram() {
		AutoBuilding ab = new AutoBuilding() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean value() {
				return false;
			}
			
			@Override
			public boolean cleanup() {
				// TODO Auto-generated method stub
				return true;
			}
		};
		
		AutoBuildingRequirement abr = new AutoBuildingRequirement();
		abr.setDeclaration(ab);
		openMappingDiagram();
		try{
			new DefaultEditor(prj+": Actor and 15 others");
		} catch (RedDeerException e) { //workaroud due to buggy auto building
			abr.fulfill();
			PackageExplorer pe = new PackageExplorer();
			pe.getProject(prj).select();
			new ShellMenu("Project","Build Project").select();
			new WaitWhile(new JobIsRunning());
			openMappingDiagram();
			new DefaultEditor(prj+": Actor and 15 others");
		} finally {
			abr.cleanUp();
		}
	}
	
	private void openMappingDiagram(){
		KnownConfigurationsView v = new KnownConfigurationsView();
		
		v.open();
		v.selectConsole(prj);
		
		ContextMenu mappingMenu = new ContextMenu("Mapping Diagram");
		mappingMenu.select();
	}
}
