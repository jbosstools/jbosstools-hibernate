package org.jboss.tools.hibernate.runtime.v_6_2;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ExportersTest {
	
	@Test
	public void testExportersAvailable() throws Exception {
		assertNotNull(Class.forName("org.hibernate.tool.hbm2x.HibernateConfigurationExporter"));
		assertNotNull(Class.forName("org.hibernate.tool.hbm2x.HibernateMappingExporter"));
		assertNotNull(Class.forName("org.hibernate.tool.hbm2x.POJOExporter"));
	}
	
}
