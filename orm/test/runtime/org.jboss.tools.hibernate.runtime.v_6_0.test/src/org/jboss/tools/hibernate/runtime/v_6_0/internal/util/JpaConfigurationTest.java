package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.dialect.Dialect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class JpaConfigurationTest {
	
	private static final String PERSISTENCE_XML = 
			"<persistence version='2.2'" +
	        "  xmlns='http://xmlns.jcp.org/xml/ns/persistence'" +
		    "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
	        "  xsi:schemaLocation='http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd'>" +
	        "  <persistence-unit name='foobar'>" +
	        "    <class>"+ FooBar.class.getName()  +"</class>" +
	        "    <properties>" +
	        "      <property name='hibernate.dialect' value='" + TestDialect.class.getName() + "'/>" +
	        "      <property name='foo' value='bar'/>" +
	        "    </properties>" +
	        "  </persistence-unit>" +
			"</persistence>";
	
	private ClassLoader original = null;
	
	@TempDir
	public File tempRoot = new File("temproot");
	
	@BeforeEach
	public void beforeEach() throws Exception {
		File metaInf = new File(tempRoot, "META-INF");
		metaInf.mkdirs();
		File persistenceXml = new File(metaInf, "persistence.xml");
		persistenceXml.createNewFile();
		FileWriter fileWriter = new FileWriter(persistenceXml);
		fileWriter.write(PERSISTENCE_XML);
		fileWriter.close();
		original = Thread.currentThread().getContextClassLoader();
		ClassLoader urlCl = URLClassLoader.newInstance(
				new URL[] { new URL(tempRoot.toURI().toURL().toString())} , 
				original);
		Thread.currentThread().setContextClassLoader(urlCl);
	}
	
	@AfterEach
	public void afterEach() {
		Thread.currentThread().setContextClassLoader(original);
	}
	
	@Test
	public void testConstruction() {
		Properties properties = new Properties();
		properties.put("foo", "bar");
		JpaConfiguration jpaConfiguration = new JpaConfiguration("barfoo", properties);
		assertNotNull(jpaConfiguration);
		assertEquals("barfoo", jpaConfiguration.persistenceUnit);
		assertEquals("bar", jpaConfiguration.getProperties().get("foo"));
	}
	
	@Test
	public void testGetMetadata() {
		JpaConfiguration jpaConfiguration = new JpaConfiguration("foobar", null);
		assertNull(jpaConfiguration.metadata);
		Metadata metadata = jpaConfiguration.getMetadata();
		assertNotNull(metadata.getEntityBinding(FooBar.class.getName()));
		assertSame(metadata, jpaConfiguration.metadata);
	}
	
	@Test
	public void testBuildSessionFactory() {
		JpaConfiguration jpaConfiguration = new JpaConfiguration("foobar", null);
		assertNull(jpaConfiguration.sessionFactory);
		SessionFactory sessionFactory = jpaConfiguration.buildSessionFactory();
		assertNotNull(sessionFactory);
		assertSame(sessionFactory, jpaConfiguration.sessionFactory);
	}
	
	@Test
	public void testSetProperties() {
		Object dummy = Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { Metadata.class, SessionFactory.class },
				new InvocationHandler() {					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
				});
		JpaConfiguration jpaConfiguration = new JpaConfiguration("foobar", null);
		jpaConfiguration.metadata = (Metadata)dummy;
		jpaConfiguration.sessionFactory = (SessionFactory)dummy;
		assertNull(jpaConfiguration.getProperty("foo"));
		Properties properties = new Properties();
		properties.put("foo", "bar");
		Object result = jpaConfiguration.setProperties(properties);
		assertSame(result, jpaConfiguration);
		assertNull(jpaConfiguration.metadata);
		assertNull(jpaConfiguration.sessionFactory);
		assertEquals("bar", jpaConfiguration.getProperty("foo"));
	}
	
	@Test
	public void testAddProperties() {
		Object dummy = Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { Metadata.class, SessionFactory.class },
				new InvocationHandler() {					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
				});
		Properties properties = new Properties();
		properties.put("foo", "bar");
		JpaConfiguration jpaConfiguration = new JpaConfiguration("foobar", properties);
		jpaConfiguration.metadata = (Metadata)dummy;
		jpaConfiguration.sessionFactory = (SessionFactory)dummy;
		assertEquals("bar", jpaConfiguration.getProperty("foo"));
		assertNull(jpaConfiguration.getProperty("bar"));
		properties = new Properties();
		properties.put("bar", "foo");
		Object result = jpaConfiguration.addProperties(properties);
		assertSame(result, jpaConfiguration);
		assertNull(jpaConfiguration.metadata);
		assertNull(jpaConfiguration.sessionFactory);
		assertEquals("foo", jpaConfiguration.getProperty("bar"));
	}
	
	@Test
	public void testGetPersistenceUnit() {
		JpaConfiguration jpaConfiguration = new JpaConfiguration("barfoo", null);
		assertNotEquals("foobar", jpaConfiguration.getPersistenceUnit());
		jpaConfiguration.persistenceUnit = "foobar";
		assertEquals("foobar", jpaConfiguration.getPersistenceUnit());
	}
	
	@Test
	public void testInitialize() {
		JpaConfiguration jpaConfiguration = new JpaConfiguration("foobar", null);
		assertNull(jpaConfiguration.metadata);
		assertNull(jpaConfiguration.sessionFactory);
		assertNull(jpaConfiguration.getProperties().get("foo"));
		jpaConfiguration.initialize();
		assertNotNull(jpaConfiguration.metadata);
		assertNotNull(jpaConfiguration.metadata.getEntityBinding(FooBar.class.getName()));
		assertNotNull(jpaConfiguration.sessionFactory);
		assertEquals("bar", jpaConfiguration.sessionFactory.getProperties().get("foo"));
		assertEquals("bar", jpaConfiguration.getProperties().get("foo"));
	}
	
	public static final class TestDialect extends Dialect {
		@Override public int getVersion() { return 0; }
	}
	
	@Entity public class FooBar {
		@Id public int id;
	}

}
