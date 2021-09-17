package org.jboss.tools.hibernate.runtime.v_5_6.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.AbstractPrimaryKeyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrimaryKeyFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IPrimaryKey primaryKeyFacade = null; 
	private PrimaryKey primaryKeyTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		primaryKeyTarget = new PrimaryKey(new Table());
		primaryKeyFacade = new AbstractPrimaryKeyFacade(FACADE_FACTORY, primaryKeyTarget) {};
	}
	
	@Test
	public void testAddColumn() throws Exception {
		Field field = AbstractPrimaryKeyFacade.class.getDeclaredField("columns");
		field.setAccessible(true);
		assertNull(field.get(primaryKeyFacade));
		field.set(primaryKeyFacade, Collections.emptyList());
		Column columnTarget = new Column();
		IColumn columnFacade = FACADE_FACTORY.createColumn(columnTarget);
		assertTrue(primaryKeyTarget.getColumns().isEmpty());
		primaryKeyFacade.addColumn(columnFacade);
		assertEquals(1, primaryKeyTarget.getColumns().size());
		assertSame(columnTarget, primaryKeyTarget.getColumns().get(0));
		assertNull(field.get(primaryKeyFacade));
	}

	@Test
	public void testGetColumnSpan() {
		assertEquals(0, primaryKeyFacade.getColumnSpan());
		primaryKeyTarget.addColumn(new Column());
		assertEquals(1, primaryKeyFacade.getColumnSpan());
	}
	
	@Test
	public void testGetColumns() throws Exception {
		Field field = AbstractPrimaryKeyFacade.class.getDeclaredField("columns");
		field.setAccessible(true);
		assertNull(field.get(primaryKeyFacade));
		assertTrue(primaryKeyFacade.getColumns().isEmpty());
		assertNotNull(field.get(primaryKeyFacade));
		field.set(primaryKeyFacade, null);
		Column columnTarget = new Column();
		primaryKeyTarget.addColumn(columnTarget);
		List<IColumn> columnFacades = primaryKeyFacade.getColumns();
		assertNotNull(columnFacades);
		assertSame(columnFacades, field.get(primaryKeyFacade));
		assertSame(columnTarget, ((IFacade)columnFacades.get(0)).getTarget());
	}
	
	@Test
	public void testGetColumn() throws Exception {
		Field field = AbstractPrimaryKeyFacade.class.getDeclaredField("columns");
		field.setAccessible(true);
		assertNull(field.get(primaryKeyFacade));
		Column columnTarget = new Column();
		primaryKeyTarget.addColumn(columnTarget);
		IColumn columnFacade = primaryKeyFacade.getColumn(0);
		assertNotNull(field.get(primaryKeyFacade));
		assertNotNull(columnFacade);
		assertSame(columnTarget, ((IFacade)columnFacade).getTarget());
	}
	
	@Test
	public void testGetTable() throws Exception {
		Field field = AbstractPrimaryKeyFacade.class.getDeclaredField("table");
		field.setAccessible(true);
		assertNull(field.get(primaryKeyFacade));
		assertNotNull(primaryKeyFacade.getTable());
		assertNotNull(field.get(primaryKeyFacade));
		field.set(primaryKeyFacade, null);
		Table tableTarget = new Table();
		primaryKeyTarget.setTable(tableTarget);
		ITable tableFacade = primaryKeyFacade.getTable();
		assertNotNull(tableFacade);
		assertSame(tableFacade, field.get(primaryKeyFacade));
		assertSame(tableTarget, ((IFacade)tableFacade).getTarget());
	}
	
	@Test
	public void testContainsColumn() {
		Column columnTarget = new Column();
		IColumn columnFacade = FACADE_FACTORY.createColumn(columnTarget);
		assertFalse(primaryKeyFacade.containsColumn(columnFacade));
		primaryKeyTarget.addColumn(columnTarget);
		assertTrue(primaryKeyFacade.containsColumn(columnFacade));
	}
	
	@Test
	public void testColumnIterator() throws Exception {
		Field field = AbstractPrimaryKeyFacade.class.getDeclaredField("columns");
		field.setAccessible(true);
		assertNull(field.get(primaryKeyFacade));
		assertFalse(primaryKeyFacade.columnIterator().hasNext());
		assertNotNull(field.get(primaryKeyFacade));
		field.set(primaryKeyFacade, null);
		Column columnTarget = new Column();
		primaryKeyTarget.addColumn(columnTarget);
		Iterator<IColumn> columnIterator = primaryKeyFacade.columnIterator();
		assertTrue(columnIterator.hasNext());
		assertSame(columnTarget, ((IFacade)columnIterator.next()).getTarget());
	}
	
	@Test
	public void testGetName() {
		assertNotEquals("foo", primaryKeyFacade.getName());
		primaryKeyTarget.setName("foo");
		assertEquals("foo", primaryKeyFacade.getName());
	}
	
}
