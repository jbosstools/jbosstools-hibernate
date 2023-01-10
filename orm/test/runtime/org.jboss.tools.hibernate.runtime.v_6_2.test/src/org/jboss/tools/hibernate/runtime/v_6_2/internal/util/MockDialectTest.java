package org.jboss.tools.hibernate.runtime.v_6_2.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.dialect.DatabaseVersion;
import org.junit.jupiter.api.Test;

public class MockDialectTest {
	
	@Test
	public void testGetVersion() {
		DatabaseVersion version = new MockDialect().getVersion();
		assertEquals(Integer.MAX_VALUE, version.getDatabaseMajorVersion());
		assertEquals(Integer.MIN_VALUE, version.getDatabaseMinorVersion());
	}

}
