package org.jboss.tools.hibernate.orm.runtime.v_6_4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.wrp.DelegatingColumnWrapperImpl;
import org.hibernate.tool.orm.jbt.wrp.PrimaryKeyWrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IPrimaryKeyTest {

	private IPrimaryKey primaryKeyFacade = null; 
	private PrimaryKey primaryKeyTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		primaryKeyTarget = new PrimaryKey(new Table(""));
		primaryKeyFacade = (IPrimaryKey)GenericFacadeFactory.createFacade(
				IPrimaryKey.class, 
				PrimaryKeyWrapperFactory.createForeinKeyWrapper(primaryKeyTarget));
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(primaryKeyFacade);
		assertNotNull(primaryKeyTarget);
	}

	@Test
	public void testAddColumn() throws Exception {
		Column columnTarget = new DelegatingColumnWrapperImpl(new Column("foo"));
		IColumn columnFacade = (IColumn)GenericFacadeFactory.createFacade(
				IColumn.class, 
				columnTarget);
		assertTrue(primaryKeyTarget.getColumns().isEmpty());
		primaryKeyFacade.addColumn(columnFacade);
		assertEquals(1, primaryKeyTarget.getColumns().size());
		assertSame(columnTarget, primaryKeyTarget.getColumns().get(0));
	}
	
	@Test
	public void testGetColumnSpan() {
		assertEquals(0, primaryKeyFacade.getColumnSpan());
		primaryKeyTarget.addColumn(new Column());
		assertEquals(1, primaryKeyFacade.getColumnSpan());
	}
	
	@Test
	public void testGetColumns() throws Exception {
		Column columnTarget = new Column("foo");
		assertTrue(primaryKeyFacade.getColumns().isEmpty());
		primaryKeyTarget.addColumn(columnTarget);
		List<IColumn> columnFacades = primaryKeyFacade.getColumns();
		assertNotNull(columnFacades);
		assertEquals(1, columnFacades.size());
		assertSame(columnTarget, ((IFacade)columnFacades.get(0)).getTarget());
	}
	
	@Test
	public void testGetColumn() throws Exception {
		try {
			primaryKeyFacade.getColumn(0);
			fail();
		} catch (IndexOutOfBoundsException e) {
			assertTrue(e.getMessage().contains("Index 0 out of bounds for length 0"));
		}
		Column columnTarget = new Column();
		primaryKeyTarget.addColumn(columnTarget);
		IColumn columnFacade = primaryKeyFacade.getColumn(0);
		assertNotNull(columnFacade);
		assertSame(columnTarget, ((IFacade)columnFacade).getTarget());
	}
	
	@Test
	public void testGetTable() throws Exception {
		Table tableTarget = new Table("foo");
		assertNotSame(tableTarget, (Table)((IFacade)primaryKeyFacade.getTable()).getTarget());
		primaryKeyTarget.setTable(tableTarget);
		assertSame(tableTarget, (Table)((IFacade)primaryKeyFacade.getTable()).getTarget());
	}
	
	@Test
	public void testContainsColumn() {
		Column columnTarget = new DelegatingColumnWrapperImpl(new Column("foo"));
		IColumn columnFacade = (IColumn)GenericFacadeFactory.createFacade(
				IColumn.class, 
				columnTarget);
		assertFalse(primaryKeyFacade.containsColumn(columnFacade));
		primaryKeyTarget.addColumn(columnTarget);
		assertTrue(primaryKeyFacade.containsColumn(columnFacade));
	}
	
	@Test
	public void testColumnIterator() throws Exception {
		assertFalse(primaryKeyFacade.columnIterator().hasNext());
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
