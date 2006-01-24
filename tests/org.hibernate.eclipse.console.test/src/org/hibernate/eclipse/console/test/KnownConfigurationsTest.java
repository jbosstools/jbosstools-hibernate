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
import org.hibernate.console.KnownConfigurationsListener;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.w3c.dom.Element;

public class KnownConfigurationsTest extends TestCase {

	public KnownConfigurationsTest(String name) {
		super( name );
	}
	
	static class CCListener implements KnownConfigurationsListener {

		List added = new ArrayList();
		
		public void configurationAdded(ConsoleConfiguration root) {
			added.add(root);
		}

		public void sessionFactoryBuilt(ConsoleConfiguration ccfg, SessionFactory builtFactory) {
			fail("no sf should be built!");
		}

		public void sessionFactoryClosing(ConsoleConfiguration configuration, SessionFactory closingFactory) {
			fail("no sf should be closed!");
		}

		public void configurationRemoved(ConsoleConfiguration root) {
			if(!added.remove(root)) {
				fail("trying to remove a non existing console");
			}
		}
	}
	
	public void testKnownConfigurations() {
		
		KnownConfigurations knownConfigurations = KnownConfigurations.getInstance();
		ConsoleConfiguration[] configurations = knownConfigurations.getConfigurations();
		
		assertEquals(0, configurations.length);
		
		
		CCListener listener = new CCListener();
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
				return "fake prefs";
			}
		
			public boolean useAnnotations() {
				// TODO Auto-generated method stub
				return false;
			}

			public String getEntityResolverName() {
				// TODO Auto-generated method stub
				return null;
			}
		
		};
		ConsoleConfiguration configuration = new ConsoleConfiguration(preferences);
		
		knownConfigurations.addConfiguration(configuration, false);
		
		configurations = knownConfigurations.getConfigurations();
		assertEquals(1,configurations.length);
		assertEquals(listener.added.size(), 0);
		
		knownConfigurations.addConfiguration(configuration, true);
		
		configurations = knownConfigurations.getConfigurations();
		assertEquals(1,configurations.length);		
		assertEquals(listener.added.size(), 1);
		
		knownConfigurations.removeConfiguration(configuration);
		
		configurations = knownConfigurations.getConfigurations();
		assertEquals(0,configurations.length);
		assertEquals(listener.added.size(), 0);
		
	}
	
}
