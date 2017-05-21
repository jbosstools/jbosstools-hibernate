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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.eclipse.ui.dialogs.ExplorerItemPropertyDialog;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.db.DatabaseRequirement.Database;
import org.jboss.reddeer.swt.impl.combo.DefaultCombo;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.tools.hibernate.reddeer.jdt.ui.wizards.NewHibernateMappingElementsSelectionPage2;
import org.jboss.tools.hibernate.reddeer.jdt.ui.wizards.NewHibernateMappingFilePage;
import org.jboss.tools.hibernate.reddeer.jdt.ui.wizards.NewHibernateMappingFileWizard;
import org.jboss.tools.hibernate.reddeer.jdt.ui.wizards.NewHibernateMappingPreviewPage;
import org.jboss.tools.hibernate.reddeer.ui.xml.editor.Hibernate3CompoundEditor;
import org.jboss.tools.hibernate.ui.bot.test.XPathHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Hibernate mapping file (hbm.xml) test   
 * @author Jiri Peterka
 *
 */
@RunWith(RedDeerSuite.class)
@Database(name="testdb")
public class MappingFileTest extends HibernateRedDeerTest {
	
	//TODO use latest
	public static final String PRJ = "mvn-hibernate52";
	public static final String PCKG = "org.test";
	public static final String PCKG_CLZ = "org.test.clazz";
    
	@Before
	public void prepare() {
		importMavenProject(PRJ);
		
		try {
			Path dogLocation = new File("resources/classes/Dog.java").toPath();
			Path ownerLocation = new File("resources/classes/Owner.java").toPath();
			new File("target/"+PRJ+"/src/main/java/org/test").mkdirs();
			Files.copy(dogLocation, new FileOutputStream("target/"+PRJ+"/src/main/java/org/test/Dog.java"));
			Files.copy(ownerLocation, new FileOutputStream("target/"+PRJ+"/src/main/java/org/test/Owner.java"));
			
			new File("target/"+PRJ+"/src/main/java/org/test/clazz").mkdirs();
			Path ownerClazzLocation = new File("resources/classes/Owner.javaclazz").toPath();
			Files.copy(ownerClazzLocation, new FileOutputStream("target/"+PRJ+"/src/main/java/org/test/clazz/Owner.java"));
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unable to find pom "+PRJ);
		}
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.getProject(PRJ).refresh();
	}
	
	@After
	public void clean(){
		deleteAllProjects();
	}
	
	@Test
	public void createMappingFileFromPackage() {
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.getProject(PRJ).getProjectItem("Java Resources","src/main/java",PCKG).select();
		
		NewHibernateMappingFileWizard wizard = new NewHibernateMappingFileWizard();
		wizard.open();
		NewHibernateMappingElementsSelectionPage2 selPage = new NewHibernateMappingElementsSelectionPage2();
		selPage.selectItem(PCKG);
		wizard.next();
		NewHibernateMappingFilePage files = new NewHibernateMappingFilePage();
		assertEquals(2, files.getClasses().size());
		wizard.next();
		NewHibernateMappingPreviewPage preview = new NewHibernateMappingPreviewPage();
		assertTrue("Preview text cannot be empty", !preview.getPreviewText().equals(""));
		wizard.finish();
		
		pe.open();
		
		assertTrue("Hbm.xml not generated: Known issue(s): JBIDE-18769, JBIDE-20042",
				pe.getProject(PRJ).containsItem("Java Resources","src/main/java",PCKG,"Dog.hbm.xml"));
		
		pe.getProject(PRJ).getProjectItem("Java Resources","src/main/java",PCKG,"Dog.hbm.xml").open();
		Hibernate3CompoundEditor hme = new Hibernate3CompoundEditor("Dog.hbm.xml");
		hme.activateSourceTab();
		String sourceText = hme.getSourceText();

		
		XPathHelper xph = XPathHelper.getInstance();
		String table = xph.getMappingFileTable(PCKG + ".Dog", sourceText);
		assertTrue(table.equals("DOG"));
		
		pe.open();	
		pe.getProject(PRJ).getProjectItem("Java Resources","src/main/java",PCKG,"Owner.hbm.xml").open();

		hme = new Hibernate3CompoundEditor("Owner.hbm.xml");
		hme.activateSourceTab();
		sourceText = hme.getSourceText();

		table = xph.getMappingFileTable(PCKG + ".Owner", sourceText);
		assertEquals("OWNER", table);
	}
	
	@Test
	public void createMappingFileFromFile() {
		
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.getProject(PRJ).getProjectItem("Java Resources","src/main/java",PCKG_CLZ,"Owner.java").select();
		
		NewHibernateMappingFileWizard wizard = new NewHibernateMappingFileWizard();
		wizard.open();
		NewHibernateMappingElementsSelectionPage2 selPage = new NewHibernateMappingElementsSelectionPage2();
		selPage.selectItem("Owner");
		wizard.next();
		NewHibernateMappingFilePage files = new NewHibernateMappingFilePage();
		files.selectClasses("Owner");
		wizard.next();
		NewHibernateMappingPreviewPage preview = new NewHibernateMappingPreviewPage();
		assertTrue("Preview text cannot be empty", !preview.getPreviewText().equals(""));
		wizard.finish();
		
		pe.open();
		
		assertTrue("Hbm.xml not generated: Known issue(s): JBIDE-18769, JBIDE-20042",
				pe.getProject(PRJ).containsItem("Java Resources","src/main/java",PCKG_CLZ,"Owner.hbm.xml"));

		pe.getProject(PRJ).getProjectItem("Java Resources","src/main/java",PCKG_CLZ,"Owner.hbm.xml").open();
		
		String fileName = "Owner.hbm.xml";
		Hibernate3CompoundEditor hme = new Hibernate3CompoundEditor(fileName);
		hme.activateSourceTab();
		String sourceText = hme.getSourceText();
		
		XPathHelper xph = XPathHelper.getInstance();
		String table = xph.getMappingFileTable(PCKG_CLZ + ".Owner", sourceText);
		assertEquals("OWNER", table);
	}
	
	//JBIDE-21766
	@Test
	public void createMappingFilePackageWithNoConfig(){
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		ExplorerItemPropertyDialog pd = new ExplorerItemPropertyDialog(pe.getProject(PRJ));
		pd.open();
		new DefaultTreeItem("Hibernate Settings").select();
		new DefaultCombo().setSelection("<None>");
		pd.ok();
		createMappingFileFromPackage();
	}
	
	//JBIDE-21766
	@Test
	public void createMappingFileWithNoConfig(){
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		ExplorerItemPropertyDialog pd = new ExplorerItemPropertyDialog(pe.getProject(PRJ));
		pd.open();
		new DefaultTreeItem("Hibernate Settings").select();
		new DefaultCombo().setSelection("<None>");
		pd.ok();
		createMappingFileFromFile();
	}
	

}
