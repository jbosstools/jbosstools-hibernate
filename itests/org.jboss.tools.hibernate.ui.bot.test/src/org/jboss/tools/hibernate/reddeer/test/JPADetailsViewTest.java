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

import org.jboss.reddeer.common.exception.RedDeerException;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.db.DatabaseConfiguration;
import org.jboss.reddeer.requirements.db.DatabaseRequirement;
import org.jboss.reddeer.requirements.db.DatabaseRequirement.Database;
import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.tools.hibernate.reddeer.view.JPADetailsView;
import org.jboss.tools.hibernate.ui.bot.test.factory.ConnectionProfileFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.DriverDefinitionFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.ProjectConfigurationFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests JPA Details view
 * 
 * @author Jiri Peterka
 */
@RunWith(RedDeerSuite.class)
@Database(name = "testdb")
public class JPADetailsViewTest extends HibernateRedDeerTest {

	//TODO use latest
	private final String PRJ = "mvn-hibernate52-ent";
	@InjectRequirement
	private DatabaseRequirement dbRequirement;

	@Before
	public void testConnectionProfile() {
		importMavenProject(PRJ);
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		DriverDefinitionFactory.createDatabaseDriverDefinition(cfg);
		ConnectionProfileFactory.createConnectionProfile(cfg);
		ProjectConfigurationFactory.setProjectFacetForDB(PRJ, cfg);
	}
	
	@After
	public void cleanUp() {
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		ConnectionProfileFactory.deleteConnectionProfile(cfg.getProfileName());
	}

	@Test
	public void testJPADetailView() {

		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.getProject(PRJ).getProjectItem("Java Resources","src/main/java","org.gen","Actor.java").open();
		TextEditor textEditor = new TextEditor("Actor.java");
		textEditor.setCursorPosition(20, 1);		

		JPADetailsView jpaDetailsView = new JPADetailsView();
		jpaDetailsView.open();
					
		try {
			new DefaultStyledText("Type 'Actor' is mapped as entity.");
		} catch (RedDeerException e) {
			fail("JPA details should be available - known issue - https://issues.jboss.org/browse/JBIDE-17940");
		}
	}

	
}