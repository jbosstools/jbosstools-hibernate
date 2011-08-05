package org.hibernate.eclipse.console.test;

import java.lang.reflect.Field;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Mappings;
import org.hibernate.console.ConcoleConfigurationAdapter;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ConsoleQueryParameter;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryInputModel;
import org.hibernate.console.QueryPage;
import org.hibernate.eclipse.console.test.launchcfg.TestConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.views.QueryPageTabView;
import org.hibernate.impl.AbstractQueryImpl;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;

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
	
	public void testHQLListParameters() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		//fix for https://issues.jboss.org/browse/JBIDE-9392
		//the view calls jdbc connection
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = activePage.findView(QueryPageTabView.ID);
		if (view != null){
			activePage.hideView(view);
			view.dispose();
		}
		
		consoleCfg.build();
		Configuration c = consoleCfg.getConfiguration();
		Mappings mappings = c.createMappings();
		RootClass rc = new RootClass();
		rc.setEntityName("java.awt.Button");
		rc.setClassName( "java.awt.Button" );
		Column column = new Column("label");
		PrimaryKey pk = new PrimaryKey();
		pk.addColumn(column);
		Table table = new Table("faketable");
		rc.setTable(table);
		table.addColumn(column);
		table.setPrimaryKey(pk);
		Property fakeProp = new Property();
		fakeProp.setName("label");
		SimpleValue sv = new SimpleValue();
		sv.addColumn(column);
		sv.setTypeName("string");
		sv.setTable(table);
		fakeProp.setValue(sv);
		rc.setIdentifierProperty(fakeProp);
		rc.setIdentifier((KeyValue) fakeProp.getValue());
		mappings.addClass(rc);

		consoleCfg.buildSessionFactory();
		
		ConsoleQueryParameter paramA = new ConsoleQueryParameter("a", Hibernate.INTEGER,
				new Integer[]{new Integer(1), new Integer(2)});
		ConsoleQueryParameter paramB = new ConsoleQueryParameter("b", Hibernate.INTEGER, new Integer(3));
		ConsoleQueryParameter paramOrdered = new ConsoleQueryParameter("0", Hibernate.INTEGER, new Integer(4));
		QueryInputModel model = new QueryInputModel();
		model.addParameter(paramA);
		model.addParameter(paramB);
		model.addParameter(paramOrdered);
		
		//activePage.hideView(view);
		
		QueryPage qp = consoleCfg.executeHQLQuery("select count(*) from java.awt.Button where 1 in ( ?, :a, :b )", model); //$NON-NLS-1$
		assertNotNull(qp);
		try{
			qp.getList();//execute the query
		} catch (Exception e){
			//ignore - there is fake mapping
		}
		Field qField = qp.getClass().getDeclaredField("query");
		qField.setAccessible(true);
		Query query = (Query) qField.get(qp);
		assertTrue(query.getNamedParameters().length == 2); // a and b
		Field listParam = AbstractQueryImpl.class.getDeclaredField("namedParameterLists");
		listParam.setAccessible(true);
		Map namedParameterLists = (Map) listParam.get(query);
		assertTrue(namedParameterLists.size() == 1);//"a" added as a list parameter
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
