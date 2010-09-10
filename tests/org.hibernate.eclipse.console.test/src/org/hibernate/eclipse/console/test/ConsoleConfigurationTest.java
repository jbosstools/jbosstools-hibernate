package org.hibernate.eclipse.console.test;

import junit.framework.TestCase;

import org.hibernate.SessionFactory;
import org.hibernate.console.ConcoleConfigurationAdapter;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryPage;
import org.hibernate.eclipse.console.test.launchcfg.TestConsoleConfigurationPreferences;

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
		consoleCfg = null;
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

		public void configurationReset(ConsoleConfiguration ccfg) {
			// TODO Auto-generated method stub
			
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
	
	public void testHQLComments() {
		consoleCfg.build();
		consoleCfg.buildSessionFactory();

		try {
			consoleCfg.buildSessionFactory();
			fail(ConsoleTestMessages.ConsoleConfigurationTest_factory_already_exists);
		} catch (HibernateConsoleRuntimeException hcre) {

		}

		QueryPage qp = consoleCfg.executeHQLQuery("from java.lang.Object --this is my comment"); //$NON-NLS-1$
		assertNotNull(qp);
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
