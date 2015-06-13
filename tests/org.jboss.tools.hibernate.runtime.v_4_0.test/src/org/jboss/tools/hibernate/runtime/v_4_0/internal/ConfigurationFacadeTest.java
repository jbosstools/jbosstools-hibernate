package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.io.File;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigurationFacadeTest {
	
	private static final String PROPERTY = "TestConfiguration.PROPERTY";
	private static final Properties PROPERTIES = new Properties();

	private String methodName = null;
	private Object[] arguments = null;
	
	private IConfiguration configuration = null;
	
	@Before
	public void setUp() {
		methodName = null;
		arguments = null;
		configuration = new AbstractConfigurationFacade(null, new TestConfiguration()) {};
	}
	
	@Test
	public void testGetProperty() {
		Assert.assertSame(PROPERTY, configuration.getProperty("foobar"));
		Assert.assertEquals("getProperty", methodName);
		Assert.assertArrayEquals(new Object[] { "foobar" }, arguments);
	}
	
	@Test
	public void testAddFile() {
		File testFile = new File("");
		Assert.assertSame(configuration, configuration.addFile(testFile));
		Assert.assertEquals("addFile", methodName);
		Assert.assertArrayEquals(new Object[] { testFile }, arguments);
	}
	
	@Test
	public void testSetProperty() {
		configuration.setProperty("name", "value");
		Assert.assertEquals("setProperty", methodName);
		Assert.assertArrayEquals(new Object[] { "name", "value" },  arguments);
	}
	
	@Test 
	public void testSetProperties() {
		Properties testProperties = new Properties();
		Assert.assertSame(configuration, configuration.setProperties(testProperties));
		Assert.assertEquals("setProperties", methodName);
		Assert.assertArrayEquals(new Object[] { testProperties }, arguments);
	}
	
	@Test
	public void testSetEntityResolver() {
		EntityResolver testResolver = new DefaultHandler();
		configuration.setEntityResolver(testResolver);
		Assert.assertEquals("setEntityResolver", methodName);
		Assert.assertArrayEquals(new Object[] { testResolver }, arguments);
	}
	
	public void testGetProperties() {
		Assert.assertSame(PROPERTIES, configuration.getProperties());
		Assert.assertEquals("getProperties", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
	}
	
	@SuppressWarnings("serial")
	private class TestConfiguration extends Configuration {
		public String getProperty(String driver) {
			methodName = "getProperty";
			arguments = new Object[] { driver };
			return PROPERTY;
		}
		public Configuration addFile(File file) {
			methodName = "addFile";
			arguments = new Object[] { file };
			return this;
		}
		public Configuration setProperty(String name, String value) {
			methodName = "setProperty";
			arguments = new Object[] { name, value };
			return this;
		}
		public Configuration setProperties(Properties properties) {
			methodName = "setProperties";
			arguments = new Object[] { properties };
			return this;
		}
		public void setEntityResolver(EntityResolver entityResolver) {
			methodName = "setEntityResolver";
			arguments = new Object[] { entityResolver };
		}
		public Properties getProperties() {
			methodName = "getProperties";
			arguments = new Object[] {};
			return PROPERTIES;
		}
	}

}
