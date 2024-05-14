package org.jboss.tools.hibernate.orm.runtime.v_6_6;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.jboss.tools.hibernate.runtime.spi.RuntimeServiceManager;
import org.junit.jupiter.api.Test;

public class VersionTest {
	
	@Test 
	public void testCoreVersion() {
		assertEquals("6.6.0.Alpha1", org.hibernate.Version.getVersionString());
	}

	@Test
	public void testToolsVersion() {
		assertEquals("6.6.0.Alpha1", org.hibernate.tool.api.version.Version.CURRENT_VERSION);
	}
	
	@Test 
	public void testRuntimeVersion() {
		assertSame(RuntimeServiceManager.getInstance().findService("6.6").getClass(), ServiceImpl.class);
	}
}
