package org.jboss.tools.hibernate.runtime.v_5_4.internal.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.junit.jupiter.api.Test;

public class DummyMetadataBuildingContextTest {
	
	@Test
	public void testInstance() {
		assertNotNull(DummyMetadataBuildingContext.INSTANCE);
		StandardServiceRegistry serviceRegistry = DummyMetadataBuildingContext.INSTANCE
				.getBootstrapContext().getServiceRegistry();
		JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
		Dialect dialect = jdbcServices.getDialect();
		assertTrue(dialect instanceof MockDialect);
	}

}
