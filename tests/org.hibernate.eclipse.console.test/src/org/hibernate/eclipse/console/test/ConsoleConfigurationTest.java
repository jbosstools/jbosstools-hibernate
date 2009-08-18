package org.hibernate.eclipse.console.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.hibernate.SessionFactory;
import org.hibernate.console.ConcoleConfigurationAdapter;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryPage;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.osgi.framework.Bundle;
import org.w3c.dom.Element;

public class ConsoleConfigurationTest extends TestCase {

	public static final String HIBERNATE_CFG_XML_PATH = "/res/project/src/hibernate.cfg.xml".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$

	private ConsoleConfiguration consoleCfg;

	public ConsoleConfigurationTest(String name) {
		super( name );
	}

	protected void setUp() throws Exception {
		super.setUp();

		TestConsoleConfigurationPreferences cfgprefs = new TestConsoleConfigurationPreferences();
		consoleCfg = new ConsoleConfiguration(cfgprefs);
		KnownConfigurations.getInstance().addConfiguration(consoleCfg, true);
	}

	protected void tearDown() throws Exception {
		KnownConfigurations.getInstance().removeAllConfigurations();
		consoleCfg = null;
	}

	static class TestConsoleConfigurationPreferences implements ConsoleConfigurationPreferences {

		public void setName(String name) {
			fail();
		}

		public void readStateFrom(Element element) {
			fail();
		}

		public void writeStateTo(Element node) {
			fail();
		}

		public File getPropertyFile() {
			return null;
		}

		public File getConfigXMLFile() {
			File xmlConfig = null;
			Bundle bundle = HibernateConsoleTestPlugin.getDefault().getBundle();
			try {
				URL url = FileLocator.resolve(bundle.getEntry(HIBERNATE_CFG_XML_PATH));
				xmlConfig = new File(url.getFile());
			} catch (IOException e) {
				fail("Cannot find file: " + HIBERNATE_CFG_XML_PATH); //$NON-NLS-1$
			}
			return xmlConfig;
		}

		public Properties getProperties() {
			Properties p = new Properties();
			p.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect"); //$NON-NLS-1$ //$NON-NLS-2$
			return p;
		}

		public File[] getMappingFiles() {
			return new File[0];
		}

		public URL[] getCustomClassPathURLS() {
			return new URL[0];
		}

		public String getName() {
			return ConsoleTestMessages.ConsoleConfigurationTest_fake_prefs;
		}



		public String getEntityResolverName() {
			return ""; //$NON-NLS-1$
		}

		public ConfigurationMode getConfigurationMode() {
			return ConfigurationMode.CORE;
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

		/* (non-Javadoc)
		 * @see org.hibernate.console.preferences.ConsoleConfigurationPreferences#getDialectName()
		 */
		public String getDialectName() {
			// TODO Auto-generated method stub
			return null;
		}

	}


	static class MockCCListener extends ConcoleConfigurationAdapter {
		int factoryBuilt = 0;
		int factoryClosing = 0;
		public int queryCreated;

		public void sessionFactoryClosing(ConsoleConfiguration configuration,
				SessionFactory aboutToCloseFactory) {
			factoryClosing++;
		}

		public void sessionFactoryBuilt(ConsoleConfiguration ccfg,
				SessionFactory builtSessionFactory) {
			factoryBuilt++;
		}

		public void queryPageCreated(QueryPage qp) {
			queryCreated++;
		}
	}

	public void testBuildConfiguration() {

		MockCCListener listener = new MockCCListener();
		assertTrue(consoleCfg.getConsoleConfigurationListeners().length==1);
		consoleCfg.addConsoleConfigurationListener(listener);

		consoleCfg.build();
		
		assertEquals(0, listener.factoryBuilt);
		consoleCfg.buildSessionFactory();
		assertEquals(1, listener.factoryBuilt);

		try {
			consoleCfg.buildSessionFactory();
			fail(ConsoleTestMessages.ConsoleConfigurationTest_factory_already_exists);
		} catch (HibernateConsoleRuntimeException hcre) {

		}

		QueryPage qp = consoleCfg.executeHQLQuery("from java.lang.Object"); //$NON-NLS-1$
		assertNotNull(qp);
		assertEquals(1, listener.queryCreated);

		consoleCfg.closeSessionFactory();
		assertEquals(1, listener.factoryClosing);


	}

	/*public void testCleanup() throws InterruptedException {

		for(int cnt=0;cnt<10000;cnt++) {
			if(cnt%2==0) {

				System.out.println("Cnt " + cnt + " " + Runtime.getRuntime().freeMemory()/1000);
				Thread.sleep( 2000 );
			}

			consoleCfg.build();
			consoleCfg.buildSessionFactory();
			consoleCfg.reset();
		}

	}*/
}
