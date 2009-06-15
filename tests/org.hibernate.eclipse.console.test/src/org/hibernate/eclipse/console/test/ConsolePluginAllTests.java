package org.hibernate.eclipse.console.test;

import java.io.IOException;

import org.hibernate.eclipse.console.test.mappingproject.MappingTestsCore;
import org.hibernate.eclipse.console.test.mappingproject.MappingTestsJpa;
import org.hibernate.eclipse.console.views.test.QueryPageViewerTest;
import org.hibernate.eclipse.hqleditor.preferences.HQLEditorPreferencePageTest;
import org.hibernate.eclipse.mapper.HBMInfoExtractorTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ConsolePluginAllTests {

	public static Test suite() throws IOException {
		TestSuite suite = new TestSuite(
				ConsoleTestMessages.ConsolePluginAllTests_test_for );

		suite.addTestSuite(KnownConfigurationsTest.class);
		suite.addTestSuite(QueryParametersTest.class);
		suite.addTestSuite(PerspectiveTest.class);
		suite.addTestSuite(ConsoleConfigurationTest.class);
		suite.addTestSuite(JavaFormattingTest.class);
		suite.addTestSuite(RefactoringTest.class);
		
		suite.addTestSuite(MappingTestsCore.class);
		suite.addTestSuite(MappingTestsJpa.class);
		suite.addTestSuite(HQLEditorPreferencePageTest.class);
		
		suite.addTestSuite(QueryPageViewerTest.class);
		suite.addTestSuite(HBMInfoExtractorTest.class);

		// core tests
		//Properties properties = new Properties();
		//properties.load(ConsolePluginAllTests.class.getResourceAsStream("plugintest-hibernate.properties"));
		
		//System.getProperties().putAll(properties);
		
		//suite.addTest(org.hibernate.tool.ToolAllTests.suite() );
		

		return suite;
	}

}
