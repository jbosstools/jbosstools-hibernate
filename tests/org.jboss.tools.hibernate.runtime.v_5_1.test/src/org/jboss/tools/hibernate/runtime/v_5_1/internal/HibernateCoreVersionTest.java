package org.jboss.tools.hibernate.runtime.v_5_1.internal;

import org.hibernate.Version;
import org.junit.Assert;
import org.junit.Test;

public class HibernateCoreVersionTest {
	
	@Test
	public void testHibernateCoreVersion() {
		Assert.assertEquals("5.1.0.Final", Version.getVersionString());
	}
	
}
