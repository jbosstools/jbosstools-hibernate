package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractNamingStrategyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

public class ConfigurationFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private String methodName = null;
	private Object[] arguments = null;
	
	private IConfiguration configuration = null;
	
	@Before
	public void setUp() throws Exception {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setSuperclass(Configuration.class);
		Class<?> proxyClass = proxyFactory.createClass();
		ProxyObject proxy = (ProxyObject)proxyClass.newInstance();
		proxy.setHandler(new MethodHandler() {		
			@Override
			public Object invoke(
					Object self, 
					Method m, 
					Method proceed, 
					Object[] args) throws Throwable {
				if (methodName == null) {
					methodName = m.getName();
				}
				if (arguments == null) {
					arguments = args;
				}
				return proceed.invoke(self, args);
			}
		});
		configuration = new AbstractConfigurationFacade(FACADE_FACTORY, proxy) {};
		reset();
	}
	
	@Test
	public void testGetProperty() {
		configuration.setProperty("foo", "bar");
		reset();
		Assert.assertEquals("bar", configuration.getProperty("foo"));
		Assert.assertEquals("getProperty", methodName);
		Assert.assertArrayEquals(new Object[] { "foo" }, arguments);
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
		configuration.setProperty("foo", "bar");
		Assert.assertEquals("setProperty", methodName);
		Assert.assertArrayEquals(new Object[] { "foo", "bar" },  arguments);
		Assert.assertEquals("bar", configuration.getProperty("foo"));
	}
	
	@Test 
	public void testSetProperties() {
		Properties testProperties = new Properties();
		Assert.assertSame(configuration, configuration.setProperties(testProperties));
		Assert.assertEquals("setProperties", methodName);
		Assert.assertArrayEquals(new Object[] { testProperties }, arguments);
		Assert.assertSame(testProperties, configuration.getProperties());
	}
	
	@Test
	public void testSetEntityResolver() {
		EntityResolver testResolver = new DefaultHandler();
		configuration.setEntityResolver(testResolver);
		Assert.assertEquals("setEntityResolver", methodName);
		Assert.assertArrayEquals(new Object[] { testResolver }, arguments);
		Assert.assertSame(testResolver, configuration.getEntityResolver());
	}
	
	@Test
	public void testGetProperties() {
		Properties testProperties = new Properties();
		configuration.setProperties(testProperties);
		reset();
		Assert.assertSame(testProperties, configuration.getProperties());
		Assert.assertEquals("getProperties", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
	}
	
	@Test
	public void testSetNamingStrategy() {
		NamingStrategy dns = new DefaultNamingStrategy();
		INamingStrategy namingStrategy = new AbstractNamingStrategyFacade(FACADE_FACTORY, dns) {};
		configuration.setNamingStrategy(namingStrategy);
		Assert.assertEquals("setNamingStrategy", methodName);
		Assert.assertArrayEquals(new Object[] { dns }, arguments);
		Assert.assertSame(namingStrategy, configuration.getNamingStrategy());
	}
	
	@Test
	public void testAddProperties() {
		Assert.assertNull(configuration.getProperty("foo"));
		reset();
		Properties testProperties = new Properties();
		testProperties.put("foo", "bar");
		configuration.addProperties(testProperties);
		Assert.assertEquals("addProperties", methodName);
		Assert.assertArrayEquals(new Object[] { testProperties }, arguments);
		Assert.assertEquals("bar", configuration.getProperty("foo"));
	}
	
	@Test
	public void testConfigure() throws Exception {
		configuration.configure();
		Assert.assertEquals("configure", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
		reset();
		Document testDocument = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.newDocument();
		configuration.configure(testDocument);
		Assert.assertEquals("configure", methodName);
		Assert.assertArrayEquals(new Object[] { testDocument }, arguments);
		reset();
		File testFile = File.createTempFile("test", "tmp");
		configuration.configure(testFile);
		Assert.assertEquals("configure", methodName);
		Assert.assertArrayEquals(new Object[] { testFile }, arguments);
	}
	
	@Test 
	public void testCreateMappings() {
		Assert.assertNotNull(configuration.createMappings());
		Assert.assertEquals("createMappings", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
	}
	
	@Test
	public void testBuildMappings() {
		configuration.buildMappings();
		Assert.assertEquals("buildMappings", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
	}
	
	@Test
	public void testBuildSessionFactory() {
		Assert.assertNotNull(configuration.buildSessionFactory());
		Assert.assertEquals("buildSessionFactory", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
	}
	
	private void reset() {
		methodName = null;
		arguments = null;
	}

}
