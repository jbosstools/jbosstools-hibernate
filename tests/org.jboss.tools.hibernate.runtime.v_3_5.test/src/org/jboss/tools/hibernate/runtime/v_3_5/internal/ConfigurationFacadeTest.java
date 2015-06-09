package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.io.File;

import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationFacadeTest {
	
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
		Assert.assertSame(TestConfiguration.PROPERTY, configuration.getProperty("foobar"));
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
	
	@SuppressWarnings("serial")
	private class TestConfiguration extends Configuration {
		static final String PROPERTY = "TestConfiguration.PROPERTY";
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
	}

}
