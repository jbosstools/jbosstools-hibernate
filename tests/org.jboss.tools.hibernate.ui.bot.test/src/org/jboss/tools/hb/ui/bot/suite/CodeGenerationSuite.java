package org.jboss.tools.hb.ui.bot.suite;

import org.jboss.tools.hb.ui.bot.test.generation.RunSchemaExportTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(RequirementAwareSuite.class)
//@SuiteClasses({CreateCodeGenerationConfiguration.class})
//@SuiteClasses({JPADDLGenerationTest.class})
//@SuiteClasses({JPAEntitiesGenerationTest.class})
@SuiteClasses({RunSchemaExportTest.class})
//@SuiteClasses({CreateCodeGenerationConfiguration.class,JPADDLGenerationTest.class,JPAEntitiesGenerationTest.class})
public class CodeGenerationSuite {

}
