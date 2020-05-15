package org.hibernate.eclipse.jdt.ui.test;

import org.hibernate.eclipse.jdt.ui.test.hbmexporter.HbmExporterTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JDTuiAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.hibernate.eclipse.jdt.ui.test"); //$NON-NLS-1$
		//$JUnit-BEGIN$
		suite.addTestSuite(HQLQueryValidatorTest.class);
		suite.addTestSuite(HbmExporterTest.class);
		suite.addTestSuite(JPAMapTest.class);
		//$JUnit-END$
		return suite;
	}

}
