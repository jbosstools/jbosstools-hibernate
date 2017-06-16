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
import org.jboss.tools.hibernate.reddeer.test.CodeGenerationConfigurationTest;
import org.jboss.tools.hibernate.reddeer.test.ConnectionProfileTest;
import org.jboss.tools.hibernate.reddeer.test.ConsoleConfigurationFileTest;
import org.jboss.tools.hibernate.reddeer.test.ConsoleConfigurationTest;
import org.jboss.tools.hibernate.reddeer.test.CreateJPAProjectTest;
import org.jboss.tools.hibernate.reddeer.test.CriteriaEditorTest;
import org.jboss.tools.hibernate.reddeer.test.HQLEditorTest;
import org.jboss.tools.hibernate.reddeer.test.HibernateUIPartsTest;
import org.jboss.tools.hibernate.reddeer.test.JPAEntityGenerationTest;
import org.jboss.tools.hibernate.reddeer.test.JPAFacetTest;
import org.jboss.tools.hibernate.reddeer.test.JPAUIPartsTest;
import org.jboss.tools.hibernate.reddeer.test.JpaAnnotationGenerationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(RedDeerSuite.class)
@Suite.SuiteClasses({

	CodeGenerationConfigurationTest.class,
	ConnectionProfileTest.class,
	ConsoleConfigurationFileTest.class,
	ConsoleConfigurationTest.class,
	CreateJPAProjectTest.class,
	CriteriaEditorTest.class,
	HibernateUIPartsTest.class,
	HQLEditorTest.class,
	JpaAnnotationGenerationTest.class,
	JPAEntityGenerationTest.class,
	JPAFacetTest.class,
	JPAUIPartsTest.class
})
public class SmokeSuite {

}
