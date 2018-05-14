package org.jboss.tools.hibernate.runtime.v_5_1;

import org.junit.Assert;
import org.junit.Test;

public class VersionTest {
	
	@Test
	public void testToolsVersion() {
		Assert.assertEquals("5.1.8.Final", org.hibernate.tool.Version.VERSION);
	}
	
	@Test
	public void testCoreVersion() {
		Assert.assertEquals("5.1.14.Final", org.hibernate.Version.getVersionString());
	}

}
