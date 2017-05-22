package org.jboss.tools.hibernate.search.test;

import org.jboss.tools.hibernate.search.test.property.testers.HibernateSearchEnabledPropertyTesterTest;
import org.jboss.tools.hibernate.search.test.property.testers.OneParentConfigPropertyTesterTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	HSearchConsoleConfigurationPreferencesTest.class,
	HibernateSearchEnabledPropertyTesterTest.class,
	OneParentConfigPropertyTesterTest.class
})
public class AllTestsSuite {
}
