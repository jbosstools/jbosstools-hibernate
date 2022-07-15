package org.jboss.tools.hibernate.runtime.v_6_1.internal.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
