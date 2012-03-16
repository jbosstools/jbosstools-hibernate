package org.jboss.tools.hb.ui.bot.suite;

import org.jboss.tools.hb.ui.bot.test.configuration.CreateConfigurationFileTest;
import org.jboss.tools.hb.ui.bot.test.configuration.EditConfigurationFileTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(RequirementAwareSuite.class)
//@SuiteClasses({CreateConfigurationFileTest.class})
//@SuiteClasses({EditConfigurationFileTest.class})
@SuiteClasses({CreateConfigurationFileTest.class, EditConfigurationFileTest.class})
public class ConfigurationFileSuite {

}
