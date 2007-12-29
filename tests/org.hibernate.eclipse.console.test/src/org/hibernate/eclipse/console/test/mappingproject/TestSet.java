package org.hibernate.eclipse.console.test.mappingproject;

import junit.framework.TestSuite;

/**
 * @author Dmitry Geraskov
 *
 */
public class TestSet{

	public static TestSuite getTests(){
		TestSuite suite = new TestSuite("Test for MappingTestProject" );
		//use only addTestSuit to prevent errors
		suite.addTestSuite( OpenJavaEditors.class );
		suite.addTestSuite( HibernateNatureTest.class );
		
		return suite;
	}

}
