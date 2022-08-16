package org.jboss.tools.hibernate.runtime.v_5_6;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class VersionTest {
	
	@Test
	public void testToolsVersion() {
		assertEquals("5.6.10.Final", org.hibernate.tool.Version.VERSION);
	}

	@Test
	public void testCoreVersion() {
		assertEquals("5.6.10.Final", org.hibernate.Version.getVersionString());
	}


}
