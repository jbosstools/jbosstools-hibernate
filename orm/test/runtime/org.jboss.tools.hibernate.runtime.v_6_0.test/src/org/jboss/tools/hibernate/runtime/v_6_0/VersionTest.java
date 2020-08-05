package org.jboss.tools.hibernate.runtime.v_6_0;

import org.hibernate.tool.api.version.Version;
import org.junit.Assert;
import org.junit.Test;

public class VersionTest {
	
	@Test
	public void testToolsVersion() {
		Assert.assertEquals("6.0.0.Alpha1", Version.CURRENT_VERSION);
	}

}
