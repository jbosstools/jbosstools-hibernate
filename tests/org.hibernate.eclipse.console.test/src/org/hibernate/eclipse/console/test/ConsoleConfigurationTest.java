package org.hibernate.eclipse.console.test;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import junit.framework.TestCase;

import org.hibernate.SessionFactory;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ConsoleConfigurationListener;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryPage;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.w3c.dom.Element;

public class ConsoleConfigurationTest extends TestCase {

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
			return null;
		}
	
		public Properties getProperties() {
			Properties p = new Properties();
			p.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
			return p;
		}
	
		public File[] getMappingFiles() {
			return new File[0];
		}
	
		public URL[] getCustomClassPathURLS() {
			return new URL[0];
		}
	
		public String getName() {
			return "fake prefs";
		}
	
		

		public String getEntityResolverName() {			
			return "";
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
	
	}
	
	
	static class MockCCListener implements ConsoleConfigurationListener {
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
			fail("Should throw an exception because a factory already exists!");
		} catch (HibernateConsoleRuntimeException hcre) {
			
		}
		
		QueryPage qp = consoleCfg.executeHQLQuery("from java.lang.Object");
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
