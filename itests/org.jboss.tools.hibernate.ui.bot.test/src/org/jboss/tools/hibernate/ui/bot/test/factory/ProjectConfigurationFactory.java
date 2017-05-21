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
package org.jboss.tools.hibernate.ui.bot.test.factory;

import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.core.condition.WidgetIsFound;
import org.jboss.reddeer.core.matcher.ClassMatcher;
import org.jboss.reddeer.core.matcher.WithMnemonicTextMatcher;
import org.jboss.reddeer.core.matcher.WithStyleMatcher;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.eclipse.ui.dialogs.ExplorerItemPropertyDialog;
import org.jboss.reddeer.eclipse.wst.common.project.facet.ui.FacetsPropertyPage;
import org.jboss.reddeer.requirements.db.DatabaseConfiguration;
import org.jboss.reddeer.swt.api.Shell;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.swt.condition.ShellIsAvailable;
import org.jboss.reddeer.swt.condition.WidgetIsEnabled;
import org.jboss.reddeer.swt.impl.button.NextButton;
import org.jboss.reddeer.swt.impl.button.OkButton;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.combo.DefaultCombo;
import org.jboss.reddeer.swt.impl.combo.LabeledCombo;
import org.jboss.reddeer.swt.impl.group.DefaultGroup;
import org.jboss.reddeer.swt.impl.link.DefaultLink;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.uiforms.impl.hyperlink.DefaultHyperlink;
import org.jboss.tools.hibernate.reddeer.editor.JpaXmlEditor;
import org.jboss.tools.hibernate.reddeer.wizard.JpaFacetInstallPage;


/**
 * Project configuration factory provides common routines for setting Hibernate
 * nature for selected proejct
 * 
 * @author jpeterka
 *
 */
public class ProjectConfigurationFactory {

	private static final Logger log = Logger.getLogger(ProjectConfigurationFactory.class);
	
	/**
	 * Convert project to facet form
	 * @param prj given project name
	 */
	public static void convertProjectToFacetsForm(String prj) {
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		ExplorerItemPropertyDialog pd = new ExplorerItemPropertyDialog(pe.getProject(prj));
		pd.open();    	
		pd.select("Project Facets");
		
		//convert to faceted form
		new DefaultTreeItem("Project Facets").select();
    	new DefaultLink("Convert to faceted form...").click();
    	new WaitWhile(new JobIsRunning());
    	new WaitUntil(new WidgetIsFound<Button>(new ClassMatcher(Button.class),new WithStyleMatcher(SWT.PUSH), new WithMnemonicTextMatcher("Apply")), TimePeriod.LONG);
    	PushButton apply = new PushButton("Apply");
    	new WaitUntil(new WidgetIsEnabled(apply));
    	apply.click();    
		pd.ok();
	}
	
	/**
	 * Sets JPA project facets for given database configuration with JPA 2.1
	 * @param prj given project
	 * @param cfg given database configuration
	 */
	public static void setProjectFacetForDB(String prj,	DatabaseConfiguration cfg) {
		setProjectFacetForDB(prj,cfg,"2.1");
	}
	
	/**
	 * Sets JPA project facets for given database configuration
	 * @param prj given project
	 * @param cfg given database configuration
	 * @param jpaVersion JPA version (2.0 or 2.1 is supported)
	 */
	public static void setProjectFacetForDB(String prj, DatabaseConfiguration cfg, String jpaVersion) {
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		
		ExplorerItemPropertyDialog pd = new ExplorerItemPropertyDialog(pe.getProject(prj));
		pd.open(); 
		pd.select("Project Facets");
		
		
		boolean javaFacet = false;
		FacetsPropertyPage pp = new FacetsPropertyPage();
		for(TreeItem t: pp.getSelectedFacets()){
			if(t.getText().equals("Java")){
				javaFacet = true;
				break;
			}
		}
		if(!javaFacet){
			pp.selectFacet("Java");
			DefaultHyperlink hyperlink = new DefaultHyperlink();
			hyperlink.activate();	
							
			Shell s= new DefaultShell("Modify Faceted Project");

			new OkButton().click();
			
			new WaitWhile(new ShellIsAvailable(s));
			
		}
		pp.selectFacet("JPA");
		pp.selectVersion("JPA",jpaVersion);
		
		addFurtherJPAConfiguration(jpaVersion,!javaFacet);
		pd.ok();
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		pe.open();
		pe.selectProjects(prj);
		
		pd.open();		
		pd.select("JPA"); //TODO Why this takes so long ?
 
		JpaFacetInstallPage jpaPage = new JpaFacetInstallPage();
		
		jpaPage.setConnectionProfile(cfg.getProfileName());
		jpaPage.setAutoDiscovery(true);
		pd.ok();
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		checkPersistenceXML(prj);
	}	

	/**
	 * Check persistence.xml 
	 * @param prj project name
	 */
	public static void checkPersistenceXML(String prj) {
		log.info("Open persistence xml file");
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.getProject(prj).getProjectItem("JPA Content", "persistence.xml").open();

		log.info("In editor set some hibernate properties on hibernate tab");
		JpaXmlEditor pexml = new JpaXmlEditor();

		String sourceText = pexml.getSourceText();

		pexml.close();

		assertTrue("persistence.xml cannot be empty", sourceText.length() > 0);
	}
	
	private static void addFurtherJPAConfiguration(String jpaVersion, boolean addedJavaFacet) {	

		DefaultHyperlink hyperlink = new DefaultHyperlink();
		hyperlink.activate();	
						
		Shell s = new DefaultShell("Modify Faceted Project");
		if(addedJavaFacet){
			new NextButton().click();
		}
		DefaultGroup group = new DefaultGroup("Platform");
				
		new DefaultCombo(group).setSelection("Hibernate (JPA " + jpaVersion + ")");
		
		
		new LabeledCombo("Type:").setSelection("Disable Library Configuration");
		new OkButton().click();
		
		new WaitWhile(new ShellIsAvailable(s));
	}
	
	


}
