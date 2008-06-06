/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.hibernate.eclipse.console.test.mappingproject;

import org.hibernate.eclipse.console.test.ConsoleTestMessages;

import junit.framework.TestSuite;

/**
 * @author Dmitry Geraskov
 *
 */
public class TestSetAfterSetup {
	public static TestSuite getTests(){
		TestSuite suite = new TestSuite(ConsoleTestMessages.TestSet_test_for_mappingtestproject);
		suite.addTestSuite( HibernateNatureAddTest.class );
		//suite.addTestSuite( CreateConsoleConfigTest.class );
		return suite;
	}
}
