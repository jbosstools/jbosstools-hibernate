package org.jboss.tools.hibernate.orm.runtime.v_6_6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.api.factory.WrapperFactory;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.internal.util.DummyMetadataBuildingContext;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ITableTest {
	
	private ITable tableFacade = null;
	private Table tableTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		tableFacade = (ITable)GenericFacadeFactory.createFacade(
				ITable.class, 
				WrapperFactory.createTableWrapper("foo"));
		Wrapper tableWrapper = (Wrapper)((IFacade)tableFacade).getTarget();
		tableTarget = (Table)tableWrapper.getWrappedObject();
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
		IColumn columnFacade = (IColumn)GenericFacadeFactory.createFacade(
				IColumn.class, 
				WrapperFactory.createColumnWrapper("foo"));
		Column columnTarget = (Column)((Wrapper)((IFacade)columnFacade).getTarget()).getWrappedObject();
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
	
	@Test
	public void testGetSchema() {
		assertNull(tableFacade.getSchema());
		tableTarget.setSchema("foo");
		assertEquals("foo", tableFacade.getSchema());
	}
	
	@Test
	public void testGetPrimaryKey() {
		PrimaryKey primaryKeyTarget = new PrimaryKey(tableTarget);
		IPrimaryKey primaryKeyFacade = tableFacade.getPrimaryKey();
		assertNotSame(primaryKeyTarget, ((IFacade)primaryKeyFacade).getTarget());
		tableTarget.setPrimaryKey(primaryKeyTarget);
		primaryKeyFacade = tableFacade.getPrimaryKey();
		assertSame(primaryKeyTarget, ((Wrapper)((IFacade)primaryKeyFacade).getTarget()).getWrappedObject());
	}
	
	@Test
	public void testGetColumnIterator() {
		Iterator<IColumn> columnIterator = tableFacade.getColumnIterator();
		assertFalse(columnIterator.hasNext());
		IColumn columnFacade1 = (IColumn)GenericFacadeFactory.createFacade(
				IColumn.class, 
				WrapperFactory.createColumnWrapper("bar"));
		tableFacade.addColumn(columnFacade1);
		columnIterator = tableFacade.getColumnIterator();
		IColumn columnFacade2 = columnIterator.next();
		assertEquals(columnFacade1, columnFacade2);
	}
	
	@Test
	public void testGetComment() {
		assertNull(tableFacade.getComment());
		tableTarget.setComment("foo");
		assertEquals("foo", tableFacade.getComment());
	}
	
	@Test
	public void testGetRowId() {
		assertNull(tableFacade.getRowId());
		tableTarget.setRowId("foo");
		assertEquals("foo", tableFacade.getRowId());
	}
	
	@Test
	public void testGetSubselect() {
		assertNull(tableFacade.getSubselect());		
		tableTarget.setSubselect("foo");
		assertEquals("foo", tableFacade.getSubselect());
	}
	
	@Test
	public void testHasDenormalizedTables() throws Exception {
		assertFalse(tableFacade.hasDenormalizedTables());
		Method method = Table.class.getDeclaredMethod(
				"setHasDenormalizedTables", 
				new Class[] { });
		method.setAccessible(true);
		method.invoke(tableTarget, new Object[] { });
		assertTrue(tableFacade.hasDenormalizedTables());
	}
	
	@Test
	public void testIsAbstract() {
		tableTarget.setAbstract(true);
		assertTrue(tableFacade.isAbstract());		
		tableTarget.setAbstract(false);
		assertFalse(tableFacade.isAbstract());		
	}
	
	@Test
	public void testIsAbstractUnionTable() throws Exception {
		tableTarget.setAbstract(false);
		assertFalse(tableFacade.isAbstractUnionTable());	
		tableTarget.setAbstract(true);
		assertFalse(tableFacade.isAbstractUnionTable());	
		Method method = Table.class.getDeclaredMethod(
				"setHasDenormalizedTables", 
				new Class[] { });
		method.setAccessible(true);
		method.invoke(tableTarget, new Object[] { });
		assertTrue(tableFacade.isAbstractUnionTable());
	}
	
	@Test
	public void testIsPhysicalTable() {
		tableTarget.setSubselect("foo");
		assertFalse(tableFacade.isPhysicalTable());	
		tableTarget.setSubselect(null);
		assertTrue(tableFacade.isPhysicalTable());
	}
	
	// TODO Disabled because of JBIDE-29200 (Remove method 'ITable#getIdentifierValue()' and find workaround)
	@Disabled
	@Test
	public void testGetIdentifierValue() {
		IValue valueFacade = tableFacade.getIdentifierValue();
		assertNull(valueFacade);
		KeyValue value = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		//tableTarget.setIdentifierValue(value);
		valueFacade = tableFacade.getIdentifierValue();
		assertSame(value, ((Wrapper)((IFacade)valueFacade).getTarget()).getWrappedObject());
	}
	
}
