package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractNamingStrategyFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractReverseEngineeringStrategyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ConfigurationFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private String methodName = null;
	private Object[] arguments = null;
	
	private IConfiguration configuration = null;
	
	@Before
	public void setUp() {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(Configuration.class);
		enhancer.setCallback(new MethodInterceptor() {
			@Override
			public Object intercept(
					Object obj, 
					Method method, 
					Object[] args, 
					MethodProxy proxy) throws Throwable {
				if (methodName == null) {
					methodName = method.getName();
				}
				if (arguments == null) {
					arguments = args;
				}
				return proxy.invokeSuper(obj, args);
			}					
		});
		configuration = new AbstractConfigurationFacade(FACADE_FACTORY, enhancer.create()) {};
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
	public void testGetEntityResolver() {
		EntityResolver testResolver = new DefaultHandler();
		configuration.setEntityResolver(testResolver);
		reset();
		Assert.assertSame(testResolver, configuration.getEntityResolver());
		Assert.assertEquals("getEntityResolver", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
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
		Properties testProperties = new Properties();
		testProperties.put("foo", "bar");
		reset();
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
	
	@Test
	public void testBuildSettings() {
		Assert.assertNotNull(configuration.buildSettings());
		Assert.assertEquals("buildSettings", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
	}
	
	@Test
	public void testGetClassMappings() {
		Assert.assertNotNull(configuration.getClassMappings());
		Assert.assertEquals("getClassMappings", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
	}
	
	@Test
	public void testSetPreferBasicCompositeIds() {
		configuration.setPreferBasicCompositeIds(true);
		Assert.assertNull(methodName);
		Assert.assertNull(arguments);
		JDBCMetaDataConfiguration jdbcMetaDataConfiguration = new JDBCMetaDataConfiguration();
		configuration = new AbstractConfigurationFacade(FACADE_FACTORY, jdbcMetaDataConfiguration) {};
		configuration.setPreferBasicCompositeIds(false);
		Assert.assertFalse(jdbcMetaDataConfiguration.preferBasicCompositeIds());
		configuration.setPreferBasicCompositeIds(true);
		Assert.assertTrue(jdbcMetaDataConfiguration.preferBasicCompositeIds());
	}
	
	@Test
	public void testSetReverseEngineeringStrategy() {
		ReverseEngineeringStrategy res = new DefaultReverseEngineeringStrategy();
		IReverseEngineeringStrategy strategy = 
				new AbstractReverseEngineeringStrategyFacade(FACADE_FACTORY, res) {};
		configuration.setReverseEngineeringStrategy(strategy);
		Assert.assertNull(methodName);
		Assert.assertNull(arguments);
		JDBCMetaDataConfiguration jdbcMetaDataConfiguration = new JDBCMetaDataConfiguration();
		configuration = new AbstractConfigurationFacade(FACADE_FACTORY, jdbcMetaDataConfiguration) {};
		Assert.assertNotSame(res, jdbcMetaDataConfiguration.getReverseEngineeringStrategy());
		configuration.setReverseEngineeringStrategy(strategy);
		Assert.assertSame(res, jdbcMetaDataConfiguration.getReverseEngineeringStrategy());
	}
	
	private void reset() {
		methodName = null;
		arguments = null;
	}

}
