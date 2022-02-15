package org.jboss.tools.hibernate.runtime.v_4_0;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class VersionTest {
	
	@Test
	public void testToolsVersion() {
		assertEquals("4.0.1.Final", org.hibernate.tool.Version.VERSION);
	}

	@Test
	public void testCoreVersion() {
		assertEquals("4.0.1.Final", org.hibernate.Version.getVersionString());
	}

}
