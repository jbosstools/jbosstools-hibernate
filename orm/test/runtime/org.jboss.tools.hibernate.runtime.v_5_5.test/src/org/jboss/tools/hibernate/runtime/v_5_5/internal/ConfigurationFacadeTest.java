package org.jboss.tools.hibernate.runtime.v_5_5.internal;

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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.v_5_5.internal.util.JdbcMetadataConfiguration;
import org.jboss.tools.hibernate.runtime.v_5_5.internal.util.MetadataHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigurationFacadeTest {
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_5_5.internal'>" +
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
	
	public static class TestDialect extends Dialect {}
	
	static class Foo {
		public String id;
	}
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private IConfiguration configurationFacade = null;
	private Configuration configuration = null;

	@BeforeEach
	public void beforeEach() {
		configuration = new Configuration();
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
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
	public void testSetEntityResolver() throws Exception {
		EntityResolver testResolver = new DefaultHandler();
		ConfigurationFacadeImpl facade = (ConfigurationFacadeImpl)configurationFacade;
		Field entityResolverField = ConfigurationFacadeImpl.class.getDeclaredField("entityResolver");
		entityResolverField.setAccessible(true);
		assertNull(entityResolverField.get(facade));
		configurationFacade.setEntityResolver(testResolver);
		assertSame(testResolver, entityResolverField.get(facade));
	}
	
	@Test
	public void testSetNamingStrategy() throws Exception {
		INamingStrategy namingStrategy = FACADE_FACTORY.createNamingStrategy(new DefaultNamingStrategy());
		ConfigurationFacadeImpl facade = (ConfigurationFacadeImpl)configurationFacade;
		Field namingStrategyField = ConfigurationFacadeImpl.class.getDeclaredField("namingStrategy");
		namingStrategyField.setAccessible(true);
		assertNotSame(namingStrategy, namingStrategyField.get(facade));
		configurationFacade.setNamingStrategy(namingStrategy);
		assertSame(namingStrategy, namingStrategyField.get(facade));
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
				"org.jboss.tools.hibernate.runtime.v_5_5.internal.ConfigurationFacadeTest$Foo";
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
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
				"org.jboss.tools.hibernate.runtime.v_5_5.internal.ConfigurationFacadeTest$Foo";
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
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
				"org.jboss.tools.hibernate.runtime.v_5_5.internal.ConfigurationFacadeTest$Foo";
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
		Metadata metadata = MetadataHelper.getMetadata(configuration);
		assertNull(metadata.getEntityBinding(fooClassName));
		configurationFacade.configure();
		metadata = MetadataHelper.getMetadata(configuration);
		assertNotNull(metadata.getEntityBinding(fooClassName));
	}
	
	@Test
	public void testAddClass() throws Exception {
		PersistentClass persistentClass = new RootClass(null);
		persistentClass.setEntityName("Foo");
		IPersistentClass persistentClassFacade = 
				FACADE_FACTORY.createPersistentClass(persistentClass);	
		Field addedClassesField = ConfigurationFacadeImpl.class.getDeclaredField("addedClasses");
		addedClassesField.setAccessible(true);
		Collection<?> addedClasses = (Collection<?>)addedClassesField.get(configurationFacade);
		assertFalse(addedClasses.contains(persistentClassFacade));
		configurationFacade.addClass(persistentClassFacade);
		assertTrue(addedClasses.contains(persistentClassFacade));
	}
	
	@Test
	public void testGetMetadata() throws Exception {
		Field metadataField = ConfigurationFacadeImpl.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		NativeTestConfiguration nativeConfiguration = new NativeTestConfiguration();
		ConfigurationFacadeImpl nativeFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, nativeConfiguration);
		assertNull(metadataField.get(nativeFacade));
		Metadata nativeMetadata = nativeFacade.getMetadata();
		assertNotNull(nativeMetadata);
		assertSame(nativeMetadata, NativeTestConfiguration.METADATA);
		assertNotNull(metadataField.get(nativeFacade));
		assertSame(metadataField.get(nativeFacade), NativeTestConfiguration.METADATA);
		JdbcMetadataTestConfiguration jdbcConfiguration = new JdbcMetadataTestConfiguration();
		ConfigurationFacadeImpl jdbcFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, jdbcConfiguration);
		assertNull(metadataField.get(jdbcFacade));
		Metadata jdbcMetadata = jdbcFacade.getMetadata();
		assertNotNull(jdbcMetadata);
		assertSame(jdbcMetadata, JdbcMetadataTestConfiguration.METADATA);
		assertNotNull(metadataField.get(jdbcFacade));
		assertSame(metadataField.get(jdbcFacade), JdbcMetadataTestConfiguration.METADATA);
	}
	
	@Test
	public void testBuildMappings() throws Exception {
		Field metadataField = ConfigurationFacadeImpl.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
		assertNull(metadataField.get(configurationFacade));
		configurationFacade.buildMappings();
		assertNotNull(metadataField.get(configurationFacade));
	}

	@Test
	public void testBuildSessionFactory() throws Throwable {
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
		ISessionFactory sessionFactoryFacade = 
				configurationFacade.buildSessionFactory();
		assertNotNull(sessionFactoryFacade);
		Object sessionFactory = ((IFacade)sessionFactoryFacade).getTarget();
		assertNotNull(sessionFactory);
		assertTrue(sessionFactory instanceof SessionFactory);
	}
	
	@Test
	public void testGetClassMappings() throws Exception {
		Field addedClassesField = ConfigurationFacadeImpl.class.getDeclaredField("addedClasses");
		addedClassesField.setAccessible(true);
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
		assertFalse(configurationFacade.getClassMappings().hasNext());		
		PersistentClass persistentClass = new RootClass(null);
		persistentClass.setEntityName("Foo");
		IPersistentClass persistentClassFacade = 
				FACADE_FACTORY.createPersistentClass(persistentClass);	
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
		@SuppressWarnings("unchecked")
		List<IPersistentClass> addedClasses = (List<IPersistentClass>)addedClassesField.get(configurationFacade);
		addedClasses.add(persistentClassFacade);
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
	public void testGetNamingStrategy() throws Exception {
		INamingStrategy strategy = FACADE_FACTORY.createNamingStrategy(new DefaultNamingStrategy());
		Field namingStrategyField = ConfigurationFacadeImpl.class.getDeclaredField("namingStrategy");
		namingStrategyField.setAccessible(true);
		assertNull(configurationFacade.getNamingStrategy());
		namingStrategyField.set(configurationFacade, strategy);
		assertSame(strategy, configurationFacade.getNamingStrategy());
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
