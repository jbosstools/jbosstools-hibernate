package org.jboss.tools.hibernate.orm.runtime.exp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class VersionTest {
	
	@Test 
	public void testCoreVersion() {
		assertEquals("6.1.5.Final", org.hibernate.Version.getVersionString());
	}

	@Test
	public void testToolsVersion() {
		assertEquals("6.1.5-SNAPSHOT", org.hibernate.tool.api.version.Version.CURRENT_VERSION);
	}
	
}
