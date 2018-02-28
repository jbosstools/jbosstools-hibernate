package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.OneToMany;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigurationFacadeTest {
	
	private static final String FOO_HBM_XML_STRING =
			"<!DOCTYPE hibernate-mapping PUBLIC" +
			"		'-//Hibernate/Hibernate Mapping DTD 3.0//EN'" +
			"		'http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd'>" +
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_3_6.internal'>" +
			"  <class name='ConfigurationFacadeTest$Foo'>" + 
			"    <id name='fooId'/>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	private static final String BAR_HBM_XML_STRING =
			"<!DOCTYPE hibernate-mapping PUBLIC" +
			"		'-//Hibernate/Hibernate Mapping DTD 3.0//EN'" +
			"		'http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd'>" +
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_3_6.internal'>" +
			"  <class name='ConfigurationFacadeTest$Bar'>" + 
			"    <id name='barId'/>" +
			"    <set name='fooSet' inverse='true'>" +
			"      <key column='fooId'/>" +
			"      <one-to-many class='ConfigurationFacadeTest$Foo'/>" +
			"    </set>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public String fooId;
	}
	
	static class Bar {
		public String barId;
		public Set<Foo> fooSet;
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
		printWriter.write(FOO_HBM_XML_STRING);
		printWriter.close();
		String fooClassName = 
				"org.jboss.tools.hibernate.runtime.v_3_6.internal.ConfigurationFacadeTest$Foo";
		// make sure the mappings are built before checking whether the class exists
		configuration.buildMappings();
		Assert.assertNull(configuration.getClassMapping(fooClassName));
		Assert.assertSame(
				configurationFacade,
				configurationFacade.addFile(testFile));
		// now that the file has been added, rebuild the mappings 
		configuration.buildMappings();
		// now the class should exist 
		Assert.assertNotNull(configuration.getClassMapping(fooClassName));
		Assert.assertTrue(testFile.delete());
	}
	
	@Test
	public void testSetEntityResolver() {
		EntityResolver testResolver = new DefaultHandler();
		Assert.assertNotSame(testResolver, configuration.getEntityResolver());
		configurationFacade.setEntityResolver(testResolver);
		Assert.assertSame(testResolver, configuration.getEntityResolver());
	}
	
	@Test
	public void testGetEntityResolver() {
		EntityResolver testResolver = new DefaultHandler();
		Assert.assertNotSame(testResolver, configurationFacade.getEntityResolver());
		configuration.setEntityResolver(testResolver);
		Assert.assertSame(testResolver, configurationFacade.getEntityResolver());
	}
	
	@Test
	public void testSetNamingStrategy() {
		NamingStrategy dns = new DefaultNamingStrategy();
		INamingStrategy namingStrategy = FACADE_FACTORY.createNamingStrategy(dns);
		Assert.assertNotSame(dns, configuration.getNamingStrategy());
		configurationFacade.setNamingStrategy(namingStrategy);
		Assert.assertSame(dns, configuration.getNamingStrategy());
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
		Assert.assertNull(configuration.getProperty(Environment.SESSION_FACTORY_NAME));
		configurationFacade.configure();
		Assert.assertEquals("bar", configuration.getProperty(Environment.SESSION_FACTORY_NAME));
	}
	
	@Test
	public void testBuildMappings() throws Exception {
		File fooFile = File.createTempFile("foo", "hbm.xml");
		PrintWriter fooWriter = new PrintWriter(fooFile);
		fooWriter.write(FOO_HBM_XML_STRING);
		fooWriter.close();
		configuration.addFile(fooFile);
		File barFile = File.createTempFile("bar", "hbm.xml");
		PrintWriter barWriter = new PrintWriter(barFile);
		barWriter.write(BAR_HBM_XML_STRING);
		barWriter.close();
		configuration.addFile(barFile);
		String collectionName = 
			"org.jboss.tools.hibernate.runtime.v_3_6.internal.ConfigurationFacadeTest$Bar.fooSet";
		Assert.assertNull(configuration.getCollectionMapping(collectionName));
		configurationFacade.buildMappings();
		Collection collection = configuration.getCollectionMapping(collectionName);
		OneToMany element = (OneToMany)collection.getElement();
		Assert.assertEquals(
				"org.jboss.tools.hibernate.runtime.v_3_6.internal.ConfigurationFacadeTest$Foo",
				element.getAssociatedClass().getClassName());
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
	public void testGetClassMappings() {
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		Iterator<IPersistentClass> iterator = configurationFacade.getClassMappings();
		Assert.assertFalse(iterator.hasNext());
		configuration.configure();
		configuration.buildMappings();
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		iterator = configurationFacade.getClassMappings();
		IPersistentClass persistentClassFacade = iterator.next();
		Assert.assertEquals(
				"org.jboss.tools.hibernate.runtime.v_3_6.internal.test.Foo",
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
		Iterator<?> iterator = jdbcMdCfg.getClassMappings();
		Assert.assertFalse(iterator.hasNext());		
		configurationFacade.readFromJDBC();
		iterator = jdbcMdCfg.getClassMappings();
		PersistentClass persistentClass = (PersistentClass)iterator.next();
		Assert.assertEquals("Foo", persistentClass.getClassName());
		statement.execute("DROP TABLE FOO");
		connection.close();
	}
	
	@Test
	public void testGetClassMapping() {
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		Assert.assertNull(configurationFacade.getClassMapping(
				"org.jboss.tools.hibernate.runtime.v_3_6.internal.test.Foo"));
		configuration.configure();
		configuration.buildMappings();
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		Assert.assertNotNull(configurationFacade.getClassMapping(
				"org.jboss.tools.hibernate.runtime.v_3_6.internal.test.Foo"));
	}
	
	@Test
	public void testGetNamingStrategy() {
		NamingStrategy firstStrategy = new DefaultNamingStrategy();
		configuration.setNamingStrategy(firstStrategy);
		INamingStrategy firstStrategyFacade = configurationFacade.getNamingStrategy();
		Assert.assertSame(firstStrategy, ((IFacade)firstStrategyFacade).getTarget());
		NamingStrategy secondStrategy = new DefaultNamingStrategy();
		configuration.setNamingStrategy(secondStrategy);
		INamingStrategy secondStrategyFacade = configurationFacade.getNamingStrategy();
		Assert.assertNotSame(secondStrategy, ((IFacade)secondStrategyFacade).getTarget());
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
	public void testAddClass() {
		PersistentClass persistentClass = new RootClass();
		persistentClass.setEntityName("Foo");
		IPersistentClass persistentClassFacade = 
				FACADE_FACTORY.createPersistentClass(persistentClass);	
		Assert.assertNull(configuration.getClassMapping("Foo"));
		configurationFacade.addClass(persistentClassFacade);
		Assert.assertEquals(persistentClass, configuration.getClassMapping("Foo"));
	}
	
}
