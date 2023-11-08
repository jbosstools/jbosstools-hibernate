package org.jboss.tools.hibernate.orm.runtime.v_6_4;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class VersionTest {
	
	@Test 
	public void testCoreVersion() {
		assertEquals("6.4.0.CR1", org.hibernate.Version.getVersionString());
	}

}
