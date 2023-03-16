package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ITableTest {
	
	private ITable tableFacade = null;
	private Table tableTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		tableFacade = NewFacadeFactory.INSTANCE.createTable("foo");
		tableTarget = (Table)((IFacade)tableFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(tableFacade);
		assertNotNull(tableTarget);
	}
	

	@Test
	public void testGetName() {
		assertEquals("foo", tableFacade.getName());
		tableTarget.setName("bar");
		assertEquals("bar", tableFacade.getName());
	}
	
	@Test
	public void testAddColumn() {
		IColumn columnFacade = NewFacadeFactory.INSTANCE.createColumn("foo");
		Column columnTarget = (Column)((IFacade)columnFacade).getTarget();
		assertNull(tableTarget.getColumn(columnTarget));
		tableFacade.addColumn(columnFacade);
		assertSame(columnTarget, tableTarget.getColumn(columnTarget));
	}
	
	@Test
	public void testGetCatalog() {
		assertNull(tableFacade.getCatalog());
		tableTarget.setCatalog("foo");
		assertEquals("foo", tableFacade.getCatalog());
	}
	
}
