package org.jboss.tools.hb.ui.bot.suite;

import org.jboss.tools.hb.ui.bot.test.hql.HQLEditorTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(RequirementAwareSuite.class)
@SuiteClasses({HQLEditorTest.class})
public class HQLEditorSuite {

}
