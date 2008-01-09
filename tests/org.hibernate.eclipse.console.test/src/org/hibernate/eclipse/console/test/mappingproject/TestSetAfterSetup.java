package org.hibernate.eclipse.console.test.mappingproject;

import junit.framework.TestSuite;

/**
 * @author Dmitry Geraskov
 *
 */
public class TestSetAfterSetup {
	public static TestSuite getTests(){
		TestSuite suite = new TestSuite("Test for MappingTestProject" );
		//suite.addTestSuite( HibernateNatureAddTest.class );
		//suite.addTestSuite( CreateConsoleConfigTest.class );
		return suite;
	}
}
