package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Test;

public class JpaConfigurationTest {
	
	@Test
	public void testConstruction() {
		Properties properties = new Properties();
		properties.put("foo", "bar");
		JpaConfiguration jpaConfiguration = new JpaConfiguration("barfoo", properties);
		assertNotNull(jpaConfiguration);
		assertEquals("barfoo", jpaConfiguration.persistenceUnit);
		assertEquals("bar", jpaConfiguration.getProperties().get("foo"));
	}

}
