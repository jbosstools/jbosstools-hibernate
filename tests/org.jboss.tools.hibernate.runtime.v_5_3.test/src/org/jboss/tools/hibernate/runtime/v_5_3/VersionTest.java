package org.jboss.tools.hibernate.runtime.v_5_3;

import org.junit.Assert;
import org.junit.Test;

public class VersionTest {
	
	@Test
	public void testToolsVersion() {
		Assert.assertEquals("5.3.0.Beta2", org.hibernate.tool.Version.VERSION);
	}

	@Test
	public void testCoreVersion() {
		Assert.assertEquals("5.3.0.Beta2", org.hibernate.Version.getVersionString());
	}

}
