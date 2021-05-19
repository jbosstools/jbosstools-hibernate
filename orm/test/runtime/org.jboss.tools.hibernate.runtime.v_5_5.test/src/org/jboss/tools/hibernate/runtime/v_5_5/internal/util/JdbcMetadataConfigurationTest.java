package org.jboss.tools.hibernate.runtime.v_5_5.internal.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JdbcMetadataConfigurationTest {

	private JdbcMetadataConfiguration jdbcMetadataConfiguration = null;
	
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
	
}
