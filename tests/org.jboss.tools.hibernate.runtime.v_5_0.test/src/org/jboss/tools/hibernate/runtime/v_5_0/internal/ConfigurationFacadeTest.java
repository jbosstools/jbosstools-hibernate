package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.jboss.tools.hibernate.runtime.common.AbstractNamingStrategyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.v_5_0.test.MetadataHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
	
}
