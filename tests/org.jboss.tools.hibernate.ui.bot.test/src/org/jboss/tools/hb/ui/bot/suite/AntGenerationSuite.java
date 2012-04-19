package org.jboss.tools.hb.ui.bot.suite;

import org.jboss.tools.hb.ui.bot.test.ant.ExportAntCodeGenerationTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ExportAntCodeGenerationTest.class})
public class AntGenerationSuite {

}
