package org.jboss.tools.hibernate.orm.runtime.v_7_0;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class VersionTest {
	
	@Test 
	public void testCoreVersion() {
		assertEquals("7.0.0.Alpha1", org.hibernate.Version.getVersionString());
	}

	@Test
	public void testToolsVersion() {
		assertEquals("7.0.0.Alpha1", org.hibernate.tool.api.version.Version.CURRENT_VERSION);
	}
	
}
