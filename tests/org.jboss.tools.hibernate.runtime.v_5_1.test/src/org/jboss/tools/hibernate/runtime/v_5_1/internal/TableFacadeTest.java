package org.jboss.tools.hibernate.runtime.v_5_1.internal;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.IFacade;
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
		Table table = new Table();
		PrimaryKey primaryKey = new PrimaryKey(table);
		IPrimaryKey primaryKeyFacade = FACADE_FACTORY.createPrimaryKey(primaryKey);
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
	
	@Test
	public void testGetPrimaryKey() {
		Table table = new Table();
		PrimaryKey primaryKey = new PrimaryKey(table);
		ITable tableFacade = FACADE_FACTORY.createTable(table);
		Assert.assertNull(tableFacade.getPrimaryKey());
		table.setPrimaryKey(primaryKey);
		IPrimaryKey primaryKeyFacade = tableFacade.getPrimaryKey();
		Assert.assertSame(primaryKey, ((IFacade)primaryKeyFacade).getTarget());
	}
	
	@Test
	public void testGetColumnIterator() {
		Table table = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(table);
		Iterator<IColumn> columnIterator = tableFacade.getColumnIterator();
		Assert.assertFalse(columnIterator.hasNext());
		Column column = new Column("foo");
		table.addColumn(column);
		tableFacade = FACADE_FACTORY.createTable(table);
		columnIterator = tableFacade.getColumnIterator();
		IColumn columnFacade = columnIterator.next();
		Assert.assertSame(column, ((IFacade)columnFacade).getTarget());
	}
	
	@Test
	public void testGetComment() {
		Table table = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(table);
		Assert.assertNull(tableFacade.getComment());
		table.setComment("foo");
		Assert.assertEquals("foo", tableFacade.getComment());
	}
	
	@Test
	public void testGetRowId() {
		Table table = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(table);
		Assert.assertNull(tableFacade.getRowId());
		table.setRowId("foo");
		Assert.assertEquals("foo", tableFacade.getRowId());
	}
	
	@Test
	public void testGetSubselect() {
		Table table = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(table);
		Assert.assertNull(tableFacade.getSubselect());		
		table.setSubselect("foo");
		Assert.assertEquals("foo", tableFacade.getSubselect());
	}
	
	@Test
	public void testHasDenormalizedTables() throws Exception {
		Table table = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(table);
		Assert.assertFalse(tableFacade.hasDenormalizedTables());
		Method method = Table.class.getDeclaredMethod(
				"setHasDenormalizedTables", 
				new Class[] { });
		method.setAccessible(true);
		method.invoke(table, new Object[] { });
		Assert.assertTrue(tableFacade.hasDenormalizedTables());
	}
	
	@Test
	public void testIsAbstract() {
		Table table = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(table);
		table.setAbstract(true);
		Assert.assertTrue(tableFacade.isAbstract());		
		table.setAbstract(false);
		Assert.assertFalse(tableFacade.isAbstract());		
	}
	
	@Test
	public void testIsAbstractUnionTable() throws Exception {
		Table table = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(table);
		table.setAbstract(false);
		Assert.assertFalse(tableFacade.isAbstractUnionTable());	
		table.setAbstract(true);
		Assert.assertFalse(tableFacade.isAbstractUnionTable());	
		Method method = Table.class.getDeclaredMethod(
				"setHasDenormalizedTables", 
				new Class[] { });
		method.setAccessible(true);
		method.invoke(table, new Object[] { });
		Assert.assertTrue(tableFacade.isAbstractUnionTable());
	}
	
	@Test
	public void testIsPhysicalTable() {
		Table table = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(table);
		table.setSubselect("foo");
		Assert.assertFalse(tableFacade.isPhysicalTable());	
		table.setSubselect(null);
		Assert.assertTrue(tableFacade.isPhysicalTable());
	}
	
}
