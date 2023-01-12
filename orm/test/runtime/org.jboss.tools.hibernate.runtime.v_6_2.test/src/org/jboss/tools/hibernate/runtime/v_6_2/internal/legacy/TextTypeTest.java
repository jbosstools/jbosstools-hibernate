package org.jboss.tools.hibernate.runtime.v_6_2.internal.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class TextTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(TextType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("text", TextType.INSTANCE.getName());
	}
	
}
