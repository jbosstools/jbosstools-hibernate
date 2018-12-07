package org.jboss.tools.hibernate.runtime.v_5_4;

import org.junit.Assert;
import org.junit.Test;

public class VersionTest {
	
	@Test
	public void testToolsVersion() {
		Assert.assertEquals("5.4.0.CR2", org.hibernate.tool.Version.VERSION);
	}

	@Test
	public void testCoreVersion() {
		Assert.assertEquals("5.4.0.CR2", org.hibernate.Version.getVersionString());
	}

}
