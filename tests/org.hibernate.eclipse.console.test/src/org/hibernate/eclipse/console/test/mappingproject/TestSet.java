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

import junit.framework.TestSuite;

/**
 * @author Dmitry Geraskov
 *
 */
public class TestSet{

	/** 
	 * use only addTestSuit to prevent errors!!!
	 * @return
	 */
	public static TestSuite getTests(){
		TestSuite suite = new TestSuite("Test for MappingTestProject" );
	
		addTestsPackSetUp( suite );
		addPackTests( suite );
		addTestsPackTearDown( suite );		
		
		return suite;
	}
	
	
	private static void addTestsPackSetUp(TestSuite suite){		
		suite.addTestSuite( UpdateConfigurationTest.class );
	}
	
	private static void addPackTests(TestSuite suite){
		suite.addTestSuite( OpenSourceFileTest.class );
		suite.addTestSuite( OpenMappingFileTest.class );
		suite.addTestSuite( OpenMappingDiagramTest.class );
	}
	
	private static void addTestsPackTearDown(TestSuite suite){
		suite.addTestSuite( CloaseAllEditorsTest.class );
	}

}
