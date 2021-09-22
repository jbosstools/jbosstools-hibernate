package org.jboss.tools.hibernate.runtime.v_5_3.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;

import org.h2.Driver;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.v_5_3.internal.util.JdbcMetadataConfiguration;
import org.jboss.tools.hibernate.runtime.v_5_3.internal.util.MetadataHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigurationFacadeTest {
	
	private static final String TEST_HBM_XML_STRING =
			"<!DOCTYPE hibernate-mapping PUBLIC" +
			"		'-//Hibernate/Hibernate Mapping DTD 3.0//EN'" +
			"		'http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd'>" +
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_5_2.internal'>" +
			"  <class name='ConfigurationFacadeTest$Foo'>" + 
			"    <id name='id'/>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public String id;
	}
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private IConfiguration configurationFacade = null;
	private Configuration configuration = null;

	@BeforeAll
	public static void beforeAll() throws Exception {
		DriverManager.registerDriver(new Driver());		
	}

	@BeforeEach
	public void beforeEach() {
		configuration = new Configuration();
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
	}
	
	@Test
	public void testGetProperty() {
		assertNull(configurationFacade.getProperty("foo"));
		configuration.setProperty("foo", "bar");
		assertEquals("bar", configurationFacade.getProperty("foo"));
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
	public void testSetEntityResolver() {
		EntityResolver testResolver = new DefaultHandler();
		ConfigurationFacadeImpl facade = (ConfigurationFacadeImpl)configurationFacade;
		assertNull(facade.entityResolver);
		configurationFacade.setEntityResolver(testResolver);
		assertSame(testResolver, facade.entityResolver);
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
	public void testSetNamingStrategy() {
		INamingStrategy namingStrategy = FACADE_FACTORY.createNamingStrategy(new DefaultNamingStrategy());
		ConfigurationFacadeImpl facade = (ConfigurationFacadeImpl)configurationFacade;
		assertNotSame(namingStrategy, facade.namingStrategy);
		configurationFacade.setNamingStrategy(namingStrategy);
		assertSame(namingStrategy, facade.namingStrategy);
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
	public void testConfigure() {
		String fooClassName = 
				"org.jboss.tools.hibernate.runtime.v_5_3.internal.test.Foo";
		Metadata metadata = MetadataHelper.getMetadata(configuration);
		assertNull(metadata.getEntityBinding(fooClassName));
		configurationFacade.configure();
		metadata = MetadataHelper.getMetadata(configuration);
		assertNotNull(metadata.getEntityBinding(fooClassName));
	}
	
	@Test
	public void testBuildMappings() throws Exception {
		configurationFacade.buildMappings();
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
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		Iterator<IPersistentClass> iterator = configurationFacade.getClassMappings();
		assertFalse(iterator.hasNext());
		configuration.configure();
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		iterator = configurationFacade.getClassMappings();
		IPersistentClass persistentClassFacade = iterator.next();
		assertEquals(
				"org.jboss.tools.hibernate.runtime.v_5_3.internal.test.Foo",
				persistentClassFacade.getClassName());
	}
	
	@Test
	public void testSetPreferBasicCompositeIds() {
		JdbcMetadataConfiguration configuration = new JdbcMetadataConfiguration();
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		// the default is true
		assertTrue(configuration.preferBasicCompositeIds());
		configurationFacade.setPreferBasicCompositeIds(false);
		assertFalse(configuration.preferBasicCompositeIds());
	}
	
	@Test
	public void testSetReverseEngineeringStrategy() {
		JdbcMetadataConfiguration configuration = new JdbcMetadataConfiguration();
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		ReverseEngineeringStrategy reverseEngineeringStrategy = new DefaultReverseEngineeringStrategy();
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
		configurationFacade = FACADE_FACTORY.createConfiguration(jdbcMdCfg);
		Metadata metadata = jdbcMdCfg.getMetadata();
		assertNull(metadata);
		jdbcMdCfg = new JdbcMetadataConfiguration();
		jdbcMdCfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		configurationFacade = FACADE_FACTORY.createConfiguration(jdbcMdCfg);
		configurationFacade.readFromJDBC();
		metadata = jdbcMdCfg.getMetadata();
		Iterator<PersistentClass> iterator = metadata.getEntityBindings().iterator();
		PersistentClass persistentClass = (PersistentClass)iterator.next();
		assertEquals("Foo", persistentClass.getClassName());
		statement.execute("DROP TABLE FOO");
		connection.close();
	}
	
	@Test
	public void testGetClassMapping() {
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		assertNull(configurationFacade.getClassMapping(
				"org.jboss.tools.hibernate.runtime.v_5_3.internal.test.Foo"));
		configuration.configure();
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		assertNotNull(configurationFacade.getClassMapping(
				"org.jboss.tools.hibernate.runtime.v_5_3.internal.test.Foo"));
	}
	
	@Test
	public void testGetNamingStrategy() {
		INamingStrategy strategy = FACADE_FACTORY.createNamingStrategy(new DefaultNamingStrategy());
		ConfigurationFacadeImpl facade = (ConfigurationFacadeImpl)configurationFacade;
		assertNull(facade.getNamingStrategy());
		facade.namingStrategy = strategy;
		assertSame(strategy, facade.getNamingStrategy());
	}
	
	@Test
	public void testGetTableMappings() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		JdbcMetadataConfiguration jdbcMdCfg = new JdbcMetadataConfiguration();
		jdbcMdCfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		configurationFacade = FACADE_FACTORY.createConfiguration(jdbcMdCfg);
		Iterator<ITable> iterator = configurationFacade.getTableMappings();
		assertFalse(iterator.hasNext());
		jdbcMdCfg.readFromJDBC();
		configurationFacade = FACADE_FACTORY.createConfiguration(jdbcMdCfg);
		iterator = configurationFacade.getTableMappings();
		Table table = (Table)((IFacade)iterator.next()).getTarget();
		assertEquals("FOO", table.getName());
		statement.execute("DROP TABLE FOO");
		connection.close();
	}
	
	@Test
	public void testAddClass() {
		PersistentClass persistentClass = new RootClass(null);
		persistentClass.setEntityName("Foo");
		IPersistentClass persistentClassFacade = 
				FACADE_FACTORY.createPersistentClass(persistentClass);	
		assertNull(configurationFacade.getClassMapping("Foo"));
		configurationFacade = 
				FACADE_FACTORY.createConfiguration(configuration);
		configurationFacade.addClass(persistentClassFacade);
		assertEquals(persistentClassFacade, configurationFacade.getClassMapping("Foo"));
	}
	
}
