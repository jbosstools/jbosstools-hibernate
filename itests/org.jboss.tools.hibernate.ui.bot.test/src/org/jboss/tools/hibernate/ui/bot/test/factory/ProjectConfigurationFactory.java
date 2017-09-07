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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.swt.condition.ControlIsEnabled;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.core.condition.WidgetIsFound;
import org.eclipse.reddeer.core.matcher.WithMnemonicTextMatcher;
import org.eclipse.reddeer.core.matcher.WithStyleMatcher;
import org.eclipse.reddeer.eclipse.ui.dialogs.PropertyDialog;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.wst.common.project.facet.ui.FacetsPropertyPage;
import org.eclipse.reddeer.requirements.db.DatabaseConfiguration;
import org.eclipse.reddeer.swt.api.Shell;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.impl.button.NextButton;
import org.eclipse.reddeer.swt.impl.button.OkButton;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.group.DefaultGroup;
import org.eclipse.reddeer.swt.impl.link.DefaultLink;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.eclipse.reddeer.uiforms.impl.hyperlink.DefaultHyperlink;
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
		PropertyDialog pd = pe.getProject(prj).openProperties();  	
		pd.select("Project Facets");
		
		//convert to faceted form
		new DefaultTreeItem("Project Facets").select();
    	new DefaultLink("Convert to faceted form...").click();
    	new WaitWhile(new JobIsRunning());
    	new WaitUntil(new WidgetIsFound(Button.class,new WithStyleMatcher(SWT.PUSH), new WithMnemonicTextMatcher("Apply")), TimePeriod.LONG);
    	PushButton apply = new PushButton("Apply");
    	new WaitUntil(new ControlIsEnabled(apply));
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
		
		PropertyDialog pd = pe.getProject(prj).openProperties(); 
		
		boolean javaFacet = false;
		FacetsPropertyPage pp = new FacetsPropertyPage(pd);
		pd.select(pp);
		

		for(TreeItem t: getFacets(pd.getShell())){
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
		
		closePreferences(pd);
		new WorkbenchShell().setFocus();
		pe.open();
		pe.selectProjects(prj);
		
		pd.open();		
		pd.select("JPA"); //TODO Why this takes so long ?
 
		JpaFacetInstallPage jpaPage = new JpaFacetInstallPage(pd);
		
		jpaPage.setConnectionProfile(cfg.getProfileName());
		jpaPage.setAutoDiscovery(true);
		closePreferences(pd);
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		checkPersistenceXML(prj);
	}	
	
	private static List<TreeItem> getFacets(Shell prefShell){
		List<TreeItem> facets = new ArrayList<TreeItem>();
		for(TreeItem i : new DefaultTree(prefShell,1).getItems()){
			if(i.isChecked())
				facets.add(i);
		}
		return facets;
	}
	
	
	private static void closePreferences(PropertyDialog pd){
		WidgetIsFound applyAndCloseButton = new WidgetIsFound(
				org.eclipse.swt.widgets.Button.class, new WithMnemonicTextMatcher("Apply and Close"));
		
		org.eclipse.reddeer.swt.api.Button btn;
		if(applyAndCloseButton.test()){
			btn = new PushButton("Apply and Close"); //oxygen changed button text
		} else {
			btn = new OkButton();	
		}
		btn.click();
		
		new WaitUntil(new ShellIsAvailable("Warning"), TimePeriod.SHORT, false);
		//warning shell appears for every facet that was not found
		//when eclipse is build by maven some plugins are missing
		while(new ShellIsAvailable("Warning").test()){
			Shell warningShell = new DefaultShell("Warning");
			new PushButton(warningShell, "Yes").click();
			new WaitWhile(new ShellIsAvailable(warningShell));
		}
		
		new WaitWhile(new ShellIsAvailable(pd.getShell())); 
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
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
