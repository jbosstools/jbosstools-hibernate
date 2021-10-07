package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MockDialectTest {
	
	@Test
	public void testGetVersion() {
		assertEquals(0, new MockDialect().getVersion());
	}

}
