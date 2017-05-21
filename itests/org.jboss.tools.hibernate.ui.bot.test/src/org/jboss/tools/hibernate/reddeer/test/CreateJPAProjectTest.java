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

import java.util.Arrays;
import java.util.Collection;

import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.jboss.reddeer.requirements.db.DatabaseRequirement.Database;
import org.jboss.tools.hibernate.ui.bot.test.factory.JPAProjectFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Create JPA Project test
 * 
 * @author Jiri Peterka
 * 
 */
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
@Database(name="testdb")
public class CreateJPAProjectTest extends HibernateRedDeerTest {	
	
	@Parameter
	public String prj; 
	@Parameter(1)
	public String jpaVersion;
	@Parameter(2)
	public String hbVersion;
	
	@Parameters(name="jpa {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{"jpa10test","1.0","Hibernate (JPA 1.x)"},
        		{"jpa20test","2.0","Hibernate (JPA 2.0)"},
        		{"jpa21test","2.1","Hibernate (JPA 2.1)"},
           });
    }
	
	@Before 
	public void before() {
		CleanWorkspaceRequirement req = new CleanWorkspaceRequirement();
		req.fulfill();
	}

	@Test
	public void createJPAProject10() {
		JPAProjectFactory.createProject(prj, jpaVersion, hbVersion);
	}
}
