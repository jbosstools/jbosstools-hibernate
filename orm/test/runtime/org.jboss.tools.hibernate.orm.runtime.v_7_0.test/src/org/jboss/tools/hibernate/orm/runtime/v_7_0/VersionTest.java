package org.jboss.tools.hibernate.orm.runtime.v_7_0;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.jboss.tools.hibernate.runtime.spi.RuntimeServiceManager;
import org.junit.jupiter.api.Test;

public class VersionTest {
	
	@Test 
	public void testCoreVersion() {
		assertEquals("7.0.0.Alpha3", org.hibernate.Version.getVersionString());
	}

	@Test
	public void testToolsVersion() {
		assertEquals("7.0.0.Alpha3", org.hibernate.tool.api.version.Version.CURRENT_VERSION);
	}
	
	@Test 
	public void testRuntimeVersion() {
		assertSame(RuntimeServiceManager.getInstance().findService("7.0 Preview").getClass(), ServiceImpl.class);
	}
}
