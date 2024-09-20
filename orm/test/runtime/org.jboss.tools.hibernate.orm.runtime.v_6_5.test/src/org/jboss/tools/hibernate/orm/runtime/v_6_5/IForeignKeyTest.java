package org.jboss.tools.hibernate.orm.runtime.v_6_5;

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
import org.hibernate.tool.orm.jbt.api.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.internal.factory.ColumnWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.ForeignKeyWrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
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
				ForeignKeyWrapperFactory.createForeignKeyWrapper(foreignKeyTarget));
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
		assertSame(tableTarget, ((Wrapper)((IFacade)table).getTarget()).getWrappedObject());
	}
	
	@Test
	public void testColumnIterator() {
		Iterator<IColumn> iterator = foreignKeyFacade.columnIterator();
		assertFalse(iterator.hasNext());
		Column column = new Column();
		foreignKeyTarget.addColumn(column);
		iterator = foreignKeyFacade.columnIterator();
		IColumn columnFacade = iterator.next();
		assertSame(column, ((Wrapper)((IFacade)columnFacade).getTarget()).getWrappedObject());
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
		assertSame(column, ((Wrapper)((IFacade)list.get(0)).getTarget()).getWrappedObject());
	}
	
	@Test
	public void testContainsColumn() throws Exception {
		ColumnWrapper columnWrapper = ColumnWrapperFactory.createColumnWrapper("foo");
		IColumn columnFacade = (IColumn)GenericFacadeFactory.createFacade(
				IColumn.class, 
				columnWrapper);
		assertFalse(foreignKeyFacade.containsColumn(columnFacade));
		foreignKeyTarget.addColumn((Column)columnWrapper.getWrappedObject());
		assertTrue(foreignKeyFacade.containsColumn(columnFacade));
	}
	
}
