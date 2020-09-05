package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
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
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.JdbcMetadataConfiguration;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.MetadataHelper;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.MetadataHelperTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigurationFacadeTest {

	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_6_0.internal'>" +
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
	
	public static class TestDialect extends Dialect {
		@Override public int getVersion() { return 0; }
	}
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private IConfiguration configurationFacade = null;
	private Configuration configuration = null;

	@Before
	public void setUp() {
		configuration = new Configuration();
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
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
				"org.jboss.tools.hibernate.runtime.v_6_0.internal.ConfigurationFacadeTest$Foo";
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
		Metadata metadata = MetadataHelper.getMetadata(configuration);
		Assert.assertNull(metadata.getEntityBinding(fooClassName));
		configurationFacade.configure();
		metadata = MetadataHelper.getMetadata(configuration);
		Assert.assertNotNull(metadata.getEntityBinding(fooClassName));
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
				"org.jboss.tools.hibernate.runtime.v_6_0.internal.ConfigurationFacadeTest$Foo";
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
		Metadata metadata = MetadataHelper.getMetadata(configuration);
		Assert.assertNull(metadata.getEntityBinding(fooClassName));
		configurationFacade.configure(document);
		metadata = MetadataHelper.getMetadata(configuration);
		Assert.assertNotNull(metadata.getEntityBinding(fooClassName));
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
	public void testBuildMappings() {
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
		assertNull(((ConfigurationFacadeImpl)configurationFacade).metadata);
		configurationFacade.buildMappings();
		assertNotNull(((ConfigurationFacadeImpl)configurationFacade).metadata);
	}

	
	@Test
	public void testBuildSessionFactory() throws Throwable {
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
		ISessionFactory sessionFactoryFacade = 
				configurationFacade.buildSessionFactory();
		Assert.assertNotNull(sessionFactoryFacade);
		Object sessionFactory = ((IFacade)sessionFactoryFacade).getTarget();
		Assert.assertNotNull(sessionFactory);
		Assert.assertTrue(sessionFactory instanceof SessionFactory);
	}
	
	@Test
	public void testGetAddedClasses() {
		ArrayList<IPersistentClass> list = new ArrayList<IPersistentClass>();
		assertNotNull(((ConfigurationFacadeImpl)configurationFacade).getAddedClasses());
		assertNotSame(((ConfigurationFacadeImpl)configurationFacade).getAddedClasses(), list);
		((ConfigurationFacadeImpl)configurationFacade).addedClasses = list;
		assertSame(((ConfigurationFacadeImpl)configurationFacade).getAddedClasses(), list);
	} 
	
	@Test
	public void testAddClass() {
		PersistentClass persistentClass = new RootClass(null);
		persistentClass.setEntityName("Foo");
		IPersistentClass persistentClassFacade = 
				FACADE_FACTORY.createPersistentClass(persistentClass);	
		assertFalse(((ConfigurationFacadeImpl)configurationFacade).addedClasses.contains(persistentClassFacade));
		configurationFacade.addClass(persistentClassFacade);
		assertTrue(((ConfigurationFacadeImpl)configurationFacade).addedClasses.contains(persistentClassFacade));
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
	public void testGetClassMapping() {
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
		PersistentClass persistentClass = new RootClass(null);
		persistentClass.setEntityName("Foo");
		IPersistentClass persistentClassFacade = 
				FACADE_FACTORY.createPersistentClass(persistentClass);	
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
		assertNull(configurationFacade.getClassMapping("Foo"));
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
		((ConfigurationFacadeImpl)configurationFacade).addedClasses.add(persistentClassFacade);
		assertNotNull(configurationFacade.getClassMapping("Foo"));
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
				MetadataHelperTest.class.getClassLoader(), 
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
