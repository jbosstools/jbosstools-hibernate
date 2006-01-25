package org.hibernate.eclipse.console.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ConsolePluginAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.hibernate.eclipse.console.test" );
		//$JUnit-BEGIN$
		suite.addTestSuite( KnownConfigurationsTest.class );
		suite.addTestSuite( QueryParametersTest.class );
		suite.addTestSuite( PluginTest.class );
		suite.addTestSuite( PerspectiveTest.class );
		suite.addTestSuite( ConsoleConfigurationTest.class );
		suite.addTestSuite( JavaFormattingTest.class );
		suite.addTestSuite( HibernateProjectTests.class );
		//$JUnit-END$
		return suite;
	}

}
