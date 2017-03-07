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

import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.db.DatabaseRequirement.Database;
import org.jboss.tools.hibernate.reddeer.editor.JpaXmlEditor;
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
	}

	@Test
	public void editPersistenceXMLFile() {
		prepare();

		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.getProject(prj).getProjectItem("JPA Content", "persistence.xml").open();
		
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