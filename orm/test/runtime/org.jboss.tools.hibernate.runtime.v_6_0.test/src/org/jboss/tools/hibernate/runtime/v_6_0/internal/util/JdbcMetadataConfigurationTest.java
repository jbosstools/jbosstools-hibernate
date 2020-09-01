package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.hibernate.tool.api.metadata.MetadataConstants;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.junit.Before;
import org.junit.Test;

public class JdbcMetadataConfigurationTest {
	
	private JdbcMetadataConfiguration jdbcMetadataConfiguration = null;
	
	@Before
	public void before() {
		jdbcMetadataConfiguration = new JdbcMetadataConfiguration();
	}
	
	@Test
	public void testGetProperties() {
		Properties properties = new Properties();
		assertNotNull(jdbcMetadataConfiguration.properties);
		assertNotSame(properties,  jdbcMetadataConfiguration.getProperties());
		jdbcMetadataConfiguration.properties = properties;
		assertSame(properties, jdbcMetadataConfiguration.getProperties());
	}
	
	@Test
	public void testSetProperties() {
		Properties properties = new Properties();
		assertNotNull(jdbcMetadataConfiguration.properties);
		assertNotSame(properties,  jdbcMetadataConfiguration.getProperties());
		jdbcMetadataConfiguration.setProperties(properties);
		assertSame(properties, jdbcMetadataConfiguration.getProperties());
	}
	
	@Test
	public void testGetProperty() {
		assertNull(jdbcMetadataConfiguration.getProperty("foo"));
		jdbcMetadataConfiguration.properties.put("foo", "bar");
		assertEquals("bar", jdbcMetadataConfiguration.getProperty("foo"));
	}

	@Test
	public void testSetProperty() {
		assertNull(jdbcMetadataConfiguration.properties.get("foo"));
		jdbcMetadataConfiguration.setProperty("foo", "bar");
		assertEquals("bar", jdbcMetadataConfiguration.properties.get("foo"));
	}
	
	@Test
	public void testAddProperties() {
		Properties properties = new Properties();
		properties.put("foo", "bar");
		assertNull(jdbcMetadataConfiguration.properties.get("foo"));
		jdbcMetadataConfiguration.addProperties(properties);
		assertEquals("bar", jdbcMetadataConfiguration.properties.get("foo"));
	}
	
	@Test
	public void testGetReverseEngineeringStrategy() {
		RevengStrategy strategy = new DefaultStrategy();
		assertNull(jdbcMetadataConfiguration.getReverseEngineeringStrategy());
		jdbcMetadataConfiguration.revengStrategy = strategy;
		assertSame(strategy, jdbcMetadataConfiguration.getReverseEngineeringStrategy());
	}
	
	@Test
	public void testSetReverseEngineeringStrategy() {
		RevengStrategy strategy = new DefaultStrategy();
		assertNull(jdbcMetadataConfiguration.revengStrategy);
		jdbcMetadataConfiguration.setReverseEngineeringStrategy(strategy);
		assertSame(strategy, jdbcMetadataConfiguration.revengStrategy);
	}
	
	@Test
	public void testPreferBasicCompositeIds() {
		assertFalse(jdbcMetadataConfiguration.preferBasicCompositeIds());
		jdbcMetadataConfiguration.properties.put(MetadataConstants.PREFER_BASIC_COMPOSITE_IDS, true);
		assertTrue(jdbcMetadataConfiguration.preferBasicCompositeIds());
	}
	
	@Test
	public void testSetPreferBasicCompositeIds() {
		assertNull(
				jdbcMetadataConfiguration.properties.get(
						MetadataConstants.PREFER_BASIC_COMPOSITE_IDS));
		jdbcMetadataConfiguration.setPreferBasicCompositeIds(true);
		assertEquals(
				true, 
				jdbcMetadataConfiguration.properties.get(
						MetadataConstants.PREFER_BASIC_COMPOSITE_IDS));
	}

}
