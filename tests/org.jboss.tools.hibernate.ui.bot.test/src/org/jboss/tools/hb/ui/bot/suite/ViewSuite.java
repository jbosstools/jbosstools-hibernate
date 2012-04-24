package org.jboss.tools.hb.ui.bot.suite;

import org.jboss.tools.hb.ui.bot.test.view.JPADetailViewTest;
import org.jboss.tools.hb.ui.bot.test.view.PackageInfoTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(RequirementAwareSuite.class)
//@SuiteClasses({JPADetailViewTest.class})
//@SuiteClasses({PackageInfoTest.class})
@SuiteClasses({JPADetailViewTest.class,PackageInfoTest.class})
public class ViewSuite {

}
