package org.hibernate.eclipse.console.test.mappingproject;

import junit.framework.TestSuite;

/**
 * @author Dmitry Geraskov
 *
 */
public class TestSetBeforeTearDown {
	public static TestSuite getTests(){
		TestSuite suite = new TestSuite("Test for MappingTestProject" );
		suite.addTestSuite( HibernateNatureRemoveTest.class );
		
		return suite;
	}
}
