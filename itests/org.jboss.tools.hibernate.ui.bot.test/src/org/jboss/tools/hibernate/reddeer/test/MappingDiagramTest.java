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

import org.eclipse.reddeer.common.exception.RedDeerException;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.jdt.ui.packageview.PackageExplorerPart;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.autobuilding.AutoBuildingRequirement;
import org.eclipse.reddeer.requirements.autobuilding.AutoBuildingRequirement.AutoBuilding;
import org.eclipse.reddeer.requirements.db.DatabaseConfiguration;
import org.eclipse.reddeer.requirements.db.DatabaseRequirement;
import org.eclipse.reddeer.requirements.db.DatabaseRequirement.Database;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.menu.ShellMenuItem;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;
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
@Database
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
    		{"mvn-hibernate36-ent","3.6","2.0"},
    		{"mvn-hibernate43-ent","4.3","2.1"},
//    		{"mvn-hibernate50-ent","5.0","2.1"},
//    		{"mvn-hibernate54-ent","5.4","2.2"},
//    		{"mvn-hibernate55-ent","5.5","2.2"},
    		{"mvn-hibernate56-ent","5.6","2.2"},
    		{"mvn-hibernate60-ent","6.0","2.2"},
    		{"mvn-hibernate61-ent","6.1","2.2"},
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
			PackageExplorerPart pe = new PackageExplorerPart();
			pe.getProject(prj).select();
			new ShellMenuItem("Project","Build Project").select();
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
		
		ContextMenuItem mappingMenu = new ContextMenuItem("Mapping Diagram");
		mappingMenu.select();
	}
}
