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

import org.eclipse.reddeer.junit.runner.RedDeerSuite;
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
	
	AntFileExportTest.class, // pass
	CodeGenerationConfigurationTest.class, // pass
	CodeGenerationKeyWordsTest.class, // pass 
	ConnectionProfileTest.class, // pass
	ConsoleConfigurationFileTest.class, // pass
	ConsoleConfigurationTest.class, // pass
	CreateJPAProjectTest.class, // pass
	CriteriaEditorTest.class, // check 60 and 61
	CriteriaEditorCodeAssistTest.class, // need check - 36, 60, 61
	EntityValidationTest.class, // pass
	JPADetailsViewTest.class, // pass
	HibernateUIPartsTest.class, // pass
	JPAEntityGenerationTest.class, // pass
	JPAFacetTest.class, // pass
	JPAUIPartsTest.class, // pass
	HQLEditorTest.class, // check 60, 61
	HQLEditorCodeAssistTest.class, // 60, 61
	JBossDatasourceTest.class, // pass 
	JpaAnnotationGenerationTest.class, // 60, 61 FAIL - https://issues.redhat.com/browse/JBIDE-28835
	MappingDiagramTest.class, // pass
	MappingFileTest.class, // FAIL - https://issues.redhat.com/browse/JBIDE-28833
	PersistenceXMLFileTest.class, // pass
	RevengFileTest.class, // pass
	TablesFromJPAEntitiesGeneration.class // first three works only	

})
public class HibernateAllTest {

}
