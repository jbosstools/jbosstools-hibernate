package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.h2.Driver;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.orm.jbt.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.util.RevengConfiguration;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

public class IConfigurationTest {

	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.orm.runtime.exp.internal'>" +
			"  <class name='IConfigurationTest$Foo'>" + 
			"    <id name='id'/>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory name='bar'>" + 
			"    <mapping resource='Foo.hbm.xml' />" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String PERSISTENCE_XML = 
			"<persistence version='2.2'" +
	        "  xmlns='http://xmlns.jcp.org/xml/ns/persistence'" +
		    "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
	        "  xsi:schemaLocation='http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd'>" +
	        "  <persistence-unit name='foobar'>" +
	        "    <class>"+ FooBar.class.getName()  +"</class>" +
	        "    <properties>" +
	        "      <property name='" + AvailableSettings.DIALECT + "' value='" + MockDialect.class.getName() + "'/>" +
	        "      <property name='" + AvailableSettings.CONNECTION_PROVIDER + "' value='" + MockConnectionProvider.class.getName() + "'/>" +
	        "      <property name='foo' value='bar'/>" +
	        "    </properties>" +
	        "  </persistence-unit>" +
			"</persistence>";
	
	private static final NewFacadeFactory NEW_FACADE_FACTORY = NewFacadeFactory.INSTANCE;

	static class Foo {
		public String id;
	}
	
	@Entity public class FooBar {
		@Id public int id;
	}

	@BeforeAll
	public static void beforeAll() throws Exception {
		DriverManager.registerDriver(new Driver());		
	}

	private ClassLoader original = null;
	
	@TempDir
	public File tempRoot;
	
	private IConfiguration nativeConfigurationFacade = null;
	private NativeConfiguration nativeConfigurationTarget = null;
	private IConfiguration revengConfigurationFacade = null;
	private RevengConfiguration revengConfigurationTarget = null;
	private IConfiguration jpaConfigurationFacade = null;
	private JpaConfiguration jpaConfigurationTarget = null;

	@BeforeEach
	public void beforeEach() throws Exception {
		tempRoot = Files.createTempDirectory("temp").toFile();
		createPersistenceXml();
		swapClassLoader();
		initializeFacadesAndTargets();
	}	
	
	@AfterEach
	public void afterEach() {
		Thread.currentThread().setContextClassLoader(original);
	}
	
	@Test
	public void testInstance() {
		assertNotNull(nativeConfigurationFacade);
		assertNotNull(nativeConfigurationTarget);
		assertNotNull(revengConfigurationFacade);
		assertNotNull(revengConfigurationTarget);
		assertNotNull(jpaConfigurationFacade);
		assertNotNull(jpaConfigurationTarget);
	}

	@Test
	public void testGetProperty() {
		// For native configuration
		assertNull(nativeConfigurationFacade.getProperty("foo"));
		nativeConfigurationTarget.setProperty("foo", "bar");
		assertEquals("bar", nativeConfigurationFacade.getProperty("foo"));
		// For reveng configuration
		assertNull(revengConfigurationFacade.getProperty("foo"));
		revengConfigurationTarget.setProperty("foo", "bar");
		assertEquals("bar", revengConfigurationFacade.getProperty("foo"));
		// For jpa configuration
		assertNull(jpaConfigurationFacade.getProperty("foo"));
		jpaConfigurationTarget.setProperty("foo", "bar");
		assertEquals("bar", jpaConfigurationFacade.getProperty("foo"));
	}

	
	@Test
	public void testAddFile() throws Exception {
		File testFile = File.createTempFile("test", "hbm.xml");
		PrintWriter printWriter = new PrintWriter(testFile);
		printWriter.write(TEST_HBM_XML_STRING);
		printWriter.close();
		testFile.deleteOnExit();
		// For native configuration
		MetadataSources metadataSources = MetadataHelper.getMetadataSources(nativeConfigurationTarget);
		assertTrue(metadataSources.getXmlBindings().isEmpty());
		assertSame(
				nativeConfigurationFacade,
				nativeConfigurationFacade.addFile(testFile));
		assertFalse(metadataSources.getXmlBindings().isEmpty());
		Binding<?> binding = metadataSources.getXmlBindings().iterator().next();
		assertEquals(testFile.getAbsolutePath(), binding.getOrigin().getName());
		// For reveng configuration
		try {
			revengConfigurationFacade.addFile(testFile);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'addFile' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationFacade.addFile(testFile);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'addFile' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test 
	public void testSetProperty() {
		// For native configuration
		assertNull(nativeConfigurationTarget.getProperty("foo"));
		nativeConfigurationFacade.setProperty("foo", "bar");
		assertEquals("bar", nativeConfigurationTarget.getProperty("foo"));
		// For reveng configuration
		assertNull(revengConfigurationTarget.getProperty("foo"));
		revengConfigurationFacade.setProperty("foo", "bar");
		assertEquals("bar", revengConfigurationTarget.getProperty("foo"));
		// For jpa configuration
		assertNull(jpaConfigurationTarget.getProperty("foo"));
		jpaConfigurationFacade.setProperty("foo", "bar");
		assertEquals("bar", jpaConfigurationTarget.getProperty("foo"));
	}

	@Test 
	public void testSetProperties() {
		Properties testProperties = new Properties();
		// For native configuration
		assertNotSame(testProperties, nativeConfigurationTarget.getProperties());
		assertSame(
				nativeConfigurationFacade, 
				nativeConfigurationFacade.setProperties(testProperties));
		assertSame(testProperties, nativeConfigurationTarget.getProperties());
		// For reveng configuration
		assertNotSame(testProperties, revengConfigurationTarget.getProperties());
		assertSame(
				revengConfigurationFacade, 
				revengConfigurationFacade.setProperties(testProperties));
		assertSame(testProperties, revengConfigurationTarget.getProperties());
		// For jpa configuration
		assertNotSame(testProperties, jpaConfigurationTarget.getProperties());
		assertSame(
				jpaConfigurationFacade, 
				jpaConfigurationFacade.setProperties(testProperties));
		assertSame(testProperties, jpaConfigurationTarget.getProperties());
	}
	
	@Test
	public void testSetEntityResolver() throws Exception {
		EntityResolver testResolver = new DefaultHandler();
		// For native configuration
		Field entityResolverField = nativeConfigurationTarget.getClass().getDeclaredField("entityResolver");
		entityResolverField.setAccessible(true);
		assertNull(entityResolverField.get(nativeConfigurationTarget));
		nativeConfigurationFacade.setEntityResolver(testResolver);
		assertNotNull(entityResolverField.get(nativeConfigurationTarget));
		assertSame(testResolver, entityResolverField.get(nativeConfigurationTarget));
		// For reveng configuration
		try {
			revengConfigurationFacade.setEntityResolver(testResolver);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setEntityResolver' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationFacade.setEntityResolver(testResolver);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setEntityResolver' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testSetNamingStrategy() throws Exception {
		INamingStrategy namingStrategyFacade = 
				NEW_FACADE_FACTORY.createNamingStrategy(DefaultNamingStrategy.class.getName());
		// For native configuration
		Field namingStrategyField = nativeConfigurationTarget.getClass().getDeclaredField("namingStrategy");
		namingStrategyField.setAccessible(true);
		NamingStrategy namingStrategyTarget = (NamingStrategy)((IFacade)namingStrategyFacade).getTarget();
		assertNull(namingStrategyField.get(nativeConfigurationTarget));
		nativeConfigurationFacade.setNamingStrategy(namingStrategyFacade);
		assertNotNull(namingStrategyField.get(nativeConfigurationTarget));
		assertSame(namingStrategyField.get(nativeConfigurationTarget), namingStrategyTarget);
		// For reveng configuration
		try {
			revengConfigurationFacade.setNamingStrategy(namingStrategyFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setNamingStrategy' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationFacade.setNamingStrategy(namingStrategyFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setNamingStrategy' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testGetProperties() {
		Properties testProperties = new Properties();
		// For native configuration
		assertNotSame(testProperties, nativeConfigurationFacade.getProperties());
		nativeConfigurationTarget.setProperties(testProperties);
		assertSame(testProperties, nativeConfigurationFacade.getProperties());
		// For reveng configuration
		assertNotSame(testProperties, revengConfigurationFacade.getProperties());
		revengConfigurationTarget.setProperties(testProperties);
		assertSame(testProperties, revengConfigurationFacade.getProperties());
		// For jpa configuration
		assertNotSame(testProperties, jpaConfigurationFacade.getProperties());
		jpaConfigurationTarget.setProperties(testProperties);
		assertSame(testProperties, jpaConfigurationFacade.getProperties());
	}
	
	@Test
	public void testAddProperties() {
		Properties testProperties = new Properties();
		testProperties.put("foo", "bar");
		// For native configuration
		assertNull(nativeConfigurationTarget.getProperty("foo"));
		nativeConfigurationFacade.addProperties(testProperties);
		assertEquals("bar", nativeConfigurationTarget.getProperty("foo"));
		// For reveng configuration
		assertNull(revengConfigurationTarget.getProperty("foo"));
		revengConfigurationFacade.addProperties(testProperties);
		assertEquals("bar", revengConfigurationTarget.getProperty("foo"));
		// For jpa configuration
		assertNull(jpaConfigurationTarget.getProperty("foo"));
		jpaConfigurationFacade.addProperties(testProperties);
		assertEquals("bar", jpaConfigurationTarget.getProperty("foo"));
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
		
		// For native configuration
		String fooClassName = 
				"org.jboss.tools.hibernate.orm.runtime.exp.internal.IConfigurationTest$Foo";
		Metadata metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
		assertNull(metadata.getEntityBinding(fooClassName));
		nativeConfigurationFacade.configure(document);
		metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
		assertNotNull(metadata.getEntityBinding(fooClassName));
		// For reveng configuration
		try {
			revengConfigurationFacade.configure(document);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'configure' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationFacade.configure(document);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'configure' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testConfigureFile() throws Exception {
		// For native configuration
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		File cfgXmlFile = new File(new File(url.toURI()), "foobarfile.cfg.xml");
		FileWriter fileWriter = new FileWriter(cfgXmlFile);
		fileWriter.write(TEST_CFG_XML_STRING);
		fileWriter.close();
		File hbmXmlFile = new File(new File(url.toURI()), "Foo.hbm.xml");
		fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();

		String fooClassName = 
				"org.jboss.tools.hibernate.orm.runtime.exp.internal.IConfigurationTest$Foo";
		Metadata metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
		assertNull(metadata.getEntityBinding(fooClassName));
		Field metadataField = NativeConfiguration.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		metadataField.set(nativeConfigurationTarget, null);
		nativeConfigurationFacade.configure(cfgXmlFile);
		metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
		assertNotNull(metadata.getEntityBinding(fooClassName));
		assertTrue(cfgXmlFile.delete());
		assertTrue(hbmXmlFile.delete());

		// For reveng configuration
		try {
			revengConfigurationFacade.configure(cfgXmlFile);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'configure' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationFacade.configure(cfgXmlFile);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'configure' should not be called on instances of " + JpaConfiguration.class.getName());
		}
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
		
		// For native configuration
		String fooClassName = 
				"org.jboss.tools.hibernate.orm.runtime.exp.internal.IConfigurationTest$Foo";
		Metadata metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
		assertNull(metadata.getEntityBinding(fooClassName));
		Field metadataField = NativeConfiguration.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		metadataField.set(nativeConfigurationTarget, null);
		nativeConfigurationFacade.configure();
		metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
		assertNotNull(metadata.getEntityBinding(fooClassName));
		// For reveng configuration
		try {
			revengConfigurationFacade.configure();
			fail();
		} catch (RuntimeException e) {
			e.printStackTrace();
			assertEquals(
					e.getMessage(),
					"Method 'configure' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationFacade.configure();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'configure' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testAddClass() throws Exception {
		String fooHbmXmlFilePath = "org/jboss/tools/hibernate/orm/runtime/exp/internal";
		String fooHbmXmlFileName = "IConfigurationTest$Foo.hbm.xml";
		String fooClassName = 
				"org.jboss.tools.hibernate.orm.runtime.exp.internal.IConfigurationTest$Foo";
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		File hbmXmlFileDir = new File(new File(url.toURI()),fooHbmXmlFilePath);
		hbmXmlFileDir.deleteOnExit();
		hbmXmlFileDir.mkdirs();
		File hbmXmlFile = new File(hbmXmlFileDir, fooHbmXmlFileName);
		hbmXmlFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();

		// For native configuration		
		Metadata metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
		assertNull(metadata.getEntityBinding(fooClassName));
		Field metadataField = NativeConfiguration.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		metadataField.set(nativeConfigurationTarget, null);
		nativeConfigurationFacade.addClass(NEW_FACADE_FACTORY.createPersistentClass(Foo.class));
		metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
		assertNotNull(metadata.getEntityBinding(fooClassName));
		// For reveng configuration
		try {
			revengConfigurationFacade.addClass(NEW_FACADE_FACTORY.createPersistentClass(Foo.class));
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'addClass' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationFacade.addClass(NEW_FACADE_FACTORY.createPersistentClass(Foo.class));
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'addClass' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testBuildMappings() throws Exception {
		// For native configuration
		Field metadataField = nativeConfigurationTarget.getClass().getDeclaredField("metadata");
		metadataField.setAccessible(true);
		assertNull(metadataField.get(nativeConfigurationTarget));
		nativeConfigurationFacade.buildMappings();
		assertNotNull(metadataField.get(nativeConfigurationTarget));
		// For reveng configuration
		try {
			revengConfigurationFacade.buildMappings();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'buildMappings' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		metadataField = jpaConfigurationTarget.getClass().getDeclaredField("metadata");
		metadataField.setAccessible(true);
		assertNull(metadataField.get(jpaConfigurationTarget));
		jpaConfigurationFacade.buildMappings();
		assertNotNull(metadataField.get(jpaConfigurationTarget));
	}

	@Test
	public void testBuildSessionFactory() throws Throwable {
		// For native configuration
		ISessionFactory sessionFactoryFacade = 
				nativeConfigurationFacade.buildSessionFactory();
		assertNotNull(sessionFactoryFacade);
		Object sessionFactory = ((IFacade)sessionFactoryFacade).getTarget();
		assertNotNull(sessionFactory);
		assertTrue(sessionFactory instanceof SessionFactory);
		sessionFactoryFacade = null;
		assertNull(sessionFactoryFacade);
		sessionFactory = null;
		assertNull(sessionFactory);
		// For reveng configuration 
		try {
			revengConfigurationFacade.buildSessionFactory();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'buildSessionFactory' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		sessionFactoryFacade = jpaConfigurationFacade.buildSessionFactory();
		assertNotNull(sessionFactoryFacade);
		sessionFactory = ((IFacade)sessionFactoryFacade).getTarget();
		assertNotNull(sessionFactory);
		assertTrue(sessionFactory instanceof SessionFactory);
	}
	
	@Test
	public void testGetClassMappings() throws Exception {
		// For native configuration
		String fooHbmXmlFilePath = "org/jboss/tools/hibernate/orm/runtime/exp/internal";
		String fooHbmXmlFileName = "IConfigurationTest$Foo.hbm.xml";
		String fooClassName = 
				"org.jboss.tools.hibernate.orm.runtime.exp.internal.IConfigurationTest$Foo";
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		File hbmXmlFileDir = new File(new File(url.toURI()),fooHbmXmlFilePath);
		hbmXmlFileDir.deleteOnExit();
		hbmXmlFileDir.mkdirs();
		File hbmXmlFile = new File(hbmXmlFileDir, fooHbmXmlFileName);
		hbmXmlFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();
		nativeConfigurationTarget.addClass(Foo.class);
		Iterator<IPersistentClass> classMappings = nativeConfigurationFacade.getClassMappings();
		assertTrue(classMappings.hasNext());
		IPersistentClass fooClassFacade = classMappings.next();
		assertSame(fooClassFacade.getEntityName(), fooClassName);
		classMappings = null;
		assertNull(classMappings);
		// For reveng configuration
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		revengConfigurationTarget.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		classMappings = revengConfigurationFacade.getClassMappings();
		assertNotNull(classMappings);
		assertFalse(classMappings.hasNext());
		revengConfigurationTarget.readFromJDBC();
		classMappings = revengConfigurationFacade.getClassMappings();
		assertNotNull(classMappings);
		assertTrue(classMappings.hasNext());
		fooClassFacade = classMappings.next();
		assertEquals(fooClassFacade.getEntityName(), "Foo");
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
		classMappings = null;
		assertNull(classMappings);
		// For jpa configuration
		classMappings = jpaConfigurationFacade.getClassMappings();
		assertNotNull(classMappings);
		assertTrue(classMappings.hasNext());
		fooClassFacade = classMappings.next();
		assertEquals(fooClassFacade.getEntityName(), FooBar.class.getName());
	}
	
	@Test
	public void testSetPreferBasicCompositeIds() {
		// For native configuration 
		try {
			nativeConfigurationFacade.setPreferBasicCompositeIds(false);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setPreferBasicCompositeIds' should not be called on instances of " + NativeConfiguration.class.getName());
		}
		// For reveng configuration
		// the default is true
		assertTrue(revengConfigurationTarget.preferBasicCompositeIds());
		revengConfigurationFacade.setPreferBasicCompositeIds(false);
		assertFalse(revengConfigurationTarget.preferBasicCompositeIds());
		// For jpa configuration 
		try {
			jpaConfigurationFacade.setPreferBasicCompositeIds(false);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setPreferBasicCompositeIds' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testSetReverseEngineeringStrategy() {
		IReverseEngineeringStrategy strategyFacade = 
				NEW_FACADE_FACTORY.createReverseEngineeringStrategy();
		RevengStrategy reverseEngineeringStrategy = (RevengStrategy)((IFacade)strategyFacade).getTarget();
		// For native configuration 
		try {
			nativeConfigurationFacade.setReverseEngineeringStrategy(strategyFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setReverseEngineeringStrategy' should not be called on instances of " + NativeConfiguration.class.getName());
		}
		// For reveng configuration
		assertNotSame(
				reverseEngineeringStrategy,
				revengConfigurationTarget.getReverseEngineeringStrategy());
		revengConfigurationFacade.setReverseEngineeringStrategy(strategyFacade);
		assertSame(
				reverseEngineeringStrategy, 
				revengConfigurationTarget.getReverseEngineeringStrategy());
		// For jpa configuration
		try {
			jpaConfigurationFacade.setReverseEngineeringStrategy(strategyFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setReverseEngineeringStrategy' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testReadFromJDBC() throws Exception {
		// For native configuration 
		try {
			nativeConfigurationFacade.readFromJDBC();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'readFromJDBC' should not be called on instances of " + NativeConfiguration.class.getName());
		}
		// For reveng configuration
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		revengConfigurationTarget.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		Metadata metadata = revengConfigurationTarget.getMetadata();
		assertNull(metadata);
		revengConfigurationFacade.readFromJDBC();
		metadata = revengConfigurationTarget.getMetadata();
		Iterator<PersistentClass> iterator = metadata.getEntityBindings().iterator();
		PersistentClass persistentClass = iterator.next();
		assertEquals("Foo", persistentClass.getClassName());
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
		// For jpa configuration
		try {
			jpaConfigurationFacade.readFromJDBC();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'readFromJDBC' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testGetClassMapping() throws Exception {
		// For native configuration
		String fooHbmXmlFilePath = "org/jboss/tools/hibernate/orm/runtime/exp/internal";
		String fooHbmXmlFileName = "IConfigurationTest$Foo.hbm.xml";
		String fooClassName = 
				"org.jboss.tools.hibernate.orm.runtime.exp.internal.IConfigurationTest$Foo";
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		File hbmXmlFileDir = new File(new File(url.toURI()),fooHbmXmlFilePath);
		hbmXmlFileDir.deleteOnExit();
		hbmXmlFileDir.mkdirs();
		File hbmXmlFile = new File(hbmXmlFileDir, fooHbmXmlFileName);
		hbmXmlFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();
		Field metadataField = nativeConfigurationTarget.getClass().getDeclaredField("metadata");
		metadataField.setAccessible(true);
		assertNull(nativeConfigurationFacade.getClassMapping("Foo"));
		metadataField.set(nativeConfigurationTarget, null);
		nativeConfigurationTarget.addClass(Foo.class);
		assertNotNull(nativeConfigurationFacade.getClassMapping(fooClassName));
		// For reveng configuration
		assertNull(revengConfigurationFacade.getClassMapping("Foo"));
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		revengConfigurationTarget.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		revengConfigurationTarget.readFromJDBC();
		assertNotNull(revengConfigurationFacade.getClassMapping("Foo"));
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
		// For jpa configuration
		assertNull(jpaConfigurationFacade.getClassMapping("Bar"));
		assertNotNull(jpaConfigurationFacade.getClassMapping(FooBar.class.getName()));
	}

	@Test
	public void testGetNamingStrategy() {
		// For native configuration 
		NamingStrategy namingStrategy = new DefaultNamingStrategy();
		assertNull(nativeConfigurationFacade.getNamingStrategy());
		nativeConfigurationTarget.setNamingStrategy(namingStrategy);
		INamingStrategy namingStrategyFacade = nativeConfigurationFacade.getNamingStrategy();
		assertNotNull(namingStrategyFacade);
		Object namingStrategyTarget = ((IFacade)namingStrategyFacade).getTarget();
		assertSame(namingStrategyTarget, namingStrategy);
		// For reveng configuration 
		try {
			revengConfigurationFacade.getNamingStrategy();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'getNamingStrategy' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationFacade.getNamingStrategy();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'getNamingStrategy' should not be called on instances of " + JpaConfiguration.class.getName());
		}
		
	}
	
	@Test
	public void testGetEntityResolver() throws Exception {
		// For native configuration
		Field entityResolverField = nativeConfigurationTarget.getClass().getDeclaredField("entityResolver");
		entityResolverField.setAccessible(true);
		EntityResolver testResolver = new DefaultHandler();
		assertNotSame(testResolver, nativeConfigurationFacade.getEntityResolver());
		entityResolverField.set(nativeConfigurationTarget, testResolver);
		assertSame(testResolver, nativeConfigurationFacade.getEntityResolver());
		// For reveng configuration 
		try {
			revengConfigurationFacade.getEntityResolver();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'getEntityResolver' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration 
		try {
			jpaConfigurationFacade.getEntityResolver();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'getEntityResolver' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testGetTableMappings() throws Exception {
		// For native configuration
		String fooHbmXmlFilePath = "org/jboss/tools/hibernate/orm/runtime/exp/internal";
		String fooHbmXmlFileName = "IConfigurationTest$Foo.hbm.xml";
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		File hbmXmlFileDir = new File(new File(url.toURI()),fooHbmXmlFilePath);
		hbmXmlFileDir.deleteOnExit();
		hbmXmlFileDir.mkdirs();
		File hbmXmlFile = new File(hbmXmlFileDir, fooHbmXmlFileName);
		hbmXmlFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();
		nativeConfigurationTarget.addClass(Foo.class);
		Iterator<ITable> tableMappings = nativeConfigurationFacade.getTableMappings();
		assertTrue(tableMappings.hasNext());
		ITable fooTableFacade = tableMappings.next();
		assertEquals(fooTableFacade.getName(), "IConfigurationTest$Foo");
		tableMappings = null;
		assertNull(tableMappings);
		fooTableFacade = null;
		assertNull(fooTableFacade);
		// For reveng configuration
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		revengConfigurationTarget.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		tableMappings = revengConfigurationFacade.getTableMappings();
		assertNotNull(tableMappings);
		assertFalse(tableMappings.hasNext());
		revengConfigurationTarget.readFromJDBC();
		tableMappings = revengConfigurationFacade.getTableMappings();
		assertNotNull(tableMappings);
		assertTrue(tableMappings.hasNext());
		fooTableFacade = tableMappings.next();
		assertEquals(fooTableFacade.getName(), "FOO");
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
		tableMappings = null;
		assertNull(tableMappings);
		fooTableFacade = null;
		assertNull(fooTableFacade);
		// For jpa configuration
		tableMappings = jpaConfigurationFacade.getTableMappings();
		assertNotNull(tableMappings);
		assertTrue(tableMappings.hasNext());
		fooTableFacade = tableMappings.next();
		assertEquals(fooTableFacade.getName(), "IConfigurationTest$FooBar");
	}
	
	private void createPersistenceXml() throws Exception {
		File metaInf = new File(tempRoot, "META-INF");
		metaInf.mkdirs();
		File persistenceXml = new File(metaInf, "persistence.xml");
		persistenceXml.createNewFile();
		FileWriter fileWriter = new FileWriter(persistenceXml);
		fileWriter.write(PERSISTENCE_XML);
		fileWriter.close();
	}
	
	private void swapClassLoader() throws Exception {
		original = Thread.currentThread().getContextClassLoader();
		ClassLoader urlCl = URLClassLoader.newInstance(
				new URL[] { new URL(tempRoot.toURI().toURL().toString())} , 
				original);
		Thread.currentThread().setContextClassLoader(urlCl);
	}
	
	private void initializeFacadesAndTargets() {
		nativeConfigurationFacade = NEW_FACADE_FACTORY.createNativeConfiguration();
		nativeConfigurationTarget = (NativeConfiguration)((IFacade)nativeConfigurationFacade).getTarget();
		nativeConfigurationTarget.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		nativeConfigurationTarget.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		revengConfigurationFacade = NEW_FACADE_FACTORY.createRevengConfiguration();
		revengConfigurationTarget = (RevengConfiguration)((IFacade)revengConfigurationFacade).getTarget();
		jpaConfigurationFacade = NEW_FACADE_FACTORY.createJpaConfiguration("foobar", null);
		jpaConfigurationTarget = (JpaConfiguration)((IFacade)jpaConfigurationFacade).getTarget();	
	}
	
}
