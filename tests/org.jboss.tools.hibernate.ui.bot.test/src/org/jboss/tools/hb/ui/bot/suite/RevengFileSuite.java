package org.jboss.tools.hb.ui.bot.suite;

import org.jboss.tools.hb.ui.bot.test.configuration.CreateConfigurationFileTest;
import org.jboss.tools.hb.ui.bot.test.configuration.EditConfigurationFileTest;
import org.jboss.tools.hb.ui.bot.test.reveng.CreateRevengFileTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(RequirementAwareSuite.class)
@SuiteClasses({CreateRevengFileTest.class})
public class RevengFileSuite {

}
