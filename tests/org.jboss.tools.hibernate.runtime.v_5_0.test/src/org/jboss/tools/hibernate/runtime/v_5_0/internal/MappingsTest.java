package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import org.hibernate.cfg.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MappingsTest {
	
	private Configuration configuration;
	private Mappings mappings;
	
	@Before
	public void setUp() {
		configuration = new Configuration();
		configuration.setProperty(
				"hibernate.dialect", 
				"org.hibernate.dialect.H2Dialect");
		mappings = new Mappings();
	}
	
	@Test
	public void testAddClass() {
		Assert.assertNotNull(mappings);
	}

}
