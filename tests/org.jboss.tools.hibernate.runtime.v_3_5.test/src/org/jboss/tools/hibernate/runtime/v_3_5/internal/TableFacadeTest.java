package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.Assert;
import org.junit.Test;

public class TableFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testGetName() {
		Table table = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(table);
		Assert.assertNull(tableFacade.getName());
		table.setName("foo");
		Assert.assertEquals("foo", tableFacade.getName());
	}
	
}
