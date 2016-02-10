package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.internal.SessionFactoryImpl;
import org.jboss.tools.hibernate.runtime.common.AbstractNamingStrategyFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractReverseEngineeringStrategyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IMapping;
import org.jboss.tools.hibernate.runtime.spi.IMappings;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ISettings;
import org.jboss.tools.hibernate.runtime.v_5_0.test.MetadataHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

public class ConfigurationFacadeTest {

	static class Foo { public int id; }
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private static final String TEST_HBM_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_5_0.internal'>" +
			"  <class name='ConfigurationFacadeTest$Foo'>" + 
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

	private HashMap<String, Object> called = null;

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
		called = new HashMap<String, Object>();
		configuration = (Configuration)createProxy(Configuration.class);
		configurationFacade = new ConfigurationFacadeImpl(
				FACADE_FACTORY, 
				configuration);
		
		configurationFacade.configure();
		Assert.assertEquals("configure", called.get("method"));
		Assert.assertArrayEquals(new Object[] {}, (Object[])called.get("args"));
		
		called.clear();
		File tempFile = File.createTempFile("temp.cfg", "xml");
		tempFile.deleteOnExit();
		FileWriter fw = new FileWriter(tempFile);
		fw.write(TEST_CONFIGURATION_STRING);
		fw.close();
		configurationFacade.configure(tempFile);
		Assert.assertEquals("configure", called.get("method"));
		Assert.assertArrayEquals(
				new Object[] { tempFile }, 
				(Object[])called.get("args"));
		tempFile.delete();
		
		called.clear();
		Document testDocument = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.newDocument();
		Element root = testDocument.createElement("hibernate-configuration");
		testDocument.appendChild(root);
		Element child = testDocument.createElement("session-factory");
		root.appendChild(child);
		configurationFacade.configure(testDocument);
		Assert.assertEquals("configure", called.get("method"));
		Object[] arguments = (Object[])called.get("args");
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
		Assert.assertNull(configurationFacade.mappings);
		IMappings mappings = configurationFacade.createMappings();
		Assert.assertNotNull(mappings);
		Assert.assertTrue(MappingsFacadeImpl.class.isInstance(mappings));
		MappingsFacadeImpl mappingsFacade = (MappingsFacadeImpl)mappings;
		Assert.assertSame(configurationFacade, mappingsFacade.configuration);
		Assert.assertSame(mappings, configurationFacade.mappings);
	}
	
	@Test
	public void testBuildMappings() {
		configurationFacade.setProperty(
				"hibernate.dialect", 
				"org.hibernate.dialect.H2Dialect");
		Assert.assertNull(configurationFacade.mappings);
		configurationFacade.buildMappings();
		Assert.assertNotNull(configurationFacade.mappings);
		Assert.assertTrue(
				MappingsFacadeImpl.class.isInstance(
						configurationFacade.mappings));
		MappingsFacadeImpl mappingsFacade = 
				(MappingsFacadeImpl)configurationFacade.mappings;
		Assert.assertSame(configurationFacade, mappingsFacade.configuration);
	}
	
	@Test
	public void testBuildSessionFactory() {
		// need to set 'hibernate.dialect' property for the session factory to properly build 
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		ISessionFactory sessionFactory = configurationFacade.buildSessionFactory();
		Assert.assertNotNull(sessionFactory);
		Assert.assertTrue(sessionFactory instanceof IFacade);
		Object sessionFactoryTarget = ((IFacade)sessionFactory).getTarget();
		Assert.assertNotNull(sessionFactoryTarget);
		Assert.assertTrue(sessionFactoryTarget instanceof SessionFactoryImpl);
	}
	
	@Test
	public void testBuildSettings() {
		// need to set 'hibernate.dialect' property for the session factory to properly build 
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		ISettings settings = configurationFacade.buildSettings();
		Assert.assertNotNull(settings);
		Assert.assertTrue(settings instanceof IFacade);
		Object settingsTarget = ((IFacade)settings).getTarget();
		Assert.assertNotNull(settingsTarget);
		Assert.assertTrue(settingsTarget instanceof Settings);
	}
	
	@Test
	public void testGetClassMappings() throws Exception {
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		Iterator<IPersistentClass> classMappings = 
				configurationFacade.getClassMappings();
		Assert.assertNotNull(configurationFacade.getClassMappings());
		Assert.assertFalse(classMappings.hasNext());

		File testFile = File.createTempFile("test", "tmp");
		testFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(testFile);
		fileWriter.write(TEST_HBM_STRING);
		fileWriter.close();
		configuration.addFile(testFile);
		configurationFacade = new ConfigurationFacadeImpl(
				FACADE_FACTORY, 
				configuration);
		classMappings = configurationFacade.getClassMappings();
		Assert.assertNotNull(configurationFacade.getClassMappings());
		Assert.assertTrue(classMappings.hasNext());
		IPersistentClass persistentClass = classMappings.next();
		Assert.assertEquals(
				"org.jboss.tools.hibernate.runtime.v_5_0.internal.ConfigurationFacadeTest$Foo", 
				persistentClass.getEntityName());
	}
	
	@Test
	public void testSetPreferBasicCompositeIds() throws Exception {
		called = new HashMap<String, Object>();
		configurationFacade = new ConfigurationFacadeImpl(
				FACADE_FACTORY, 
				(Configuration)createProxy(Configuration.class));
		configurationFacade.setPreferBasicCompositeIds(true);
		Assert.assertNull(called.get("method"));
		JDBCMetaDataConfiguration jdbcMetaDataConfiguration = 
				(JDBCMetaDataConfiguration)createProxy(JDBCMetaDataConfiguration.class);
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, jdbcMetaDataConfiguration);
		configurationFacade.setPreferBasicCompositeIds(false);
		Assert.assertEquals("setPreferBasicCompositeIds", called.get("method"));
		Assert.assertArrayEquals(
				new Object[] { false }, 
				(Object[])called.get("args"));
		Assert.assertFalse(jdbcMetaDataConfiguration.preferBasicCompositeIds());
		called.clear();
		configurationFacade.setPreferBasicCompositeIds(true);
		Assert.assertEquals("setPreferBasicCompositeIds", called.get("method"));
		Assert.assertArrayEquals(
				new Object[] { true }, 
				(Object[])called.get("args"));
		Assert.assertTrue(jdbcMetaDataConfiguration.preferBasicCompositeIds());
	}
	
	@Test
	public void testSetReverseEngineeringStrategy() throws Exception {
		called = new HashMap<String, Object>();
		configurationFacade = new ConfigurationFacadeImpl(
				FACADE_FACTORY, 
				(Configuration)createProxy(Configuration.class));
		ReverseEngineeringStrategy res = new DefaultReverseEngineeringStrategy();
		IReverseEngineeringStrategy strategy = 
				new AbstractReverseEngineeringStrategyFacade(FACADE_FACTORY, res) {};
		configurationFacade.setReverseEngineeringStrategy(strategy);
		Assert.assertNull(called.get("method"));
		Assert.assertNull(called.get("args"));

		JDBCMetaDataConfiguration jdbcMetaDataConfiguration = 
				(JDBCMetaDataConfiguration)createProxy(JDBCMetaDataConfiguration.class);
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, jdbcMetaDataConfiguration);
		Assert.assertNotSame(res, jdbcMetaDataConfiguration.getReverseEngineeringStrategy());
		called.clear();
		configurationFacade.setReverseEngineeringStrategy(strategy);
		Assert.assertEquals(
				"setReverseEngineeringStrategy", 
				called.get("method"));
		Assert.assertArrayEquals(
				new Object[] { ((IFacade)strategy).getTarget() }, 
				(Object[])called.get("args"));
		Assert.assertSame(res, jdbcMetaDataConfiguration.getReverseEngineeringStrategy());
	}
	
	@Test
	public void testReadFromJDBC() throws Exception {
		called = new HashMap<String, Object>();
		configurationFacade = new ConfigurationFacadeImpl(
				FACADE_FACTORY, 
				(Configuration)createProxy(Configuration.class));
		configurationFacade.readFromJDBC();
		Assert.assertNull(called.get("method"));
		Assert.assertNull(called.get("args"));

		Configuration jdbcMetaDataConfiguration = new JDBCMetaDataConfiguration() {
			@Override public void readFromJDBC() {
				called.put("method", "readFromJDBC");
				called.put("args", new Object[] {});
			}
		};
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, jdbcMetaDataConfiguration);
		configurationFacade.readFromJDBC();
		Assert.assertEquals("readFromJDBC", called.get("method"));
		Assert.assertArrayEquals(new Object[] {}, (Object[])called.get("args"));
	}
	
	@Test
	public void testBuildMapping() throws Exception {
		final Metadata md = (Metadata)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
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
		configuration = new Configuration() {
			@SuppressWarnings("unused")
			public Metadata metadata = md;
		};
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
		IMapping mapping = configurationFacade.buildMapping();
		Assert.assertNotNull(mapping);
		Assert.assertSame(md, ((IFacade)mapping).getTarget());
		Assert.assertSame(mapping, configurationFacade.buildMapping());
	}
	
	@Test
	public void testGetNamingStrategy() {
		Assert.assertNull(configurationFacade.getNamingStrategy());
		INamingStrategy namingStrategy = 
				new AbstractNamingStrategyFacade(FACADE_FACTORY,null) {};
		configurationFacade.namingStrategy = namingStrategy;
		Assert.assertSame(namingStrategy, configurationFacade.getNamingStrategy());
	}
		
	private Object createProxy(Class<?> clazz) throws Exception {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setSuperclass(clazz);
		Class<?> proxyClass = proxyFactory.createClass();
		ProxyObject proxy = (ProxyObject)proxyClass.newInstance();
		proxy.setHandler(new MethodHandler() {		
			@Override
			public Object invoke(
					Object self, 
					Method m, 
					Method proceed, 
					Object[] args) throws Throwable {
				if (called.get("method") == null) {
					called.put("method", m.getName());
				}
				if (called.get("args") == null) {
					called.put("args", args);
				}
				return proceed.invoke(self, args);
			}
		});
		return proxy;
	}
	
}
	
	
