package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ForeignKeyFacadeTest {

	private static final NewFacadeFactory FACADE_FACTORY = NewFacadeFactory.INSTANCE;
	
	private IForeignKey foreignKeyFacade = null; 
	private ForeignKey foreignKey = null;
	
	@BeforeEach
	public void beforeEach() {
		foreignKey = new ForeignKey();
		foreignKeyFacade = new ForeignKeyFacadeImpl(FACADE_FACTORY, foreignKey);
	}
	
	@Test
	public void testGetReferencedTable() {
		Table tableTarget = new Table();
		foreignKey.setReferencedTable(tableTarget);
		ITable table = foreignKeyFacade.getReferencedTable();
		assertSame(tableTarget, ((IFacade)table).getTarget());
	}
	
	@Test
	public void testColumnIterator() {
		Column column = new Column();
		foreignKey.addColumn(column);
		Iterator<IColumn> iterator = foreignKeyFacade.columnIterator();
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
		foreignKey.addReferencedColumns(list);
		assertFalse(foreignKeyFacade.isReferenceToPrimaryKey());
	}
	
	@Test
	public void testGetReferencedColumns() {
		List<IColumn> list = foreignKeyFacade.getReferencedColumns();
		assertTrue(list.isEmpty());		
		Column column = new Column();
		ArrayList<Column> columns = new ArrayList<Column>();
		columns.add(column);
		foreignKey.addReferencedColumns(columns);
		// recreate facade to reinitialize the instance variables
		foreignKeyFacade = new ForeignKeyFacadeImpl(FACADE_FACTORY, foreignKey);
		list = foreignKeyFacade.getReferencedColumns();
		assertEquals(1, list.size());
		assertSame(column, ((IFacade)list.get(0)).getTarget());
	}
	
	@Test
	public void testContainsColumn() throws Exception {
		IColumn columnFacade = FACADE_FACTORY.createColumn("foo");
		Column column = (Column)((IFacade)columnFacade).getTarget();
		assertFalse(foreignKeyFacade.containsColumn(columnFacade));
		foreignKey.addColumn(column);
		assertTrue(foreignKeyFacade.containsColumn(columnFacade));
	}
	
}
