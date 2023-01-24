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
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.reddeer.jface.text.contentassist.ContentAssistant;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.db.DatabaseConfiguration;
import org.eclipse.reddeer.requirements.db.DatabaseRequirement;
import org.eclipse.reddeer.requirements.db.DatabaseRequirement.Database;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.jboss.tools.hibernate.reddeer.console.EditConfigurationMainPage;
import org.jboss.tools.hibernate.reddeer.console.EditConfigurationShell;
import org.jboss.tools.hibernate.reddeer.console.views.KnownConfigurationsView;
import org.jboss.tools.hibernate.reddeer.criteriaeditor.CriteriaEditor;
import org.jboss.tools.hibernate.ui.bot.test.factory.ConnectionProfileFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.ProjectConfigurationFactory;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;


/**
 * Hibernate Criteria Editor test for Code Assist 
 * @author Jiri Peterka
 */
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
@Database
public class CriteriaEditorCodeAssistTest extends HibernateRedDeerTest {

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
        		{"mvn-hibernate56-ent","5.6","2.2"},
        		{"mvn-hibernate60-ent","6.0","2.2"},
        		{"mvn-hibernate61-ent","6.1","2.2"},
           });
    }
	
    @InjectRequirement    
    private DatabaseRequirement dbRequirement;     
    
	@After
	public void cleanUp() {
		ConnectionProfileFactory.deleteAllConnectionProfiles();
		deleteAllProjects();
	}

    @Test
    public void testCriteriaEditorCodeAssist() {
    	prepareMaven();
    	checkCriteriaEditor();
    }
    
	private void prepareMaven() {
		prepareMvn(prj, hbVersion);
		
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		ProjectConfigurationFactory.setProjectFacetForDB(prj, cfg, jpaVersion);
		
		KnownConfigurationsView v = new KnownConfigurationsView();
		v.open();
		List<TreeItem> confs = v.getConsoleConfigurations();
		if(confs!= null){
			for(TreeItem i: confs){
				v.deleteConsoleConfiguration(i.getText());
			}
		}
		if (!v.isOpen()) {
			v.open();
		}
		v.activate();
		EditConfigurationShell confShell = v.addConfiguration();
		confShell.setName(prj);
		

		EditConfigurationMainPage mainPage = confShell.getMainPage();
		mainPage.setProject(prj);
		mainPage.setType("JPA (jdk 1.5+)");
		mainPage.setDatabaseConnection("[JPA Project Configured Connection]");
		mainPage.setHibernateVersion(hbVersion);
		confShell.ok();
	}
		
	private void checkCriteriaEditor() {
		KnownConfigurationsView v = new KnownConfigurationsView();
		v.open();
		v.selectConsole(prj);
		new ContextMenuItem("Hibernate Criteria Editor").select();

		String expression = "ses";
		CriteriaEditor criteriaEditor = new CriteriaEditor(prj);
		criteriaEditor.setText(expression);
		criteriaEditor.setCursorPosition(expression.length());		
		String proposal = "session : Session";
		ContentAssistant ca = criteriaEditor.openContentAssistant();
		List<String> proposals = ca.getProposals();
		ca.close();
		assertTrue(proposal + " is expected", proposals.contains(proposal));
		
		expression = "session.cre";
		criteriaEditor.setText(expression);
		criteriaEditor.setCursorPosition(expression.length());
		proposal = "createCriteria\\(Class arg0\\) \\: Criteria \\- Session";
		Double hv = Double.parseDouble(hbVersion);
		if(hv >= 4.0) {
			proposal = "createCriteria\\(Class \\w*\\) : Criteria - SharedSessionContract";
		}
		ca = criteriaEditor.openContentAssistant();
		proposals = ca.getProposals();
		ca.close();
		boolean shouldFail = true;
		for(String p: proposals){
			if(p.matches(proposal)){
				shouldFail = false;
				break;
			}
		}
		if(shouldFail){
			fail(proposal + " CA proposal is expected but was "+proposals);
		}
		
		expression = "session.createCriteria(Act";
		criteriaEditor.setText(expression);
		criteriaEditor.setCursorPosition(expression.length());
		proposal = "Actor - org.gen";
		ca = criteriaEditor.openContentAssistant();
		proposals = ca.getProposals();
		ca.close();
		assertTrue(proposal + " is expected", proposals.contains(proposal));						
	}
  
}