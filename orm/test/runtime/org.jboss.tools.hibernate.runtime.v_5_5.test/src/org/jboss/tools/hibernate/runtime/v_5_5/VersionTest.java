package org.jboss.tools.hibernate.runtime.v_5_5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class VersionTest {
	
	@Test
	public void testToolsVersion() {
		assertEquals("5.5.0.Beta1", org.hibernate.tool.Version.VERSION);
	}

	@Test
	public void testCoreVersion() {
		assertEquals("5.5.0.Beta1", org.hibernate.Version.getVersionString());
	}


}
