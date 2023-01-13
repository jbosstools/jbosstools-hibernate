package org.jboss.tools.hibernate.runtime.v_6_1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.jboss.tools.hibernate.runtime.spi.RuntimeServiceManager;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.ServiceImpl;
import org.junit.jupiter.api.Test;

public class VersionTest {
	
	@Test
	public void testToolsVersion() {
		assertEquals("6.1.6.Final", org.hibernate.tool.api.version.Version.CURRENT_VERSION);
	}
	
	@Test
	public void testCoreVersion() {
		assertEquals("6.1.6.Final", org.hibernate.Version.getVersionString());
	}

	@Test 
	public void testRuntimeVersion() {
		assertSame(RuntimeServiceManager.getInstance().findService("6.1").getClass(), ServiceImpl.class);
	}
}
