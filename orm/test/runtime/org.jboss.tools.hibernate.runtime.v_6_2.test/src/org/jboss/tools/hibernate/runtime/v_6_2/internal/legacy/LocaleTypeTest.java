package org.jboss.tools.hibernate.runtime.v_6_2.internal.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LocaleTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(LocaleType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("locale", LocaleType.INSTANCE.getName());
	}
	
	@Test
	public void testRegisterUnderJavaType() {
		assertTrue(LocaleType.INSTANCE.registerUnderJavaType());
	}

}
