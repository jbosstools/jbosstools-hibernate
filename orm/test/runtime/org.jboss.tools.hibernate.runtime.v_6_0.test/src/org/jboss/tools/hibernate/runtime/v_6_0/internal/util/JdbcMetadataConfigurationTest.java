package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import static org.junit.Assert.assertNotNull;

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
		assertNotNull(jdbcMetadataConfiguration.properties);
	}

}
