package org.jboss.tools.hb.ui.bot.suite;

import org.jboss.tools.hb.ui.bot.test.jpa.CreateJPAEntityTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(RequirementAwareSuite.class)
//@SuiteClasses({CreateJPAProjectTest.class})
//@SuiteClasses({EditPersistenceXMLTest.class})
@SuiteClasses({CreateJPAEntityTest.class})
public class JPASuite {

}
