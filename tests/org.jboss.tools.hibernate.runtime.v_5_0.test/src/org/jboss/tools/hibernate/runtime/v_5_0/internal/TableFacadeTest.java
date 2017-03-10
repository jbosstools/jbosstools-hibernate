package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
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
	
	@Test
	public void testAddColumn() {
		Table table = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(table);
		Column column = new Column("foo");
		IColumn columnFacade = FACADE_FACTORY.createColumn(column);
		Assert.assertNull(table.getColumn(column));
		tableFacade.addColumn(columnFacade);
		Assert.assertSame(column, table.getColumn(column));
	}
	
}
