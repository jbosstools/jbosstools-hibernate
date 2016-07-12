package org.jboss.tools.hibernate.runtime.v_5_2;

import org.junit.Assert;
import org.junit.Test;

public class VersionTest {
	
	@Test
	public void testVersion() {
		Assert.assertEquals("5.2.0.Alpha2", org.hibernate.tool.Version.VERSION);
	}

}
