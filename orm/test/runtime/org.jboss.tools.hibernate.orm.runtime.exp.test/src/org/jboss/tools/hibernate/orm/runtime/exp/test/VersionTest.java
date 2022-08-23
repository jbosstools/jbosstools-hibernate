package org.jboss.tools.hibernate.orm.runtime.exp.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class VersionTest {
	
	@Test 
	public void testCoreVersion() {
		assertEquals("6.1.2.Final", org.hibernate.Version.getVersionString());
	}

}
