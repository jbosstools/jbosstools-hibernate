package org.hibernate.eclipse.console.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ConsolePluginAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.hibernate.eclipse.console.test" );
		//$JUnit-BEGIN$
		suite.addTestSuite( JavaFormattingTest.class );
		suite.addTestSuite( KnownConfigurationsTest.class );
		suite.addTestSuite( ConsoleConfigurationTest.class );
		suite.addTestSuite( PluginTest.class );
		suite.addTestSuite( QueryParametersTest.class );
		//$JUnit-END$
		return suite;
	}

}
