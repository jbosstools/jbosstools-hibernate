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

import static org.junit.Assert.assertTrue;

import org.jboss.reddeer.eclipse.jdt.ui.packageexplorer.PackageExplorer;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.db.DatabaseConfiguration;
import org.jboss.reddeer.requirements.db.DatabaseRequirement;
import org.jboss.reddeer.requirements.db.DatabaseRequirement.Database;
import org.jboss.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.tools.hibernate.reddeer.codegen.ExportAntCodeGenWizard;
import org.jboss.tools.hibernate.reddeer.codegen.ExportAntCodeGenWizardPage;
import org.jboss.tools.hibernate.reddeer.dialog.LaunchConfigurationsDialog;
import org.jboss.tools.hibernate.reddeer.perspective.HibernatePerspective;
import org.jboss.tools.hibernate.ui.bot.test.factory.HibernateToolsFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test export ant file based on Hibernate Code Generation Configuration 
 * @author Jiri Peterka
 */
@RunWith(RedDeerSuite.class)
@Database(name="testdb")
public class AntFileExportTest extends HibernateRedDeerTest {

	private final String PRJ = "antconfiguration";
	private final String GEN_NAME = "genconfiguration";
	private final String ANTFILE_NAME = "build.xml";
	

    @InjectRequirement    
    private DatabaseRequirement dbRequirement;
    
    @Before
	public void testConnectionProfile() {
    	DatabaseConfiguration cfg = dbRequirement.getConfiguration();
    	importProject(PRJ, null);		
		HibernateToolsFactory.createConfigurationFile(cfg, PRJ, "hibernate.cfg.xml", true);
	}
    
    @Test
    public void testAntFilenameExport() {
    	
    	HibernatePerspective p = new HibernatePerspective();
    	p.open();
    	
    	LaunchConfigurationsDialog launchDialog = new LaunchConfigurationsDialog();
    	launchDialog.open();
    	launchDialog.selectHibernateCodeGeneration(GEN_NAME);
    	launchDialog.selectConfiguration(PRJ);
    	launchDialog.apply();
    	launchDialog.close();
    	    	
    	PackageExplorer pe = new PackageExplorer();    
    	pe.open();
    	pe.selectProjects(PRJ);
    	
    	ExportAntCodeGenWizard w = new ExportAntCodeGenWizard();
    	w.open();
    	ExportAntCodeGenWizardPage page = new ExportAntCodeGenWizardPage();
    	page.setHibernateGenConfiguration(GEN_NAME);
    	page.setAntFileName(ANTFILE_NAME);
    	
    	w.finish();
    	
    	pe.open();
    	pe.getProject(PRJ).getProjectItem(ANTFILE_NAME).open();
    	
    	assertTrue("Ant file cannot be ampty", new TextEditor(ANTFILE_NAME).getText().length() > 0);
    }
}