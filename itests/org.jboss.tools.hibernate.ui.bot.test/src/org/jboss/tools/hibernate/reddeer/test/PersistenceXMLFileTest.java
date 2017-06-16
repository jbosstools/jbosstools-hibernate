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

import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.eclipse.ui.dialogs.ExplorerItemPropertyDialog;
import org.jboss.reddeer.eclipse.wst.common.project.facet.ui.FacetsPropertyPage;
import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.db.DatabaseRequirement.Database;
import org.jboss.reddeer.swt.api.Shell;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.swt.condition.ShellIsAvailable;
import org.jboss.reddeer.swt.impl.button.NextButton;
import org.jboss.reddeer.swt.impl.button.OkButton;
import org.jboss.reddeer.swt.impl.link.DefaultLink;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.uiforms.impl.hyperlink.DefaultHyperlink;
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
@Database(name = "testdb")
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
			{ "ecl-jpa21" }
		});
	}

	private void prepare() {
		importProject(prj, null);
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		ExplorerItemPropertyDialog pd = new ExplorerItemPropertyDialog(pe.getProject(prj));
		pd.open();
		FacetsPropertyPage fp = new FacetsPropertyPage();
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
			new DefaultHyperlink().activate();
			Shell s = new DefaultShell("Modify Faceted Project");
			new OkButton().click();
			new WaitWhile(new ShellIsAvailable(s));
		}
		if(!jpaFacet){
			fp.selectFacet("JPA");
			new DefaultHyperlink().activate();
			Shell s = new DefaultShell("Modify Faceted Project");
			new NextButton().click();
			JpaFacetInstallPage installPage = new JpaFacetInstallPage();
			installPage.setPlatform("Hibernate (JPA 2.1)");
			installPage.setJpaImplementation("Disable Library Configuration");
			new OkButton().click();
			new WaitWhile(new ShellIsAvailable(s));
		}
		pd.ok();
		
	}

	@Test
	public void editPersistenceXMLFile() {
		prepare();

		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.getProject(prj).getProjectItem("src", "META-INF","persistence.xml").open();
		
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