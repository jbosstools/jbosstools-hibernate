package org.jboss.tools.hibernate.runtime.v_6_0;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

public class VersionTest {
	
	@Test
	public void testToolsVersion() {
		Assert.assertEquals("6.0.0.Alpha2", org.hibernate.tool.api.version.Version.CURRENT_VERSION);
	}
	
	@Test 
	public void testCoreVersion() {
		assertEquals("6.0.0.Alpha6", org.hibernate.Version.getVersionString());
	}

}
