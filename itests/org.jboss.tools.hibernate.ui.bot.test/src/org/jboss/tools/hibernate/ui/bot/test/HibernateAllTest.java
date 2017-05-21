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
package org.jboss.tools.hibernate.ui.bot.test;

import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.hibernate.reddeer.test.AntFileExportTest;
import org.jboss.tools.hibernate.reddeer.test.CodeGenerationConfigurationTest;
import org.jboss.tools.hibernate.reddeer.test.CodeGenerationKeyWordsTest;
import org.jboss.tools.hibernate.reddeer.test.ConnectionProfileTest;
import org.jboss.tools.hibernate.reddeer.test.ConsoleConfigurationFileTest;
import org.jboss.tools.hibernate.reddeer.test.ConsoleConfigurationTest;
import org.jboss.tools.hibernate.reddeer.test.CreateJPAProjectTest;
import org.jboss.tools.hibernate.reddeer.test.CriteriaEditorCodeAssistTest;
import org.jboss.tools.hibernate.reddeer.test.CriteriaEditorTest;
import org.jboss.tools.hibernate.reddeer.test.EntityValidationTest;
import org.jboss.tools.hibernate.reddeer.test.HQLEditorCodeAssistTest;
import org.jboss.tools.hibernate.reddeer.test.HQLEditorTest;
import org.jboss.tools.hibernate.reddeer.test.HibernateUIPartsTest;
import org.jboss.tools.hibernate.reddeer.test.JBossDatasourceTest;
import org.jboss.tools.hibernate.reddeer.test.JPADetailsViewTest;
import org.jboss.tools.hibernate.reddeer.test.JPAEntityGenerationTest;
import org.jboss.tools.hibernate.reddeer.test.JPAFacetTest;
import org.jboss.tools.hibernate.reddeer.test.JPAUIPartsTest;
import org.jboss.tools.hibernate.reddeer.test.JpaAnnotationGenerationTest;
import org.jboss.tools.hibernate.reddeer.test.MappingDiagramTest;
import org.jboss.tools.hibernate.reddeer.test.MappingFileTest;
import org.jboss.tools.hibernate.reddeer.test.PersistenceXMLFileTest;
import org.jboss.tools.hibernate.reddeer.test.RevengFileTest;
import org.jboss.tools.hibernate.reddeer.test.TablesFromJPAEntitiesGeneration;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(RedDeerSuite.class)
@Suite.SuiteClasses({

	AntFileExportTest.class,
	CodeGenerationConfigurationTest.class,
	CodeGenerationKeyWordsTest.class,
	ConnectionProfileTest.class,
	ConsoleConfigurationFileTest.class,
	ConsoleConfigurationTest.class,
	CreateJPAProjectTest.class,
	CriteriaEditorTest.class,
	CriteriaEditorCodeAssistTest.class,
	EntityValidationTest.class,
	JPADetailsViewTest.class,
	HibernateUIPartsTest.class,
	JPAEntityGenerationTest.class,
	
	JPAFacetTest.class,
	JPAUIPartsTest.class,
	HQLEditorTest.class,
	HQLEditorCodeAssistTest.class,
	JBossDatasourceTest.class,
	JpaAnnotationGenerationTest.class, 
	MappingDiagramTest.class,
	MappingFileTest.class,
	PersistenceXMLFileTest.class, 
	RevengFileTest.class,
	TablesFromJPAEntitiesGeneration.class
	

})
public class HibernateAllTest {

}
