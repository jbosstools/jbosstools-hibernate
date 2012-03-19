package org.jboss.tools.hb.ui.bot.suite;

import org.jboss.tools.hb.ui.bot.test.configuration.CreateConfigurationFileTest;
import org.jboss.tools.hb.ui.bot.test.configuration.EditConfigurationFileTest;
import org.jboss.tools.hb.ui.bot.test.console.CreateConsoleConfigurationTest;
import org.jboss.tools.hb.ui.bot.test.perspective.PerspectiveTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(RequirementAwareSuite.class)
@SuiteClasses({
	CreateConfigurationFileTest.class, 
	EditConfigurationFileTest.class,
	PerspectiveTest.class,
	CreateConsoleConfigurationTest.class
	})
public class HibernateSuite {

}
