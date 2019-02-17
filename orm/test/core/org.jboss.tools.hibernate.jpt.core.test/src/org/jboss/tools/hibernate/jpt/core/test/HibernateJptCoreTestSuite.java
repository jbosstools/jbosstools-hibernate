package org.jboss.tools.hibernate.jpt.core.test;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

public class HibernateJptCoreTestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new JUnit4TestAdapter(HibernateJpaModelTests.class));
		suite.addTest(new JUnit4TestAdapter(HibernateJpaOrmModelTests.class));
		
		return suite;
	}
}
