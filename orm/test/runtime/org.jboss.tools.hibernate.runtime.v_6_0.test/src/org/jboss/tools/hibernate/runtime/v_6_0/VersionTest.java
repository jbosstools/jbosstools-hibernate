package org.jboss.tools.hibernate.runtime.v_6_0;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class VersionTest {
	
	@Test
	public void testToolsVersion() {
		assertEquals("6.0.0.Beta3", org.hibernate.tool.api.version.Version.CURRENT_VERSION);
	}
	
	@Test 
	public void testCoreVersion() {
		assertEquals("6.0.0.Beta3", org.hibernate.Version.getVersionString());
	}

}
