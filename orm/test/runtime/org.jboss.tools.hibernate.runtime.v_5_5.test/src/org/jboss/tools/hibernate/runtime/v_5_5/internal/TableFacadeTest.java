package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.jupiter.api.Test;

public class TableFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testGetName() {
		Table table = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(table);
		assertNull(tableFacade.getName());
		table.setName("foo");
		assertEquals("foo", tableFacade.getName());
	}
	
}
