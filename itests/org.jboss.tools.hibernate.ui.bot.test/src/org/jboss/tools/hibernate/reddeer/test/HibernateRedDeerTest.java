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

import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.eclipse.m2e.core.ui.wizard.MavenImportWizard;
import org.jboss.reddeer.eclipse.ui.views.properties.PropertiesView;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.requirements.autobuilding.AutoBuildingRequirement.AutoBuilding;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.jboss.reddeer.requirements.db.DatabaseConfiguration;
import org.jboss.reddeer.requirements.db.DatabaseRequirement;
import org.jboss.reddeer.swt.api.Shell;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.swt.condition.ShellIsAvailable;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.hibernate.reddeer.console.views.KnownConfigurationsView;
import org.jboss.tools.hibernate.ui.bot.test.Activator;
import org.jboss.tools.hibernate.ui.bot.test.DatabaseUtils;
import org.jboss.tools.hibernate.ui.bot.test.ProjectImporter;
import org.jboss.tools.hibernate.ui.bot.test.factory.ConnectionProfileFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.DriverDefinitionFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.HibernateToolsFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;

@AutoBuilding(value=false,cleanup=false)
public class HibernateRedDeerTest {
	
	@InjectRequirement
	protected DatabaseRequirement dbRequirement;
	
	@BeforeClass
	public static void beforeClass() {
		if(!new WorkbenchShell().isMaximized()){
			new WorkbenchShell().maximize();
		}
		
		//https://bugs.eclipse.org/bugs/show_bug.cgi?id=470094
		PropertiesView pw = new PropertiesView();
		if(pw.isOpened()){
			pw.close();
		}
	}
	
	@Before
	public void runSakila(){
		String dbPath = dbRequirement.getConfiguration().getDriverPath();
		DatabaseUtils.runSakilaDB(dbPath.substring(0, dbPath.lastIndexOf(File.separator)));
	}

	@AfterClass
	public static void afterClass() {
		DatabaseUtils.stopSakilaDB();
		deleteAllProjects();
		
	}
	
	public static void importProject(String prjName, Map<String,String> libraryPathMap) {
		ProjectImporter.importProjectWithoutErrors(Activator.PLUGIN_ID, prjName,libraryPathMap);
	}
	
	private static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                fail("Unable to delete "+dir);
	            }
	        }
	    }

	    return dir.delete();
	}
	
	public static void importMavenProject(String prjName) {
		try {
			Path sourceFolder = new File("resources/prj/"+prjName).toPath();
			File dir = new File("target/"+prjName);
			if(dir.exists()){
				deleteDir(dir);
			}
			Path destFolder = dir.toPath();
			Files.walkFileTree(sourceFolder, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
					Files.createDirectories(destFolder.resolve(sourceFolder.relativize(dir)));
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
					Files.copy(file, destFolder.resolve(sourceFolder.relativize(file)));
					return FileVisitResult.CONTINUE;
				}
			});
			
			
			MavenImportWizard wizard = new MavenImportWizard();
			wizard.open();
			wizard.getWizardPage().setRootDirectory("target/"+prjName);
			wizard.getWizardPage().refresh();
			wizard.getWizardPage().waitUntilProjectIsLoaded(TimePeriod.LONG);
			Shell shell = new DefaultShell("Import Maven Projects");
			new PushButton("Finish").click();
			new WaitWhile(new ShellIsAvailable(shell), TimePeriod.NORMAL);
			new WaitUntil(new JobIsRunning(), TimePeriod.NORMAL, false);
			new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
			
		} catch (IOException e) {
			fail("Unable to find pom "+prjName);
		}
		//TODO check error log for errors
	}

	protected static void deleteAllProjects(){
		deleteHibernateConfigurations();
		CleanWorkspaceRequirement req = new CleanWorkspaceRequirement();
		req.fulfill();
		deleteHibernateConfigurations();
		//windows is not able to delete sometimes due to locked files
	}
	
	protected void prepareMvn(String project, String hbVersion) {
    	importMavenProject(project);
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		DriverDefinitionFactory.createDatabaseDriverDefinition(cfg);
		HibernateToolsFactory.createConfigurationFile(cfg, project, "hibernate.cfg.xml", true);
		ConnectionProfileFactory.createConnectionProfile(cfg);
	}
	
	
	private static void deleteHibernateConfigurations(){
		KnownConfigurationsView v = new KnownConfigurationsView();
		v.open();
		while(v.getConsoleConfigurations() != null && !v.getConsoleConfigurations().isEmpty()){
			TreeItem i =  v.getConsoleConfigurations().get(0);
			i.select();
			ContextMenu closeConfig = new ContextMenu("Close Configuration");
			if(closeConfig.isEnabled()){
				closeConfig.select();
				new WaitWhile(new JobIsRunning());
			}
			v.deleteConsoleConfiguration(i.getText());
		}
	}
}	


