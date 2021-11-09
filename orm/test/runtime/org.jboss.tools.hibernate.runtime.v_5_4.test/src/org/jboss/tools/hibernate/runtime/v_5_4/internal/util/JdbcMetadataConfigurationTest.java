package org.jboss.tools.hibernate.runtime.v_5_4.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import org.h2.Driver;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JdbcMetadataConfigurationTest {

	private JdbcMetadataConfiguration jdbcMetadataConfiguration = null;
	
	@BeforeAll
	public static void beforeAll() throws Exception {
		DriverManager.registerDriver(new Driver());		
	}

	@BeforeEach
	public void beforeEach() {
		jdbcMetadataConfiguration = new JdbcMetadataConfiguration();
	}
	
	@Test
	public void testGetProperties() throws Exception {
		Properties properties = new Properties();
		Field propertiesField = JdbcMetadataConfiguration.class.getDeclaredField("properties");
		propertiesField.setAccessible(true);	
		assertNotNull(jdbcMetadataConfiguration.getProperties());
		assertNotSame(properties,  jdbcMetadataConfiguration.getProperties());
		propertiesField.set(jdbcMetadataConfiguration, properties);
		assertSame(properties, jdbcMetadataConfiguration.getProperties());
	}
	
	@Test
	public void testSetProperties() throws Exception {
		Properties properties = new Properties();
		Field propertiesField = JdbcMetadataConfiguration.class.getDeclaredField("properties");
		propertiesField.setAccessible(true);	
		assertNotNull(propertiesField.get(jdbcMetadataConfiguration));
		assertNotSame(properties,  propertiesField.get(jdbcMetadataConfiguration));
		jdbcMetadataConfiguration.setProperties(properties);
		assertSame(properties, propertiesField.get(jdbcMetadataConfiguration));
	}
	
	@Test
	public void testGetProperty() throws Exception {
		assertNull(jdbcMetadataConfiguration.getProperty("foo"));
		Field propertiesField = JdbcMetadataConfiguration.class.getDeclaredField("properties");
		propertiesField.setAccessible(true);
		Properties properties = (Properties)propertiesField.get(jdbcMetadataConfiguration);
		properties.put("foo", "bar");
		assertEquals("bar", jdbcMetadataConfiguration.getProperty("foo"));
	}

	@Test
	public void testSetProperty() throws Exception {
		Field propertiesField = JdbcMetadataConfiguration.class.getDeclaredField("properties");
		propertiesField.setAccessible(true);
		Properties properties = (Properties)propertiesField.get(jdbcMetadataConfiguration);
		assertNull(properties.get("foo"));
		jdbcMetadataConfiguration.setProperty("foo", "bar");
		assertEquals("bar", properties.get("foo"));
	}
	
	@Test
	public void testAddProperties() throws Exception {
		Field propertiesField = JdbcMetadataConfiguration.class.getDeclaredField("properties");
		propertiesField.setAccessible(true);
		Properties properties = (Properties)propertiesField.get(jdbcMetadataConfiguration);
		Properties addedProperties = new Properties();
		addedProperties.put("foo", "bar");
		assertNull(properties.get("foo"));
		jdbcMetadataConfiguration.addProperties(addedProperties);
		assertEquals("bar", properties.get("foo"));
	}
	
	@Test
	public void testGetReverseEngineeringStrategy() throws Exception {
		Field revengStrategyField = JdbcMetadataConfiguration.class.getDeclaredField("res");
		revengStrategyField.setAccessible(true);
		ReverseEngineeringStrategy strategy = new DefaultReverseEngineeringStrategy();
		assertNull(jdbcMetadataConfiguration.getReverseEngineeringStrategy());
		revengStrategyField.set(jdbcMetadataConfiguration, strategy);
		assertSame(strategy, jdbcMetadataConfiguration.getReverseEngineeringStrategy());
	}
	
	@Test
	public void testSetReverseEngineeringStrategy() throws Exception {
		Field revengStrategyField = JdbcMetadataConfiguration.class.getDeclaredField("res");
		revengStrategyField.setAccessible(true);
		ReverseEngineeringStrategy strategy = new DefaultReverseEngineeringStrategy();
		assertNull(revengStrategyField.get(jdbcMetadataConfiguration));
		jdbcMetadataConfiguration.setReverseEngineeringStrategy(strategy);
		assertSame(strategy, revengStrategyField.get(jdbcMetadataConfiguration));
	}
	
	@Test
	public void testPreferBasicCompositeIds() throws Exception {
		Field preferBasicCompositeField = JdbcMetadataConfiguration.class.getDeclaredField("preferBasicCompositeIds");
		preferBasicCompositeField.setAccessible(true);
		assertTrue(jdbcMetadataConfiguration.preferBasicCompositeIds());
		preferBasicCompositeField.set(jdbcMetadataConfiguration, false);
		assertFalse(jdbcMetadataConfiguration.preferBasicCompositeIds());
	}
	
	@Test
	public void testSetPreferBasicCompositeIds() throws Exception {
		Field preferBasicCompositeField = JdbcMetadataConfiguration.class.getDeclaredField("preferBasicCompositeIds");
		preferBasicCompositeField.setAccessible(true);
		assertTrue((boolean)preferBasicCompositeField.get(jdbcMetadataConfiguration));
		jdbcMetadataConfiguration.setPreferBasicCompositeIds(false);
		assertFalse((boolean)preferBasicCompositeField.get(jdbcMetadataConfiguration));
	}
	
	@Test
	public void testGetMetadata() throws Exception {
		Field metadataField = JdbcMetadataConfiguration.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		Metadata metadata = (Metadata)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { Metadata.class }, 
				new InvocationHandler() {					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
				});
		assertNull(jdbcMetadataConfiguration.getMetadata());
		metadataField.set(jdbcMetadataConfiguration, metadata);
		assertSame(metadata, jdbcMetadataConfiguration.getMetadata());
	}
	
	@Test
	public void testReadFromJdbc() throws Exception {
		Field metadataField = JdbcMetadataConfiguration.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		jdbcMetadataConfiguration.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		jdbcMetadataConfiguration.setReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
		assertNull(metadataField.get(jdbcMetadataConfiguration));
		jdbcMetadataConfiguration.readFromJDBC();
		assertNotNull(metadataField.get(jdbcMetadataConfiguration));
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
	}

}
