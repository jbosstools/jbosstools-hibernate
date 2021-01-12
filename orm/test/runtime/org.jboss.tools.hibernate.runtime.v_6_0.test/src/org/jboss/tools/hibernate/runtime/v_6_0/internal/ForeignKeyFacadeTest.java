package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.Before;
import org.junit.Test;

public class ForeignKeyFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IForeignKey foreignKeyFacade = null; 
	private ForeignKey foreignKey = null;
	
	@Before
	public void before() {
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
		foreignKey.addReferencedColumns(list.iterator());
		assertFalse(foreignKeyFacade.isReferenceToPrimaryKey());
	}
	
	@Test
	public void testGetReferencedColumns() {
		List<IColumn> list = foreignKeyFacade.getReferencedColumns();
		assertTrue(list.isEmpty());		
		Column column = new Column();
		ArrayList<Column> columns = new ArrayList<Column>();
		columns.add(column);
		foreignKey.addReferencedColumns(columns.iterator());
		// recreate facade to reinitialize the instance variables
		foreignKeyFacade = new ForeignKeyFacadeImpl(FACADE_FACTORY, foreignKey);
		list = foreignKeyFacade.getReferencedColumns();
		assertEquals(1, list.size());
		assertSame(column, ((IFacade)list.get(0)).getTarget());
	}
	
}
