package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.runtime.common.AbstractColumnFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.jboss.tools.hibernate.runtime.spi.IMapping;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ColumnFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IColumn columnFacade = null; 
	private Column column = null;
	
	@SuppressWarnings("serial")
	@Before
	public void setUp() {
		column = new Column() { 
			@Override public String getSqlType(Dialect dialect, Mapping mapping) {
				return "dummy";
			}
		};
		columnFacade = new AbstractColumnFacade(FACADE_FACTORY, column) {};
	}
	
	@Test
	public void testGetMappedClass() {
		Assert.assertNull(columnFacade.getName());
		column.setName("foobar");
		Assert.assertEquals("foobar", columnFacade.getName());
	}
	
	@Test
	public void testGetSqlTypeCode() {
		Assert.assertNull(columnFacade.getSqlTypeCode());
		column.setSqlTypeCode(Integer.MAX_VALUE);
		Assert.assertEquals(Integer.MAX_VALUE, columnFacade.getSqlTypeCode().intValue());
	}
	
	@Test
	public void testGetSqlType() {
		Assert.assertNull(columnFacade.getSqlType());
		column.setSqlType("foobar");
		Assert.assertEquals("foobar", columnFacade.getSqlType());
		Dialect targetDialect = new Dialect() {};
		Mapping targetMapping = (Mapping)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Mapping.class }, 
				new TestInvocationHandler());
		IDialect dialect = FACADE_FACTORY.createDialect(targetDialect);
		IMapping mapping = FACADE_FACTORY.createMapping(targetMapping);
		column.setSqlType(null);
		Assert.assertEquals("dummy", columnFacade.getSqlType(dialect, mapping));
	}
	
	@Test
	public void testGetLength() {
		Assert.assertEquals(Column.DEFAULT_LENGTH, columnFacade.getLength());
		column.setLength(999);
		Assert.assertEquals(999, columnFacade.getLength());
	}
	
	@Test
	public void testGetDefaultLength() {
		Assert.assertEquals(Column.DEFAULT_LENGTH, columnFacade.getDefaultLength());
	}
	
	@Test
	public void testGetPrecision() {
		Assert.assertEquals(Column.DEFAULT_PRECISION, columnFacade.getPrecision());
		column.setPrecision(999);
		Assert.assertEquals(999, columnFacade.getPrecision());
	}
	
	@Test
	public void testGetDefaultPrecision() {
		Assert.assertEquals(Column.DEFAULT_PRECISION, columnFacade.getDefaultPrecision());
	}
	
	@Test
	public void testGetScale() {
		Assert.assertEquals(Column.DEFAULT_SCALE, columnFacade.getScale());
		column.setScale(999);
		Assert.assertEquals(999, columnFacade.getScale());
	}
	
	@Test
	public void testGetDefaultScale() {
		Assert.assertEquals(Column.DEFAULT_SCALE, columnFacade.getDefaultScale());
	}
	
	@Test
	public void testIsNullable() {
		column.setNullable(true);
		Assert.assertTrue(columnFacade.isNullable());
		column.setNullable(false);
		Assert.assertFalse(columnFacade.isNullable());
	}
	
	public void testGetValue() throws Exception {
		Value targetValue = null;
		column.setValue(targetValue);
		Assert.assertNull(columnFacade.getValue());
		targetValue = (Value)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Value.class }, 
				new TestInvocationHandler());
		column.setValue(targetValue);
		IValue value = columnFacade.getValue();
		Assert.assertNotNull(value);
		Assert.assertEquals(targetValue, ((IFacade)value).getTarget());
	}
	
	public void testIsUnique() {
		column.setUnique(true);
		Assert.assertFalse(columnFacade.isUnique());
		column.setUnique(true);
		Assert.assertTrue(columnFacade.isUnique());
	}
	
	public void testSetSqlType() {
		Assert.assertNull(column.getSqlType());
		columnFacade.setSqlType("blah");
		Assert.assertEquals("blah", column.getSqlType());
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}		
	}
	
}
