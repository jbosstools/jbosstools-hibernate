package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.util.MetadataHelper;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.jboss.tools.hibernate.runtime.spi.IMapping;
import org.jboss.tools.hibernate.runtime.spi.IMappings;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ISettings;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

	@Before
	public void setUp() {
		configuration = new Configuration();
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
	}
	
	@Test
	public void testGetProperty() {
		Assert.assertNull(configurationFacade.getProperty("foo"));
		configuration.setProperty("foo", "bar");
		Assert.assertEquals("bar", configurationFacade.getProperty("foo"));
	}

	@Test 
	public void testSetProperty() {
		Assert.assertNull(configuration.getProperty("foo"));
		configurationFacade.setProperty("foo", "bar");
		Assert.assertEquals("bar", configuration.getProperty("foo"));
	}

	@Test 
	public void testSetProperties() {
		Properties testProperties = new Properties();
		Assert.assertNotSame(testProperties, configuration.getProperties());
		Assert.assertSame(
				configurationFacade, 
				configurationFacade.setProperties(testProperties));
		Assert.assertSame(testProperties, configuration.getProperties());
	}
	
	@Test
	public void testAddFile() throws Exception {
		File testFile = File.createTempFile("test", "hbm.xml");
		PrintWriter printWriter = new PrintWriter(testFile);
		printWriter.write(TEST_HBM_XML_STRING);
		printWriter.close();
		MetadataSources metadataSources = MetadataHelper.getMetadataSources(configuration);
		Assert.assertTrue(metadataSources.getXmlBindings().isEmpty());
		Assert.assertSame(
				configurationFacade,
				configurationFacade.addFile(testFile));
		Assert.assertFalse(metadataSources.getXmlBindings().isEmpty());
		Binding<?> binding = metadataSources.getXmlBindings().iterator().next();
		Assert.assertEquals(testFile.getAbsolutePath(), binding.getOrigin().getName());
		Assert.assertTrue(testFile.delete());
	}
	
	@Test
	public void testSetEntityResolver() {
		EntityResolver testResolver = new DefaultHandler();
		ConfigurationFacadeImpl facade = (ConfigurationFacadeImpl)configurationFacade;
		Assert.assertNull(facade.entityResolver);
		configurationFacade.setEntityResolver(testResolver);
		Assert.assertSame(testResolver, facade.entityResolver);
	}
	
	@Test
	public void testGetEntityResolver() {
		EntityResolver testResolver = new DefaultHandler();
		ConfigurationFacadeImpl facade = (ConfigurationFacadeImpl)configurationFacade;
		Assert.assertNotSame(testResolver, configurationFacade.getEntityResolver());
		facade.entityResolver = testResolver;
		Assert.assertSame(testResolver, configurationFacade.getEntityResolver());
	}
	
	@Test
	public void testSetNamingStrategy() {
		INamingStrategy namingStrategy = FACADE_FACTORY.createNamingStrategy(new DefaultNamingStrategy());
		ConfigurationFacadeImpl facade = (ConfigurationFacadeImpl)configurationFacade;
		Assert.assertNotSame(namingStrategy, facade.namingStrategy);
		configurationFacade.setNamingStrategy(namingStrategy);
		Assert.assertSame(namingStrategy, facade.namingStrategy);
	}
	
	@Test
	public void testAddProperties() {
		Assert.assertNull(configuration.getProperty("foo"));
		Properties testProperties = new Properties();
		testProperties.put("foo", "bar");
		configurationFacade.addProperties(testProperties);
		Assert.assertEquals("bar", configuration.getProperty("foo"));
	}
	
	@Test
	public void testConfigure() {
		String fooClassName = 
				"org.jboss.tools.hibernate.runtime.v_5_2.internal.test.Foo";
		Metadata metadata = MetadataHelper.getMetadata(configuration);
		Assert.assertNull(metadata.getEntityBinding(fooClassName));
		configurationFacade.configure();
		metadata = MetadataHelper.getMetadata(configuration);
		Assert.assertNotNull(metadata.getEntityBinding(fooClassName));
	}
	
	@Test 
	public void testCreateMappings() {
		IMappings mappingsFacade = configurationFacade.createMappings();
		Assert.assertNotNull(mappingsFacade);
		Object object = ((IFacade)mappingsFacade).getTarget();
		Assert.assertNull(object);
	}

	@Test
	public void testBuildMappings() throws Exception {
		ConfigurationFacadeImpl facade = (ConfigurationFacadeImpl)configurationFacade;
		Assert.assertNull(facade.mappings);
		configurationFacade.buildMappings();
		Assert.assertNotNull(facade.mappings);
		MappingsFacadeImpl mappings = (MappingsFacadeImpl)facade.mappings;
		Assert.assertSame(configurationFacade, mappings.configuration);
	}
	
	@Test
	public void testBuildSessionFactory() throws Throwable {
		ISessionFactory sessionFactoryFacade = 
				configurationFacade.buildSessionFactory();
		Assert.assertNotNull(sessionFactoryFacade);
		Object sessionFactory = ((IFacade)sessionFactoryFacade).getTarget();
		Assert.assertNotNull(sessionFactory);
		Assert.assertTrue(sessionFactory instanceof SessionFactory);
	}
	
	@Test
	public void testBuildSettings() {
		ISettings settingsFacade = configurationFacade.buildSettings();
		Assert.assertNotNull(settingsFacade);
		Object settings = ((IFacade)settingsFacade).getTarget();
		Assert.assertNotNull(settings);
		Assert.assertTrue(settings instanceof Settings);
	}
	
	@Test
	public void testGetClassMappings() {
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		Iterator<IPersistentClass> iterator = configurationFacade.getClassMappings();
		Assert.assertFalse(iterator.hasNext());
		configuration.configure();
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		iterator = configurationFacade.getClassMappings();
		IPersistentClass persistentClassFacade = iterator.next();
		Assert.assertEquals(
				"org.jboss.tools.hibernate.runtime.v_5_2.internal.test.Foo",
				persistentClassFacade.getClassName());
	}
	
	@Test
	public void testSetPreferBasicCompositeIds() {
		JDBCMetaDataConfiguration configuration = new JDBCMetaDataConfiguration();
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		// the default is false
		Assert.assertTrue(configuration.preferBasicCompositeIds());
		configurationFacade.setPreferBasicCompositeIds(false);
		Assert.assertFalse(configuration.preferBasicCompositeIds());
	}
	
	@Test
	public void testSetReverseEngineeringStrategy() {
		JDBCMetaDataConfiguration configuration = new JDBCMetaDataConfiguration();
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		ReverseEngineeringStrategy reverseEngineeringStrategy = new DefaultReverseEngineeringStrategy();
		IReverseEngineeringStrategy strategyFacade = 
				FACADE_FACTORY.createReverseEngineeringStrategy(reverseEngineeringStrategy);
		Assert.assertNotSame(
				reverseEngineeringStrategy,
				configuration.getReverseEngineeringStrategy());
		configurationFacade.setReverseEngineeringStrategy(strategyFacade);
		Assert.assertSame(
				reverseEngineeringStrategy, 
				configuration.getReverseEngineeringStrategy());
	}
	
	@Test
	public void testReadFromJDBC() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		JDBCMetaDataConfiguration jdbcMdCfg = new JDBCMetaDataConfiguration();
		jdbcMdCfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		configurationFacade = FACADE_FACTORY.createConfiguration(jdbcMdCfg);
		Metadata metadata = jdbcMdCfg.getMetadata();
		Iterator<?> iterator = metadata.getEntityBindings().iterator();
		jdbcMdCfg = new JDBCMetaDataConfiguration();
		jdbcMdCfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		configurationFacade = FACADE_FACTORY.createConfiguration(jdbcMdCfg);
		Assert.assertFalse(iterator.hasNext());		
		configurationFacade.readFromJDBC();
		metadata = jdbcMdCfg.getMetadata();
		iterator = metadata.getEntityBindings().iterator();
		PersistentClass persistentClass = (PersistentClass)iterator.next();
		Assert.assertEquals("Foo", persistentClass.getClassName());
		statement.execute("DROP TABLE FOO");
		connection.close();
	}
	
	@Test
	public void testBuildMapping() {
		configuration.configure();
		IMapping mappingFacade = configurationFacade.buildMapping();
		Mapping mapping = (Mapping)((IFacade)mappingFacade).getTarget();
		Assert.assertEquals(
				"id", 
				mapping.getIdentifierPropertyName(
						"org.jboss.tools.hibernate.runtime.v_5_2.internal.test.Foo"));
	}
	
	@Test
	public void testGetClassMapping() {
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		Assert.assertNull(configurationFacade.getClassMapping(
				"org.jboss.tools.hibernate.runtime.v_5_2.internal.test.Foo"));
		configuration.configure();
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		Assert.assertNotNull(configurationFacade.getClassMapping(
				"org.jboss.tools.hibernate.runtime.v_5_2.internal.test.Foo"));
	}
	
	@Test
	public void testGetNamingStrategy() {
		INamingStrategy strategy = FACADE_FACTORY.createNamingStrategy(new DefaultNamingStrategy());
		ConfigurationFacadeImpl facade = (ConfigurationFacadeImpl)configurationFacade;
		Assert.assertNull(facade.getNamingStrategy());
		facade.namingStrategy = strategy;
		Assert.assertSame(strategy, facade.getNamingStrategy());
	}
	
	@Test
	public void testGetTableMappings() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		JDBCMetaDataConfiguration jdbcMdCfg = new JDBCMetaDataConfiguration();
		jdbcMdCfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		configurationFacade = FACADE_FACTORY.createConfiguration(jdbcMdCfg);
		Iterator<ITable> iterator = configurationFacade.getTableMappings();
		Assert.assertFalse(iterator.hasNext());
		jdbcMdCfg.readFromJDBC();
		configurationFacade = FACADE_FACTORY.createConfiguration(jdbcMdCfg);
		iterator = configurationFacade.getTableMappings();
		Table table = (Table)((IFacade)iterator.next()).getTarget();
		Assert.assertEquals("FOO", table.getName());
		statement.execute("DROP TABLE FOO");
		connection.close();
	}
	
	@Test
	public void testGetDialect() {
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		IDialect dialectFacade = configurationFacade.getDialect();
		Assert.assertNotNull(dialectFacade);
		Dialect dialect = (Dialect)((IFacade)dialectFacade).getTarget();
		Assert.assertEquals("org.hibernate.dialect.H2Dialect", dialect.getClass().getName());
	}
	
	@Test
	public void testGetDefaultCatalogName() {
		Assert.assertNull(configurationFacade.getDefaultCatalogName());
	}
	
}
