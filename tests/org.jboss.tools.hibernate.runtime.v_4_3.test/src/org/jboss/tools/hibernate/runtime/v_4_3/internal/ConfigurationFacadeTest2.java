package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import java.io.File;
import java.io.PrintWriter;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.Mappings;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IMappings;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigurationFacadeTest2 {
	
	private static final String TEST_HBM_XML_STRING =
			"<!DOCTYPE hibernate-mapping PUBLIC" +
			"		'-//Hibernate/Hibernate Mapping DTD 3.0//EN'" +
			"		'http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd'>" +
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_4_3.internal'>" +
			"  <class name='ConfigurationFacadeTest2$Foo'>" + 
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
		String fooClassName = 
				"org.jboss.tools.hibernate.runtime.v_4_3.internal.ConfigurationFacadeTest2$Foo";
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
		DefaultNamingStrategy dns = new DefaultNamingStrategy();
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
		String fooClassName = 
				"org.jboss.tools.hibernate.runtime.v_4_3.internal.test.Foo";
		Assert.assertNull(configuration.getClassMapping(fooClassName));
		configurationFacade.configure();
		Assert.assertNotNull(configuration.getClassMapping(fooClassName));
	}
	
	@Test 
	public void testCreateMappings() {
		configuration.setProperty("createMappingsProperty", "a lot of blabla");
		IMappings mappingsFacade = configurationFacade.createMappings();
		Assert.assertNotNull(mappingsFacade);
		Object object = ((IFacade)mappingsFacade).getTarget();
		Assert.assertTrue(object instanceof Mappings);
		Mappings mappings = (Mappings)object;
		Assert.assertEquals(
				"a lot of blabla", 
				mappings.getConfigurationProperties().get("createMappingsProperty"));
	}

}
