package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.AbstractForeignKeyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


//TODO JBIDE-28083: Hibernate Java 17 compability - Reenable test and investigate error
@Ignore
public class ForeignKeyFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IForeignKey foreignKeyFacade = null; 
	private ForeignKey foreignKey = null;
	
	private String methodName = null;
	private Object[] arguments = null;
	
	@Before
	public void setUp() throws Exception {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(ForeignKey.class);
		enhancer.setCallback(new MethodInterceptor() {
			@Override
			public Object intercept(
					Object obj, 
					Method method, 
					Object[] args, 
					MethodProxy proxy) throws Throwable {
				if (methodName == null) {
					methodName = method.getName();
				}
				if (arguments == null) {
					arguments = args;
				}
				return proxy.invokeSuper(obj, args);
			}					
		});
		foreignKey = (ForeignKey)enhancer.create();
		foreignKeyFacade = new AbstractForeignKeyFacade(FACADE_FACTORY, foreignKey) {};
		reset();
	}
	
	@Test
	public void testGetReferencedTable() {
		ITable first = foreignKeyFacade.getReferencedTable();
		Assert.assertEquals("getReferencedTable", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
		Assert.assertNull(first);
		Table table = new Table();
		foreignKey.setReferencedTable(table);
		reset();
		ITable second = foreignKeyFacade.getReferencedTable();
		Assert.assertEquals("getReferencedTable", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
		Assert.assertNotNull(second);
		Assert.assertSame(table, ((IFacade)second).getTarget());
		reset();
		ITable third = foreignKeyFacade.getReferencedTable();
		Assert.assertNull(methodName);
		Assert.assertNull(arguments);
		Assert.assertSame(second, third);
	}
	
	@Test
	public void testColumnIterator() {
		Column column = new Column();
		foreignKey.addColumn(column);
		reset();
		Iterator<IColumn> iterator = foreignKeyFacade.columnIterator();
		Assert.assertEquals("columnIterator", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
		IColumn columnFacade = iterator.next();
		Assert.assertSame(column, ((IFacade)columnFacade).getTarget());
		Assert.assertFalse(iterator.hasNext());
	}
	
	@Test
	public void testIsReferenceToPrimaryKey() {
		Assert.assertTrue(foreignKeyFacade.isReferenceToPrimaryKey());
		Assert.assertEquals("isReferenceToPrimaryKey", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
		Column column = new Column();
		ArrayList<Column> list = new ArrayList<Column>();
		list.add(column);
		foreignKey.addReferencedColumns(list.iterator());
		reset();
		Assert.assertFalse(foreignKeyFacade.isReferenceToPrimaryKey());
		Assert.assertEquals("isReferenceToPrimaryKey", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
	}
	
	@Test
	public void testGetReferencedColumns() {
		List<IColumn> list = foreignKeyFacade.getReferencedColumns();
		Assert.assertTrue(list.isEmpty());
		Assert.assertEquals("getReferencedColumns", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
		// recreate facade to reinitialize the instance variables
		foreignKeyFacade = new AbstractForeignKeyFacade(FACADE_FACTORY, foreignKey) {};
		Column column = new Column();
		ArrayList<Column> columns = new ArrayList<Column>();
		columns.add(column);
		foreignKey.addReferencedColumns(columns.iterator());
		reset();
		list = foreignKeyFacade.getReferencedColumns();
		Assert.assertFalse(list.isEmpty());
		Assert.assertEquals("getReferencedColumns", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
	}
	
	@Test
	public void testContainsColumn() {
		Column column = new Column();
		IColumn columnFacade = FACADE_FACTORY.createColumn(column);
		Assert.assertFalse(foreignKeyFacade.containsColumn(columnFacade));
		Assert.assertEquals("containsColumn", methodName);
		Assert.assertArrayEquals(new Object[] { column }, arguments);
		foreignKey.addColumn(column);
		reset();
		Assert.assertTrue(foreignKeyFacade.containsColumn(columnFacade));
		Assert.assertEquals("containsColumn", methodName);
		Assert.assertArrayEquals(new Object[] { column }, arguments);
	}
	
	private void reset() {
		methodName = null;
		arguments = null;
	}
	
}
