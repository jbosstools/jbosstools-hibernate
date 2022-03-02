package org.jboss.tools.hibernate.runtime.v_3_6;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class VersionTest {
	
	@Test
	public void testToolsVersion() {
		assertEquals("3.6.2.Final", org.hibernate.tool.Version.VERSION);
	}

	@Test
	public void testCoreVersion() {
		assertEquals("3.6.10.Final", org.hibernate.Version.getVersionString());
	}

}
