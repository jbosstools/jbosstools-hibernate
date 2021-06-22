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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.condition.ProjectContainsProjectItem;
import org.eclipse.reddeer.eclipse.core.resources.DefaultProject;
import org.eclipse.reddeer.eclipse.ui.dialogs.PropertyDialog;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.wst.common.project.facet.ui.FacetsPropertyPage;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.db.DatabaseRequirement.Database;
import org.eclipse.reddeer.swt.api.Shell;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.NextButton;
import org.eclipse.reddeer.swt.impl.button.OkButton;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.uiforms.impl.hyperlink.DefaultHyperlink;
import org.jboss.tools.hibernate.reddeer.editor.JpaXmlEditor;
import org.jboss.tools.hibernate.reddeer.wizard.JpaFacetInstallPage;
import org.jboss.tools.hibernate.ui.bot.test.XPathHelper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Test edits persistenceXML File
 * 
 * @author Jiri Peterka
 */
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
@Database
public class PersistenceXMLFileTest extends HibernateRedDeerTest {

	@Parameter
	public String prj;

	@After
	public void cleanUp() {
		deleteAllProjects();
	}

	@Parameters(name = "jpa {0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { 
			{ "ecl-jpa10" },
			{ "ecl-jpa20" },
			{ "ecl-jpa21" },
			{ "ecl-jpa22" }
		});
	}

	private void prepare() {
		importProject(prj, null);
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		PropertyDialog pd = pe.getProject(prj).openProperties();
		FacetsPropertyPage fp = new FacetsPropertyPage(pd);
		pd.select(fp);
		List<TreeItem> facets = fp.getSelectedFacets();
		boolean javaFacet = false;
		boolean jpaFacet = false;
		for(TreeItem facet: facets){
			if(facet.getText().equals("Java")){
				javaFacet = true;
			} else if (facet.getText().equals("JPA")){
				jpaFacet = true;
			}
		}
		if(!javaFacet){
			fp.selectFacet("Java");
			new DefaultHyperlink(pd).activate();
			Shell s = new DefaultShell("Modify Faceted Project");
			new OkButton(s).click();
			new WaitWhile(new ShellIsAvailable(s));
		}
		if(!jpaFacet){
			fp.selectFacet("JPA");
			new DefaultHyperlink(pd).activate();
			Shell s = new DefaultShell("Modify Faceted Project");
			new NextButton(s).click();
			JpaFacetInstallPage installPage = new JpaFacetInstallPage(s);
			installPage.setPlatform("Hibernate (JPA 2.1)");
			installPage.setJpaImplementation("Disable Library Configuration");
			new OkButton(s).click();
			new WaitWhile(new ShellIsAvailable(s));
		}
		pd.ok();
		
	}

	@Test
	public void editPersistenceXMLFile() {
		prepare();

		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		DefaultProject project = pe.getProject(prj);
		ProjectContainsProjectItem persistenceFileCondition = 
				new ProjectContainsProjectItem(project, "src", "META-INF","persistence.xml");
		new WaitUntil(persistenceFileCondition, TimePeriod.DEFAULT);
		persistenceFileCondition.getResult().open();
		
		JpaXmlEditor pexml = new JpaXmlEditor();
		pexml.setHibernateUsername("sa");
		pexml.setHibernateDialect("H2");
		pexml.save();

		String usernameProp = "hibernate.connection.username";
		String dialectProp = "hibernate.dialect";

		String usernameExpected = "sa";
		String dialectExpected = "org.hibernate.dialect.H2Dialect";

		XPathHelper xh = XPathHelper.getInstance();
		String text = pexml.getSourceText();
		String usrnameVal = xh.getPersistencePropertyValue(usernameProp, text);
		assertTrue("sa value is expected", usrnameVal.equals(usernameExpected));
		String dialectVal = xh.getPersistencePropertyValue(dialectProp, text);
		assertTrue("H2 value is expected", dialectVal.equals(dialectExpected));
	}
}