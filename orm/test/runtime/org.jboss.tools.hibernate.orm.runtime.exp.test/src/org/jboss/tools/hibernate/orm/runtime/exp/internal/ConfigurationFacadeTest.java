package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.h2.Driver;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.util.JdbcMetadataConfiguration;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigurationFacadeTest {

	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.orm.runtime.exp.internal'>" +
			"  <class name='ConfigurationFacadeTest$Foo'>" + 
			"    <id name='id'/>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory name='bar'>" + 
			"    <mapping resource='Foo.hbm.xml' />" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	static class Foo {
		public String id;
	}
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	private static final NewFacadeFactory NEW_FACADE_FACTORY = NewFacadeFactory.INSTANCE;

	@BeforeAll
	public static void beforeAll() throws Exception {
		DriverManager.registerDriver(new Driver());		
	}

	private IConfiguration configurationFacade = null;
	private Configuration configuration = null;

	@BeforeEach
	public void beforeEach() {
		configuration = new Configuration();
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
	}	
	
	@Test
	public void testInstance() {
		assertNotNull(configuration);
		assertNotNull(configurationFacade);
	}

	@Test
	public void testGetProperty() {
		assertNull(configurationFacade.getProperty("foo"));
		configuration.setProperty("foo", "bar");
		assertEquals("bar", configurationFacade.getProperty("foo"));
	}

	@Test
	public void testAddFile() throws Exception {
		File testFile = File.createTempFile("test", "hbm.xml");
		PrintWriter printWriter = new PrintWriter(testFile);
		printWriter.write(TEST_HBM_XML_STRING);
		printWriter.close();
		MetadataSources metadataSources = MetadataHelper.getMetadataSources(configuration);
		assertTrue(metadataSources.getXmlBindings().isEmpty());
		assertSame(
				configurationFacade,
				configurationFacade.addFile(testFile));
		assertFalse(metadataSources.getXmlBindings().isEmpty());
		Binding<?> binding = metadataSources.getXmlBindings().iterator().next();
		assertEquals(testFile.getAbsolutePath(), binding.getOrigin().getName());
		assertTrue(testFile.delete());
	}
	
	@Test 
	public void testSetProperty() {
		assertNull(configuration.getProperty("foo"));
		configurationFacade.setProperty("foo", "bar");
		assertEquals("bar", configuration.getProperty("foo"));
	}

	@Test 
	public void testSetProperties() {
		Properties testProperties = new Properties();
		assertNotSame(testProperties, configuration.getProperties());
		assertSame(
				configurationFacade, 
				configurationFacade.setProperties(testProperties));
		assertSame(testProperties, configuration.getProperties());
	}
	
	@Test
	public void testSetEntityResolver() {
		EntityResolver testResolver = new DefaultHandler();
		ConfigurationFacadeImpl facade = (ConfigurationFacadeImpl)configurationFacade;
		assertNull(facade.entityResolver);
		configurationFacade.setEntityResolver(testResolver);
		assertSame(testResolver, facade.entityResolver);
	}
	
	@Test
	public void testSetNamingStrategy() {
		INamingStrategy namingStrategy = NEW_FACADE_FACTORY.createNamingStrategy(DefaultNamingStrategy.class.getName());
		ConfigurationFacadeImpl facade = (ConfigurationFacadeImpl)configurationFacade;
		assertNotSame(namingStrategy, facade.namingStrategy);
		configurationFacade.setNamingStrategy(namingStrategy);
		assertSame(namingStrategy, facade.namingStrategy);
	}
	
	@Test
	public void testGetProperties() {
		Properties testProperties = new Properties();
		assertNotSame(testProperties, configurationFacade.getProperties());
		configuration.setProperties(testProperties);
		assertSame(testProperties, configurationFacade.getProperties());
	}
	
	@Test
	public void testAddProperties() {
		assertNull(configuration.getProperty("foo"));
		Properties testProperties = new Properties();
		testProperties.put("foo", "bar");
		configurationFacade.addProperties(testProperties);
		assertEquals("bar", configuration.getProperty("foo"));
	}
	
	@Test
	public void testConfigureDocument() throws Exception {
		Document document = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.newDocument();
		Element hibernateConfiguration = document.createElement("hibernate-configuration");
		document.appendChild(hibernateConfiguration);
		Element sessionFactory = document.createElement("session-factory");
		sessionFactory.setAttribute("name", "bar");
		hibernateConfiguration.appendChild(sessionFactory);
		Element mapping = document.createElement("mapping");
		mapping.setAttribute("resource", "Foo.hbm.xml");
		sessionFactory.appendChild(mapping);
		
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		File hbmXmlFile = new File(new File(url.toURI()), "Foo.hbm.xml");
		hbmXmlFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();

		String fooClassName = 
				"org.jboss.tools.hibernate.orm.runtime.exp.internal.ConfigurationFacadeTest$Foo";
		Metadata metadata = MetadataHelper.getMetadata(configuration);
		assertNull(metadata.getEntityBinding(fooClassName));
		configurationFacade.configure(document);
		metadata = MetadataHelper.getMetadata(configuration);
		assertNotNull(metadata.getEntityBinding(fooClassName));
	}
	
	@Test
	public void testConfigureFile() throws Exception {
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		File cfgXmlFile = new File(new File(url.toURI()), "foobarfile.cfg.xml");
		cfgXmlFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(cfgXmlFile);
		fileWriter.write(TEST_CFG_XML_STRING);
		fileWriter.close();
		File hbmXmlFile = new File(new File(url.toURI()), "Foo.hbm.xml");
		hbmXmlFile.deleteOnExit();
		fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();
		String fooClassName = 
				"org.jboss.tools.hibernate.orm.runtime.exp.internal.ConfigurationFacadeTest$Foo";
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		Metadata metadata = MetadataHelper.getMetadata(configuration);
		assertNull(metadata.getEntityBinding(fooClassName));
		configurationFacade.configure(cfgXmlFile);
		metadata = MetadataHelper.getMetadata(configuration);
		assertNotNull(metadata.getEntityBinding(fooClassName));
	}
	
	@Test
	public void testConfigureDefault() throws Exception {
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		File cfgXmlFile = new File(new File(url.toURI()), "hibernate.cfg.xml");
		cfgXmlFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(cfgXmlFile);
		fileWriter.write(TEST_CFG_XML_STRING);
		fileWriter.close();
		File hbmXmlFile = new File(new File(url.toURI()), "Foo.hbm.xml");
		hbmXmlFile.deleteOnExit();
		fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();
		String fooClassName = 
				"org.jboss.tools.hibernate.orm.runtime.exp.internal.ConfigurationFacadeTest$Foo";
		Metadata metadata = MetadataHelper.getMetadata(configuration);
		assertNull(metadata.getEntityBinding(fooClassName));
		configurationFacade.configure();
		metadata = MetadataHelper.getMetadata(configuration);
		assertNotNull(metadata.getEntityBinding(fooClassName));
	}
	
	@Test
	public void testAddClass() {
		PersistentClass persistentClass = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		persistentClass.setEntityName("Foo");
		IPersistentClass persistentClassFacade = 
				FACADE_FACTORY.createPersistentClass(persistentClass);	
		assertFalse(((ConfigurationFacadeImpl)configurationFacade).addedClasses.contains(persistentClassFacade));
		configurationFacade.addClass(persistentClassFacade);
		assertTrue(((ConfigurationFacadeImpl)configurationFacade).addedClasses.contains(persistentClassFacade));
	}
	
	@Test
	public void testBuildMappings() {
		assertNull(((ConfigurationFacadeImpl)configurationFacade).metadata);
		configurationFacade.buildMappings();
		assertNotNull(((ConfigurationFacadeImpl)configurationFacade).metadata);
	}

	@Test
	public void testBuildSessionFactory() throws Throwable {
		ISessionFactory sessionFactoryFacade = 
				configurationFacade.buildSessionFactory();
		assertNotNull(sessionFactoryFacade);
		Object sessionFactory = ((IFacade)sessionFactoryFacade).getTarget();
		assertNotNull(sessionFactory);
		assertTrue(sessionFactory instanceof SessionFactory);
	}
	
	@Test
	public void testGetClassMappings() {
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
		assertFalse(configurationFacade.getClassMappings().hasNext());		
		PersistentClass persistentClass = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		persistentClass.setEntityName("Foo");
		IPersistentClass persistentClassFacade = 
				FACADE_FACTORY.createPersistentClass(persistentClass);	
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
		((ConfigurationFacadeImpl)configurationFacade).addedClasses.add(persistentClassFacade);
		Iterator<IPersistentClass> iterator = configurationFacade.getClassMappings();
		assertTrue(iterator.hasNext());
		assertSame(iterator.next(), persistentClassFacade);		
	}
	
	@Test
	public void testSetPreferBasicCompositeIds() {
		JdbcMetadataConfiguration configuration = new JdbcMetadataConfiguration();
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
		// the default is true
		assertTrue(configuration.preferBasicCompositeIds());
		configurationFacade.setPreferBasicCompositeIds(false);
		assertFalse(configuration.preferBasicCompositeIds());
	}
	
	@Test
	public void testSetReverseEngineeringStrategy() {
		JdbcMetadataConfiguration configuration = new JdbcMetadataConfiguration();
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
		RevengStrategy reverseEngineeringStrategy = new DefaultStrategy();
		IReverseEngineeringStrategy strategyFacade = 
				FACADE_FACTORY.createReverseEngineeringStrategy(reverseEngineeringStrategy);
		assertNotSame(
				reverseEngineeringStrategy,
				configuration.getReverseEngineeringStrategy());
		configurationFacade.setReverseEngineeringStrategy(strategyFacade);
		assertSame(
				reverseEngineeringStrategy, 
				configuration.getReverseEngineeringStrategy());
	}
	
	@Test
	public void testReadFromJDBC() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		JdbcMetadataConfiguration jdbcMdCfg = new JdbcMetadataConfiguration();
		jdbcMdCfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, jdbcMdCfg);
		Metadata metadata = jdbcMdCfg.getMetadata();
		assertNull(metadata);
		jdbcMdCfg = new JdbcMetadataConfiguration();
		jdbcMdCfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, jdbcMdCfg);
		configurationFacade.readFromJDBC();
		metadata = jdbcMdCfg.getMetadata();
		Iterator<PersistentClass> iterator = metadata.getEntityBindings().iterator();
		PersistentClass persistentClass = iterator.next();
		assertEquals("Foo", persistentClass.getClassName());
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
	}

	@Test
	public void testGetClassMapping() {
		PersistentClass persistentClass = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		persistentClass.setEntityName("Foo");
		IPersistentClass persistentClassFacade = 
				FACADE_FACTORY.createPersistentClass(persistentClass);	
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
		assertNull(configurationFacade.getClassMapping("Foo"));
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
		((ConfigurationFacadeImpl)configurationFacade).addedClasses.add(persistentClassFacade);
		assertSame(configurationFacade.getClassMapping("Foo"), persistentClassFacade);
	}
	
	@Test
	public void testGetNamingStrategy() {
		INamingStrategy strategy = NEW_FACADE_FACTORY.createNamingStrategy(DefaultNamingStrategy.class.getName());
		ConfigurationFacadeImpl facade = (ConfigurationFacadeImpl)configurationFacade;
		assertNull(facade.getNamingStrategy());
		facade.namingStrategy = strategy;
		assertSame(strategy, facade.getNamingStrategy());
	}
	
	@Test
	public void testGetEntityResolver() {
		EntityResolver testResolver = new DefaultHandler();
		ConfigurationFacadeImpl facade = (ConfigurationFacadeImpl)configurationFacade;
		assertNotSame(testResolver, configurationFacade.getEntityResolver());
		facade.entityResolver = testResolver;
		assertSame(testResolver, configurationFacade.getEntityResolver());
	}
	
	@Test
	public void testGetTableMappings() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		JdbcMetadataConfiguration jdbcMdCfg = new JdbcMetadataConfiguration();
		jdbcMdCfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, jdbcMdCfg);
		Iterator<ITable> iterator = configurationFacade.getTableMappings();
		assertFalse(iterator.hasNext());
		jdbcMdCfg.readFromJdbc();
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, jdbcMdCfg);
		iterator = configurationFacade.getTableMappings();
		IFacade facade = (IFacade)iterator.next();
		Table table = (Table)facade.getTarget();
		assertEquals("FOO", table.getName());
		statement.execute("DROP TABLE FOO");
		connection.close();
	}
	
	@Test
	public void testGetMetadata() {
		NativeTestConfiguration nativeConfiguration = new NativeTestConfiguration();
		ConfigurationFacadeImpl nativeFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, nativeConfiguration);
		assertNull(nativeFacade.metadata);
		Metadata nativeMetadata = nativeFacade.getMetadata();
		assertNotNull(nativeMetadata);
		assertSame(nativeMetadata, NativeTestConfiguration.METADATA);
		assertNotNull(nativeFacade.metadata);
		assertSame(nativeFacade.metadata, NativeTestConfiguration.METADATA);
		JdbcMetadataTestConfiguration jdbcConfiguration = new JdbcMetadataTestConfiguration();
		ConfigurationFacadeImpl jdbcFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, jdbcConfiguration);
		assertNull(jdbcFacade.metadata);
		Metadata jdbcMetadata = jdbcFacade.getMetadata();
		assertNotNull(jdbcMetadata);
		assertSame(jdbcMetadata, JdbcMetadataTestConfiguration.METADATA);
		assertNotNull(jdbcFacade.metadata);
		assertSame(jdbcFacade.metadata, JdbcMetadataTestConfiguration.METADATA);
	}
	
	@Test
	public void testGetAddedClasses() {
		ArrayList<IPersistentClass> list = new ArrayList<IPersistentClass>();
		assertNotNull(((ConfigurationFacadeImpl)configurationFacade).getAddedClasses());
		assertNotSame(((ConfigurationFacadeImpl)configurationFacade).getAddedClasses(), list);
		((ConfigurationFacadeImpl)configurationFacade).addedClasses = list;
		assertSame(((ConfigurationFacadeImpl)configurationFacade).getAddedClasses(), list);
	} 
	
	private static class NativeTestConfiguration extends Configuration {
		static Metadata METADATA = createMetadata();
		@SuppressWarnings("unused")
		public Metadata getMetadata() {
			return METADATA;
		}
	}
	
	private static class JdbcMetadataTestConfiguration extends JdbcMetadataConfiguration {
		static Metadata METADATA = createMetadata();
		public Metadata getMetadata() {
			return METADATA;
		}
	}
	
	private static Metadata createMetadata() {
		Metadata result = null;
		result = (Metadata) Proxy.newProxyInstance(
				ConfigurationFacadeTest.class.getClassLoader(), 
				new Class[] { Metadata.class },  
				new InvocationHandler() {				
					@Override
					public Object invoke(
							Object proxy, 
							Method method, 
							Object[] args) throws Throwable {
						return null;
					}
				});
		return result;
	}
	
}
