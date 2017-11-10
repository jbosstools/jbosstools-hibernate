package org.jboss.tools.hibernate.orm.test;

import java.io.File;
import java.io.FileWriter;

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
import org.hibernate.eclipse.console.views.QueryPageTabView;
import org.jboss.tools.hibernate.orm.test.utils.TestConsoleConfigurationPreferences;
import org.jboss.tools.hibernate.orm.test.utils.TestConsoleMessages;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ConsoleConfigurationTest {

	private static final String HIBERNATE_CFG_XML = 
			"<!DOCTYPE hibernate-configuration PUBLIC                               " +
			"	'-//Hibernate/Hibernate Configuration DTD 3.0//EN'                  " +
			"	'http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd'> " +
			"                                                                       " +
			"<hibernate-configuration>                                              " +
			"	<session-factory/>                                                  " + 
			"</hibernate-configuration>                                             " ;		
		
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
		
	private File cfgXmlFile = null;
	private ConsoleConfiguration consoleCfg;
	private IService service;
	private ITypeFactory typeFactory;

	@Before
	public void setUp() throws Exception {
		cfgXmlFile = new File(temporaryFolder.getRoot(), "hibernate.cfg.xml");
		FileWriter fw = new FileWriter(cfgXmlFile);
		fw.write(HIBERNATE_CFG_XML);
		fw.close();
		TestConsoleConfigurationPreferences cfgprefs = 
				new TestConsoleConfigurationPreferences(cfgXmlFile);
		consoleCfg = new ConsoleConfiguration(cfgprefs);
		service = consoleCfg.getHibernateExtension().getHibernateService();
		typeFactory = service.newTypeFactory();
		KnownConfigurations.getInstance().addConfiguration(consoleCfg, true);
	}

	@After
	public void tearDown() throws Exception {
		KnownConfigurations.getInstance().removeAllConfigurations();
		consoleCfg = null;
		cfgXmlFile = null;
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

	@Test
	public void testBuildConfiguration() {

		MockCCListener listener = new MockCCListener();
		Assert.assertTrue(consoleCfg.getConsoleConfigurationListeners().length==1);
		consoleCfg.addConsoleConfigurationListener(listener);

		consoleCfg.build();
		
		Assert.assertEquals(0, listener.factoryBuilt);
		consoleCfg.buildSessionFactory();
		Assert.assertEquals(1, listener.factoryBuilt);

		try {
			consoleCfg.buildSessionFactory();
			Assert.fail(TestConsoleMessages.ConsoleConfigurationTest_factory_already_exists);
		} catch (HibernateConsoleRuntimeException hcre) {

		}

		QueryPage qp = consoleCfg.executeHQLQuery("from java.lang.Object"); //$NON-NLS-1$
		Assert.assertNotNull(qp);
		Assert.assertEquals(1, listener.queryCreated);

		consoleCfg.closeSessionFactory();
		Assert.assertEquals(1, listener.factoryClosing);


	}
	
	@Test
	public void testHQLComments() {
		consoleCfg.build();
		consoleCfg.buildSessionFactory();

		try {
			consoleCfg.buildSessionFactory();
			Assert.fail(TestConsoleMessages.ConsoleConfigurationTest_factory_already_exists);
		} catch (HibernateConsoleRuntimeException hcre) {

		}

		QueryPage qp = consoleCfg.executeHQLQuery("from java.lang.Object --this is my comment"); //$NON-NLS-1$
		Assert.assertNotNull(qp);
	}
	
	@Test
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
		IPersistentClass rc = service.newRootClass();
		rc.setEntityName("java.awt.Button");
		rc.setClassName( "java.awt.Button" );
		IColumn column = service.newColumn("label");
		ITable table = service.newTable("faketable");
		IPrimaryKey pk = table.getPrimaryKey();
		pk.addColumn(column);
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
		c.addClass(rc);

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
		Assert.assertNotNull(qp);
		try{
			qp.getList();//execute the query
		} catch (Exception e){
			//ignore - there is fake mapping
		}
	}


}
