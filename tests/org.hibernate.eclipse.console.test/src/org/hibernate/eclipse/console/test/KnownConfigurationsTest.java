package org.hibernate.eclipse.console.test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.hibernate.SessionFactory;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.KnownConfigurationsAdapter;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.w3c.dom.Element;

public class KnownConfigurationsTest extends TestCase {

	public KnownConfigurationsTest(String name) {
		super( name );
	}

	static class CCListener extends KnownConfigurationsAdapter {

		List<ConsoleConfiguration> added = new ArrayList<ConsoleConfiguration>();

		public void configurationAdded(ConsoleConfiguration root) {
			added.add(root);
		}

		public void sessionFactoryBuilt(ConsoleConfiguration ccfg, SessionFactory builtFactory) {
			fail(ConsoleTestMessages.KnownConfigurationsTest_no_sf_should_be_build);
		}

		public void sessionFactoryClosing(ConsoleConfiguration configuration, SessionFactory closingFactory) {
			fail(ConsoleTestMessages.KnownConfigurationsTest_no_sf_should_be_closed);
		}

		public void configurationRemoved(ConsoleConfiguration root, boolean forUpdate) {
			if(!added.remove(root)) {
				fail(ConsoleTestMessages.KnownConfigurationsTest_trying_remove_non_existing_console);
			}
		}
	}

	public void testKnownConfigurations() {

		KnownConfigurations knownConfigurations = KnownConfigurations.getInstance();
		ConsoleConfiguration[] configurations = knownConfigurations.getConfigurations();

		assertEquals(0, configurations.length);


		CCListener listener = new CCListener();
		try {
		knownConfigurations.addConsoleConfigurationListener(listener);

		assertEquals(0, listener.added.size());

		ConsoleConfigurationPreferences preferences = new ConsoleConfigurationPreferences() {

			public void setName(String name) {
				// TODO Auto-generated method stub

			}

			public void readStateFrom(Element element) {
				// TODO Auto-generated method stub

			}

			public void writeStateTo(Element node) {
				// TODO Auto-generated method stub

			}

			public File getPropertyFile() {
				// TODO Auto-generated method stub
				return null;
			}

			public File getConfigXMLFile() {
				// TODO Auto-generated method stub
				return null;
			}

			public Properties getProperties() {
				// TODO Auto-generated method stub
				return null;
			}

			public File[] getMappingFiles() {
				// TODO Auto-generated method stub
				return null;
			}

			public URL[] getCustomClassPathURLS() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getName() {
				return ConsoleTestMessages.KnownConfigurationsTest_fake_prefs;
			}

			public String getEntityResolverName() {
				// TODO Auto-generated method stub
				return null;
			}

			public ConfigurationMode getConfigurationMode() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getNamingStrategy() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getPersistenceUnitName() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getConnectionProfileName() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getDialectName() {
				// TODO Auto-generated method stub
				return null;
			}

		};

		ConsoleConfigurationPreferences preferences2 = new ConsoleConfigurationPreferences() {

			String name = ConsoleTestMessages.KnownConfigurationsTest_new_test;

			public void setName(String name) {
				this.name = name;
			}

			public void readStateFrom(Element element) {
				// TODO Auto-generated method stub

			}

			public void writeStateTo(Element node) {
				// TODO Auto-generated method stub

			}

			public File getPropertyFile() {
				// TODO Auto-generated method stub
				return null;
			}

			public File getConfigXMLFile() {
				// TODO Auto-generated method stub
				return null;
			}

			public Properties getProperties() {
				// TODO Auto-generated method stub
				return null;
			}

			public File[] getMappingFiles() {
				// TODO Auto-generated method stub
				return null;
			}

			public URL[] getCustomClassPathURLS() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getName() {
				return name;
			}

			public String getEntityResolverName() {
				// TODO Auto-generated method stub
				return null;
			}

			public ConfigurationMode getConfigurationMode() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getNamingStrategy() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getPersistenceUnitName() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getConnectionProfileName() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getDialectName() {
				// TODO Auto-generated method stub
				return null;
			}

		};

		ConsoleConfiguration configuration = new ConsoleConfiguration(preferences);
		ConsoleConfiguration configuration2 = new ConsoleConfiguration(preferences2);

		knownConfigurations.addConfiguration(configuration, false);
		knownConfigurations.addConfiguration(configuration2, false);

		configurations = knownConfigurations.getConfigurations();
		assertEquals(2,configurations.length);
		assertEquals(listener.added.size(), 0);

		knownConfigurations.addConfiguration(configuration, true);
		knownConfigurations.addConfiguration(configuration2, true);

		configurations = knownConfigurations.getConfigurations();
		assertEquals(2,configurations.length);
		assertEquals(listener.added.size(), 2);

		knownConfigurations.removeConfiguration(configuration, false);
		knownConfigurations.removeConfiguration(configuration2, false);

		configurations = knownConfigurations.getConfigurations();
		assertEquals(0,configurations.length);
		assertEquals(listener.added.size(), 0);
		} finally {
			KnownConfigurations.getInstance().removeConfigurationListener(listener);
		}
	}

}
