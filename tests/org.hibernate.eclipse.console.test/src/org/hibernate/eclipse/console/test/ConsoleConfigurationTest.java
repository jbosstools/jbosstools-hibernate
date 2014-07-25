package org.hibernate.eclipse.console.test;

import junit.framework.TestCase;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.hibernate.console.ConcoleConfigurationAdapter;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ConsoleQueryParameter;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryInputModel;
import org.hibernate.console.QueryPage;
import org.hibernate.eclipse.console.test.launchcfg.TestConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.views.QueryPageTabView;
import org.jboss.tools.hibernate.spi.IColumn;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IMappings;
import org.jboss.tools.hibernate.spi.IPersistentClass;
import org.jboss.tools.hibernate.spi.IPrimaryKey;
import org.jboss.tools.hibernate.spi.IProperty;
import org.jboss.tools.hibernate.spi.IService;
import org.jboss.tools.hibernate.spi.ISessionFactory;
import org.jboss.tools.hibernate.spi.ITable;
import org.jboss.tools.hibernate.spi.ITypeFactory;
import org.jboss.tools.hibernate.spi.IValue;

public class ConsoleConfigurationTest extends TestCase {

	private ConsoleConfiguration consoleCfg;
	private IService service;
	private ITypeFactory typeFactory;

	public ConsoleConfigurationTest(String name) {
		super( name );
	}

	protected void setUp() throws Exception {
		super.setUp();
		typeFactory = service.newTypeFactory();
		TestConsoleConfigurationPreferences cfgprefs = new TestConsoleConfigurationPreferences();
		consoleCfg = new ConsoleConfiguration(cfgprefs);
		service = consoleCfg.getHibernateExtension().getHibernateService();
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
				ISessionFactory aboutToCloseFactory) {
			factoryClosing++;
		}

		public void sessionFactoryBuilt(ConsoleConfiguration ccfg,
				ISessionFactory builtSessionFactory) {
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
		IConfiguration c = consoleCfg.getConfiguration();
		IMappings mappings = c.createMappings();
		IPersistentClass rc = service.newRootClass();
		rc.setEntityName("java.awt.Button");
		rc.setClassName( "java.awt.Button" );
		IColumn column = service.newColumn("label");
		IPrimaryKey pk = service.newPrimaryKey();
		pk.addColumn(column);
		ITable table = service.newTable("faketable");
		rc.setTable(table);
		table.addColumn(column);
		table.setPrimaryKey(pk);
		IProperty fakeProp = service.newProperty();
		fakeProp.setName("label");
		IValue sv = service.newSimpleValue();
		sv.addColumn(column);
		sv.setTypeName("string");
		sv.setTable(table);
		fakeProp.setValue(sv);
		rc.setIdentifierProperty(fakeProp);
		rc.setIdentifier(fakeProp.getValue());
		mappings.addClass(rc);

		consoleCfg.buildSessionFactory();
		
		ConsoleQueryParameter paramA = new ConsoleQueryParameter(service, "a", typeFactory.getIntegerType(),
				new Integer[]{new Integer(1), new Integer(2)});
		ConsoleQueryParameter paramB = new ConsoleQueryParameter(service, "b", typeFactory.getIntegerType(), new Integer(3));
		ConsoleQueryParameter paramOrdered = new ConsoleQueryParameter(service, "0", typeFactory.getIntegerType(), new Integer(4));
		QueryInputModel model = new QueryInputModel(service);
		model.addParameter(paramA);
		model.addParameter(paramB);
		model.addParameter(paramOrdered);
		
		QueryPage qp = consoleCfg.executeHQLQuery("select count(*) from java.awt.Button where 1 in ( ?, :a, :b )", model); //$NON-NLS-1$
		assertNotNull(qp);
		try{
			qp.getList();//execute the query
		} catch (Exception e){
			//ignore - there is fake mapping
		}
	}


}
