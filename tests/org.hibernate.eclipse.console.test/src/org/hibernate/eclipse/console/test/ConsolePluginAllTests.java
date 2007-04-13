package org.hibernate.eclipse.console.test;

import java.io.IOException;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ConsolePluginAllTests {

	public static Test suite() throws IOException {
		TestSuite suite = new TestSuite(
				"Test for org.hibernate.eclipse.console.test" );
		
		suite.addTestSuite( KnownConfigurationsTest.class );
		suite.addTestSuite( QueryParametersTest.class );
		suite.addTestSuite( PerspectiveTest.class );
		suite.addTestSuite( ConsoleConfigurationTest.class );
		suite.addTestSuite( JavaFormattingTest.class );
		suite.addTestSuite( HibernateProjectTests.class );

		// core tests
		//Properties properties = new Properties();
		//properties.load(ConsolePluginAllTests.class.getResourceAsStream("plugintest-hibernate.properties"));
		
		//System.getProperties().putAll(properties);
		
		//suite.addTest(org.hibernate.tool.ToolAllTests.suite() );
		

		return suite;
	}

}
