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
		//suite.addTestSuite( UpdateConfigurationTest.class );
	}
	
	private static void addPackTests(TestSuite suite){
		
	}
	
	private static void addTestsPackTearDown(TestSuite suite){
		//suite.addTestSuite( CloaseAllEditorsTest.class );
	}

}
