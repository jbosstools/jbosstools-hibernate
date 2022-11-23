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
import java.lang.reflect.Field;
import java.net.URL;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

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
	
	private static final NewFacadeFactory NEW_FACADE_FACTORY = NewFacadeFactory.INSTANCE;

	static class Foo {
		public String id;
	}
	
	@BeforeAll
	public static void beforeAll() throws Exception {
		DriverManager.registerDriver(new Driver());		
	}

	private IConfiguration nativeConfigurationFacade = null;
	private NativeConfiguration nativeConfigurationTarget = null;
	private IConfiguration revengConfigurationFacade = null;
	private RevengConfiguration revengConfigurationTarget = null;

	@BeforeEach
	public void beforeEach() {
		nativeConfigurationFacade = NEW_FACADE_FACTORY.createNativeConfiguration();
		nativeConfigurationTarget = (NativeConfiguration)((IFacade)nativeConfigurationFacade).getTarget();
		nativeConfigurationTarget.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		nativeConfigurationTarget.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		revengConfigurationFacade = NEW_FACADE_FACTORY.createRevengConfiguration();
		revengConfigurationTarget = (RevengConfiguration)((IFacade)revengConfigurationFacade).getTarget();
	}	
	
	@Test
	public void testInstance() {
		assertNotNull(nativeConfigurationFacade);
	}

	@Test
	public void testGetProperty() {
		assertNull(nativeConfigurationFacade.getProperty("foo"));
		nativeConfigurationTarget.setProperty("foo", "bar");
		assertEquals("bar", nativeConfigurationFacade.getProperty("foo"));
	}

	
	@Test
	public void testAddFile() throws Exception {
		File testFile = File.createTempFile("test", "hbm.xml");
		PrintWriter printWriter = new PrintWriter(testFile);
		printWriter.write(TEST_HBM_XML_STRING);
		printWriter.close();
		MetadataSources metadataSources = MetadataHelper.getMetadataSources(nativeConfigurationTarget);
		assertTrue(metadataSources.getXmlBindings().isEmpty());
		assertSame(
				nativeConfigurationFacade,
				nativeConfigurationFacade.addFile(testFile));
		assertFalse(metadataSources.getXmlBindings().isEmpty());
		Binding<?> binding = metadataSources.getXmlBindings().iterator().next();
		assertEquals(testFile.getAbsolutePath(), binding.getOrigin().getName());
		assertTrue(testFile.delete());
	}
	
	@Test 
	public void testSetProperty() {
		assertNull(nativeConfigurationTarget.getProperty("foo"));
		nativeConfigurationFacade.setProperty("foo", "bar");
		assertEquals("bar", nativeConfigurationTarget.getProperty("foo"));
	}

	@Test 
	public void testSetProperties() {
		Properties testProperties = new Properties();
		assertNotSame(testProperties, nativeConfigurationTarget.getProperties());
		assertSame(
				nativeConfigurationFacade, 
				nativeConfigurationFacade.setProperties(testProperties));
		assertSame(testProperties, nativeConfigurationTarget.getProperties());
	}
	
	@Test
	public void testSetEntityResolver() throws Exception {
		EntityResolver testResolver = new DefaultHandler();
		Field entityResolverField = nativeConfigurationTarget.getClass().getDeclaredField("entityResolver");
		entityResolverField.setAccessible(true);
		assertNull(entityResolverField.get(nativeConfigurationTarget));
		nativeConfigurationFacade.setEntityResolver(testResolver);
		assertNotNull(entityResolverField.get(nativeConfigurationTarget));
		assertSame(testResolver, entityResolverField.get(nativeConfigurationTarget));
	}
	
	@Test
	public void testSetNamingStrategy() throws Exception {
		Field namingStrategyField = nativeConfigurationTarget.getClass().getDeclaredField("namingStrategy");
		namingStrategyField.setAccessible(true);
		INamingStrategy namingStrategyFacade = 
				NEW_FACADE_FACTORY.createNamingStrategy(DefaultNamingStrategy.class.getName());
		NamingStrategy namingStrategyTarget = (NamingStrategy)((IFacade)namingStrategyFacade).getTarget();
		assertNull(namingStrategyField.get(nativeConfigurationTarget));
		nativeConfigurationFacade.setNamingStrategy(namingStrategyFacade);
		assertNotNull(namingStrategyField.get(nativeConfigurationTarget));
		assertSame(namingStrategyField.get(nativeConfigurationTarget), namingStrategyTarget);
	}
	
	@Test
	public void testGetProperties() {
		Properties testProperties = new Properties();
		assertNotSame(testProperties, nativeConfigurationFacade.getProperties());
		nativeConfigurationTarget.setProperties(testProperties);
		assertSame(testProperties, nativeConfigurationFacade.getProperties());
	}
	
	@Test
	public void testAddProperties() {
		assertNull(nativeConfigurationTarget.getProperty("foo"));
		Properties testProperties = new Properties();
		testProperties.put("foo", "bar");
		nativeConfigurationFacade.addProperties(testProperties);
		assertEquals("bar", nativeConfigurationTarget.getProperty("foo"));
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
				"org.jboss.tools.hibernate.orm.runtime.exp.internal.IConfigurationTest$Foo";
		Metadata metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
		assertNull(metadata.getEntityBinding(fooClassName));
		nativeConfigurationFacade.configure(document);
		metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
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
				"org.jboss.tools.hibernate.orm.runtime.exp.internal.IConfigurationTest$Foo";
		Metadata metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
		assertNull(metadata.getEntityBinding(fooClassName));
		nativeConfigurationFacade.configure(cfgXmlFile);
		metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
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
				"org.jboss.tools.hibernate.orm.runtime.exp.internal.IConfigurationTest$Foo";
		Metadata metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
		assertNull(metadata.getEntityBinding(fooClassName));
		nativeConfigurationFacade.configure();
		metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
		assertNotNull(metadata.getEntityBinding(fooClassName));
	}
	
	@Test
	public void testAddClass() throws Exception {
		String fooHbmXmlFilePath = "org/jboss/tools/hibernate/orm/runtime/exp/internal";
		String fooHbmXmlFileName = "IConfigurationTest$Foo.hbm.xml";
		String fooClassName = 
				"org.jboss.tools.hibernate.orm.runtime.exp.internal.IConfigurationTest$Foo";
		Metadata metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
		assertNull(metadata.getEntityBinding(fooClassName));
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		File hbmXmlFileDir = new File(new File(url.toURI()),fooHbmXmlFilePath);
		hbmXmlFileDir.deleteOnExit();
		hbmXmlFileDir.mkdirs();
		File hbmXmlFile = new File(hbmXmlFileDir, fooHbmXmlFileName);
		hbmXmlFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();
		nativeConfigurationFacade.addClass(NEW_FACADE_FACTORY.createPersistentClass(Foo.class));
		metadata = MetadataHelper.getMetadata(nativeConfigurationTarget);
		assertNotNull(metadata.getEntityBinding(fooClassName));
	}
	
	@Test
	public void testBuildMappings() throws Exception {
		Field metadataField = nativeConfigurationTarget.getClass().getDeclaredField("metadata");
		metadataField.setAccessible(true);
		assertNull(metadataField.get(nativeConfigurationTarget));
		nativeConfigurationFacade.buildMappings();
		assertNotNull(metadataField.get(nativeConfigurationTarget));
	}

	@Test
	public void testBuildSessionFactory() throws Throwable {
		ISessionFactory sessionFactoryFacade = 
				nativeConfigurationFacade.buildSessionFactory();
		assertNotNull(sessionFactoryFacade);
		Object sessionFactory = ((IFacade)sessionFactoryFacade).getTarget();
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
	}
	
	@Test
	public void testSetPreferBasicCompositeIds() {
		// the default is true
		assertTrue(revengConfigurationTarget.preferBasicCompositeIds());
		revengConfigurationFacade.setPreferBasicCompositeIds(false);
		assertFalse(revengConfigurationTarget.preferBasicCompositeIds());
	}
	
	@Test
	public void testSetReverseEngineeringStrategy() {
		IReverseEngineeringStrategy strategyFacade = 
				NEW_FACADE_FACTORY.createReverseEngineeringStrategy();
		RevengStrategy reverseEngineeringStrategy = (RevengStrategy)((IFacade)strategyFacade).getTarget();
		assertNotSame(
				reverseEngineeringStrategy,
				revengConfigurationTarget.getReverseEngineeringStrategy());
		revengConfigurationFacade.setReverseEngineeringStrategy(strategyFacade);
		assertSame(
				reverseEngineeringStrategy, 
				revengConfigurationTarget.getReverseEngineeringStrategy());
	}
	
	@Test
	public void testReadFromJDBC() throws Exception {
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
	}

	@Test
	public void testGetNamingStrategy() {
		NamingStrategy namingStrategy = new DefaultNamingStrategy();
		assertNull(nativeConfigurationFacade.getNamingStrategy());
		nativeConfigurationTarget.setNamingStrategy(namingStrategy);
		INamingStrategy namingStrategyFacade = nativeConfigurationFacade.getNamingStrategy();
		assertNotNull(namingStrategyFacade);
		Object namingStrategyTarget = ((IFacade)namingStrategyFacade).getTarget();
		assertSame(namingStrategyTarget, namingStrategy);
		
	}
	
}
