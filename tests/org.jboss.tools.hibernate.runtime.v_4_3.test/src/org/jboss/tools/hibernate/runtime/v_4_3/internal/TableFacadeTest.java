package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
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
	
	@Test
	public void testSetPrimaryKey() {
		PrimaryKey primaryKey = new PrimaryKey();
		IPrimaryKey primaryKeyFacade = FACADE_FACTORY.createPrimaryKey(primaryKey);
		Table table = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(table);
		Assert.assertNull(table.getPrimaryKey());
		tableFacade.setPrimaryKey(primaryKeyFacade);
		Assert.assertSame(primaryKey, table.getPrimaryKey());
	}
	
	@Test
	public void testGetCatalog() {
		Table table = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(table);
		Assert.assertNull(tableFacade.getCatalog());
		table.setCatalog("foo");
		Assert.assertEquals("foo", tableFacade.getCatalog());
	}
	
	@Test
	public void testGetSchema() {
		Table table = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(table);
		Assert.assertNull(tableFacade.getSchema());
		table.setSchema("foo");
		Assert.assertEquals("foo", tableFacade.getSchema());
	}
	
}
