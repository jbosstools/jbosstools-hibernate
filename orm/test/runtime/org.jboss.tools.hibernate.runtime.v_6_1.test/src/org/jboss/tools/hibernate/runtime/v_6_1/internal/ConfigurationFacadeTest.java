package org.jboss.tools.hibernate.runtime.v_6_1.internal;

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
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.DummyMetadataBuildingContext;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.MetadataHelper;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.MockConnectionProvider;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.MockDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigurationFacadeTest {

	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_6_1.internal'>" +
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
		INamingStrategy namingStrategy = FACADE_FACTORY.createNamingStrategy(new DefaultNamingStrategy());
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
				"org.jboss.tools.hibernate.runtime.v_6_1.internal.ConfigurationFacadeTest$Foo";
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
				"org.jboss.tools.hibernate.runtime.v_6_1.internal.ConfigurationFacadeTest$Foo";
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
				"org.jboss.tools.hibernate.runtime.v_6_1.internal.ConfigurationFacadeTest$Foo";
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

}