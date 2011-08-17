package org.jboss.tools.hibernate.ui.bot.testcase;

import org.jboss.tools.hibernate.ui.bot.testsuite.HibernateTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.DB;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.gen.ActionItem.Perspective;
import org.jboss.tools.ui.bot.ext.gen.ActionItem.View.HibernateHibernateConfigurations;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Basic Hibernate tests
 */
@Require( db=@DB,perspective="Hibernate")
public class BasicHibernateTest extends HibernateTest {
	@BeforeClass
	/**
	 * Setup prerequisites for this test
	 */
	public static void setUpTest() {

		eclipse.maximizeActiveShell();
		eclipse.closeView(IDELabel.View.WELCOME);
		
		prepareProject();
		util.waitForNonIgnoredJobs();
	}
	
	@Test
	public void testItI() {
		open.viewOpen(HibernateHibernateConfigurations.LABEL);
		open.perspective(Perspective.HIBERNATE.LABEL);
	}
}
