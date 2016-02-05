package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.jboss.tools.hibernate.runtime.common.AbstractNamingStrategyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IMappings;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.v_5_0.test.MetadataHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigurationFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private static final String TEST_HBM_STRING =
			"<hibernate-mapping>" +
			"  <class name='Foo'>" + 
			"    <id name='id'/>" + 
			"  </class>" + 
			"</hibernate-mapping>";

	private static final String TEST_CONFIGURATION_STRING =
			"<!DOCTYPE hibernate-configuration PUBLIC" +
			"		\"-//Hibernate/Hibernate Configuration DTD 3.0//EN\"" +
			"		\"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd\">" +
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"  </session-factory>" + 
			"</hibernate-configuration>";

	private ConfigurationFacadeImpl configurationFacade = null;
	private Configuration configuration = null;

	@Before
	public void setUp() throws Exception {
		configuration = new Configuration();
		configurationFacade = new ConfigurationFacadeImpl(
				FACADE_FACTORY, 
				configuration);
	}
	
	@Test
	public void testGetProperty() {
		configuration.setProperty("foo", "bar");
		String foo = configurationFacade.getProperty("foo");
		Assert.assertEquals("bar", foo);
	}

	@Test
	public void testAddFile() throws Exception {
		File testFile = File.createTempFile("test", "tmp");
		testFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(testFile);
		fileWriter.write(TEST_HBM_STRING);
		fileWriter.close();
		configurationFacade.addFile(testFile);
		MetadataSources mds = MetadataHelper.getMetadataSources(configuration);
		Assert.assertEquals(1, mds.getXmlBindings().size());
		Binding<?> binding = mds.getXmlBindings().get(0);
		Assert.assertEquals(
				testFile.getAbsolutePath(), 
				binding.getOrigin().getName());
	}
	
	@Test
	public void testSetProperty() {
		configurationFacade.setProperty("foo", "bar");
		Assert.assertEquals("bar", configuration.getProperty("foo"));
	}
	
	@Test 
	public void testSetProperties() {
		Properties testProperties = new Properties();
		Assert.assertSame(
				configurationFacade, 
				configurationFacade.setProperties(testProperties));
		Assert.assertSame(testProperties, configuration.getProperties());
	}
	
	@Test
	public void testSetEntityResolver() {
		EntityResolver testResolver = new DefaultHandler();
		configurationFacade.setEntityResolver(testResolver);
		Assert.assertSame(testResolver, configurationFacade.entityResolver);
	}
	
	@Test
	public void testGetEntityResolver() {
		EntityResolver testResolver = new DefaultHandler();
		configurationFacade.entityResolver = testResolver;
		Assert.assertSame(testResolver, configurationFacade.getEntityResolver());
	}
	
	@Test
	public void testGetProperties() {
		Properties testProperties = new Properties();
		configuration.setProperties(testProperties);
		Assert.assertSame(
				testProperties, 
				configurationFacade.getProperties());
	}
	
	@Test
	public void testSetNamingStrategy() {
		DefaultNamingStrategy dns = new DefaultNamingStrategy();
		INamingStrategy namingStrategy = new AbstractNamingStrategyFacade(FACADE_FACTORY, dns) {};
		configurationFacade.setNamingStrategy(namingStrategy);
		Assert.assertSame(
				namingStrategy, 
				configurationFacade.namingStrategy);
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
	public void testConfigure() throws Exception {
		final HashMap<String, Object[]> invoked = new HashMap<String, Object[]>();
		configuration = new Configuration() {
			@Override
			public Configuration configure() {
				invoked.put("configure", new Object[] {});
				return super.configure();
			}
			@Override
			public Configuration configure(File file) {
				invoked.put("configure", new Object[] { file });
				return super.configure(file);
			}
		};
		configurationFacade = new ConfigurationFacadeImpl(
				FACADE_FACTORY, 
				configuration);
		
		Assert.assertNull(invoked.get("configure"));
		configurationFacade.configure();
		Assert.assertArrayEquals(new Object[] {}, invoked.get("configure"));
		
		invoked.clear();
		File tempFile = File.createTempFile("temp.cfg", "xml");
		tempFile.deleteOnExit();
		FileWriter fw = new FileWriter(tempFile);
		fw.write(TEST_CONFIGURATION_STRING);
		fw.close();
		Assert.assertNull(invoked.get("configure"));
		configurationFacade.configure(tempFile);
		Assert.assertArrayEquals(
				new Object[] { tempFile }, 
				invoked.get("configure"));
		tempFile.delete();
		
		invoked.clear();
		Document testDocument = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.newDocument();
		Element root = testDocument.createElement("hibernate-configuration");
		testDocument.appendChild(root);
		Element child = testDocument.createElement("session-factory");
		root.appendChild(child);
		Assert.assertNull(invoked.get("configure"));
		configurationFacade.configure(testDocument);
		Object[] arguments = invoked.get("configure");
		Assert.assertNotNull(arguments);
		Assert.assertTrue(arguments.length == 1);
		Object arg = arguments[0];
		Assert.assertTrue(File.class.isInstance(arg));
		File file = (File)arg;
		Assert.assertFalse(file.exists());
	}
	
	@Test 
	public void testCreateMappings() {
		configurationFacade.setProperty(
				"hibernate.dialect", 
				"org.hibernate.dialect.H2Dialect");
		IMappings mappings = configurationFacade.createMappings();
		Assert.assertNotNull(mappings);
		Assert.assertTrue(MappingsFacadeImpl.class.isInstance(mappings));
		MappingsFacadeImpl mappingsFacade = (MappingsFacadeImpl)mappings;
		Assert.assertSame(configurationFacade, mappingsFacade.configuration);
		Assert.assertSame(mappings, configurationFacade.mappings);
	}
	
}
