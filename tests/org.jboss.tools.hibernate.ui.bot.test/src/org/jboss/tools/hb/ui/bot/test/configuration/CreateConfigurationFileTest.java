package org.jboss.tools.hb.ui.bot.test.configuration;

import org.jboss.tools.hb.ui.bot.common.ConfigurationFile;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.junit.Test;

/**
 * Hibernate create configuration ui bot test
 * 
 * @author jpeterka
 * 
 */
@Require(clearProjects = true)
public class CreateConfigurationFileTest extends HibernateBaseTest {

	@Test
	public void configurationFileTest() {
		emptyErrorLog();
		importTestProject("/resources/prj/hibernate35");
		createHBConfiguration();
		openHBConfiguration();
		checkErrorLog();
	}

	private void createHBConfiguration() {
		ConfigurationFile.create(new String[]{"hibernate35", "cfg"}, "hibernate.cfg.xml",false);
	}

	private void openHBConfiguration() {
		ConfigurationFile.open("hibernate35", "cfg", "hibernate.cfg.xml");
	}

}