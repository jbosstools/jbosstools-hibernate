package org.jboss.tools.hibernate.orm.runtime.exp.internal.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class CalendarDateTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(CalendarDateType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("calendar_date", CalendarDateType.INSTANCE.getName());
	}
	
}
