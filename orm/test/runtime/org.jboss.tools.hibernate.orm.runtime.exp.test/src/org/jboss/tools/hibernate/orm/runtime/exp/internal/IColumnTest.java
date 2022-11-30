package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.type.IntegerType;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.hibernate.tool.orm.jbt.wrp.ColumnWrapper;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IColumnTest {
	
	private static final NewFacadeFactory FACADE_FACTORY = NewFacadeFactory.INSTANCE;
	
	private IColumn columnFacade = null; 
	private ColumnWrapper columnTarget = null;
	private Column wrappedColumn = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		columnFacade = FACADE_FACTORY.createColumn();
		columnTarget = (ColumnWrapper)((IFacade)columnFacade).getTarget();
		Field columnField = ColumnWrapper.class.getDeclaredField("wrappedColumn");
		columnField.setAccessible(true);
		wrappedColumn = (Column)columnField.get(columnTarget);
	}
	
	@Test
	public void testInstance() {
		assertNotNull(columnFacade);
		assertNotNull(columnTarget);
		assertNotNull(wrappedColumn);
	}
	
	@Test
	public void testGetName() {
		assertNull(columnFacade.getName());
		wrappedColumn.setName("foobar");
		assertEquals("foobar", columnFacade.getName());
	}
	
	@Test
	public void testGetSqlTypeCode() {
		assertNull(columnFacade.getSqlTypeCode());
		wrappedColumn.setSqlTypeCode(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnFacade.getSqlTypeCode().intValue());
	}

	@Test
	public void testGetSqlType() {
		assertNull(columnFacade.getSqlType());
		wrappedColumn.setSqlType("foobar");
		assertEquals("foobar", columnFacade.getSqlType());
		IConfiguration configurationFacade = FACADE_FACTORY.createNativeConfiguration();
		configurationFacade.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configurationFacade.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		wrappedColumn.setValue(createIntegerTypeValue());
		wrappedColumn.setSqlType(null);
		assertEquals("integer", columnFacade.getSqlType(configurationFacade));
	}
	
	@Test
	public void testGetLength() {
		assertEquals(Integer.MIN_VALUE, columnFacade.getLength());
		wrappedColumn.setLength(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnFacade.getLength());
	}
	
	@Test
	public void testGetDefaultLength() throws Exception {
		Field defaultLengthField = ColumnWrapper.class.getDeclaredField("DEFAULT_LENGTH");
		defaultLengthField.setAccessible(true);
		assertEquals(defaultLengthField.get(null), columnFacade.getDefaultLength());
	}
	
	@Test
	public void testGetPrecision() {
		assertEquals(Integer.MIN_VALUE, columnFacade.getPrecision());
		wrappedColumn.setPrecision(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnFacade.getPrecision());
	}
	
	@Test
	public void testGetDefaultPrecision() throws Exception {
		Field defaultPrecisionField = ColumnWrapper.class.getDeclaredField("DEFAULT_PRECISION");
		defaultPrecisionField.setAccessible(true);
		assertEquals(defaultPrecisionField.get(null), columnFacade.getDefaultPrecision());
	}
	
	@Test
	public void testGetScale() {
		assertEquals(Integer.MIN_VALUE, columnFacade.getScale());
		wrappedColumn.setScale(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnFacade.getScale());
	}
	
	@Test
	public void testGetDefaultScale() throws Exception {
		Field defaultScaleField = ColumnWrapper.class.getDeclaredField("DEFAULT_SCALE");
		defaultScaleField.setAccessible(true);
		assertEquals(defaultScaleField.get(null), columnFacade.getDefaultScale());
	}
	
	private Value createIntegerTypeValue() {
		return (Value)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { Value.class }, 
				new InvocationHandler() {					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) 
							throws Throwable {
						if (method.getName().equals("getType")) {
							return IntegerType.INSTANCE;
						}
						return null;
					}
				});
	}

}
