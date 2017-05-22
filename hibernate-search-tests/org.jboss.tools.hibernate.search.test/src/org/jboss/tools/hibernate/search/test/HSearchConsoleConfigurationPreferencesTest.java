package org.jboss.tools.hibernate.search.test;

import static org.junit.Assert.assertEquals;

import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.console.preferences.StandAloneConsoleConfigurationPreferences;
import org.jboss.tools.hibernate.search.HSearchConsoleConfigurationPreferences;
import org.junit.Test;

public class HSearchConsoleConfigurationPreferencesTest {

	@Test
	public void testGetHSearchVersion() {
		ConsoleConfigurationPreferences prefs = new StandAloneConsoleConfigurationPreferences("my-console-config", "4.3") {};
		ConsoleConfiguration consoleConfiguration = new ConsoleConfiguration(prefs);
		KnownConfigurations.getInstance().addConfiguration(consoleConfiguration, false);
		assertEquals(HSearchConsoleConfigurationPreferences.getHSearchVersion("my-console-config"), "5.3");
	}
}
