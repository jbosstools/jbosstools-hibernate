package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.wrp.DelegatingColumnWrapperImpl;
import org.hibernate.tool.orm.jbt.wrp.ForeignKeyWrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IForeignKeyTest {

	private IForeignKey foreignKeyFacade = null; 
	private ForeignKey foreignKeyTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		foreignKeyTarget = new ForeignKey();
		foreignKeyFacade = (IForeignKey)GenericFacadeFactory.createFacade(
				IForeignKey.class, 
				ForeignKeyWrapperFactory.createForeinKeyWrapper(foreignKeyTarget));
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(foreignKeyFacade);
		assertNotNull(foreignKeyTarget);
	}

	@Test
	public void testGetReferencedTable() {
		Table tableTarget = new Table("");
		foreignKeyTarget.setReferencedTable(tableTarget);
		ITable table = foreignKeyFacade.getReferencedTable();
		assertSame(tableTarget, ((IFacade)table).getTarget());
	}
	
	@Test
	public void testColumnIterator() {
		Iterator<IColumn> iterator = foreignKeyFacade.columnIterator();
		assertFalse(iterator.hasNext());
		Column column = new Column();
		foreignKeyTarget.addColumn(column);
		iterator = foreignKeyFacade.columnIterator();
		IColumn columnFacade = iterator.next();
		assertSame(column, ((IFacade)columnFacade).getTarget());
		assertFalse(iterator.hasNext());
	}
	
	@Test
	public void testIsReferenceToPrimaryKey() {
		assertTrue(foreignKeyFacade.isReferenceToPrimaryKey());
		ArrayList<Column> list = new ArrayList<Column>();
		Column column = new Column();
		list.add(column);
		foreignKeyTarget.addReferencedColumns(list);
		assertFalse(foreignKeyFacade.isReferenceToPrimaryKey());
	}
	
	@Test
	public void testGetReferencedColumns() {
		List<IColumn> list = foreignKeyFacade.getReferencedColumns();
		assertTrue(list.isEmpty());		
		Column column = new Column();
		ArrayList<Column> columns = new ArrayList<Column>();
		columns.add(column);
		foreignKeyTarget.addReferencedColumns(columns);
		list = foreignKeyFacade.getReferencedColumns();
		assertEquals(1, list.size());
		assertSame(column, ((IFacade)list.get(0)).getTarget());
	}
	
	@Test
	public void testContainsColumn() throws Exception {
		Column columnWrapper = new DelegatingColumnWrapperImpl(new Column("foo"));
		IColumn columnFacade = (IColumn)GenericFacadeFactory.createFacade(
				IColumn.class, 
				columnWrapper);
		assertFalse(foreignKeyFacade.containsColumn(columnFacade));
		foreignKeyTarget.addColumn(columnWrapper);
		assertTrue(foreignKeyFacade.containsColumn(columnFacade));
	}
	
}
