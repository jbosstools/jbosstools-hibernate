package org.hibernate.eclipse.console.test;

import java.io.IOException;

import org.junit.Assert;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

public class ConsolePluginAllTests {

	public static Test suite() throws IOException {
		TestSuite suite = new TestSuite(
				ConsoleTestMessages.ConsolePluginAllTests_test_for );
		suite.addTest(new JUnit4TestAdapter(DummyTest.class));
		return suite;
	}
	
	public static class DummyTest {
		@org.junit.Test
		public void test() {
			Assert.assertTrue(true);
		}
	}
	
}
