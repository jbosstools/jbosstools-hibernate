package org.hibernate.eclipse.jdt.ui.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JDTuiAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.hibernate.eclipse.jdt.ui.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(HQLQueryValidatorTest.class);
		suite.addTestSuite(ELTransformerTest.class);
		//$JUnit-END$
		return suite;
	}

}
