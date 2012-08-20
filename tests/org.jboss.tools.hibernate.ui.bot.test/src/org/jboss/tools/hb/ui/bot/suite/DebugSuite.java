package org.jboss.tools.hb.ui.bot.suite;

import org.jboss.tools.hb.ui.bot.test.view.JPADetailViewTest;
import org.jboss.tools.hb.ui.bot.test.view.PackageInfoTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Suite of tests for to be executed on jenkins slave
 * Same test from Hibernate suite are not here because of known issues
 * @author jpeterka
 *
 */
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({
	JPADetailViewTest.class,
	PackageInfoTest.class
 	})
public class DebugSuite {

}
