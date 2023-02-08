package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.type.IntegerType;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.hibernate.tool.orm.jbt.wrp.ColumnWrapper;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IColumnTest {
	
	private static final NewFacadeFactory FACADE_FACTORY = NewFacadeFactory.INSTANCE;
	
	private IColumn columnFacade = null; 
	private ColumnWrapper columnTarget = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		columnFacade = FACADE_FACTORY.createColumn(null);
		columnTarget = (ColumnWrapper)((IFacade)columnFacade).getTarget();
	}
	
	@Test
	public void testInstance() {
		assertNotNull(columnFacade);
		assertNotNull(columnTarget);
	}
	
	@Test
	public void testGetName() {
		assertNull(columnFacade.getName());
		columnTarget.setName("foobar");
		assertEquals("foobar", columnFacade.getName());
	}
	
	@Test
	public void testGetSqlTypeCode() {
		assertNull(columnFacade.getSqlTypeCode());
		columnTarget.setSqlTypeCode(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnFacade.getSqlTypeCode().intValue());
	}

	@Test
	public void testGetSqlType() {
		// IColumn#getSqlType()
		assertNull(columnFacade.getSqlType());
		columnTarget.setSqlType("foobar");
		assertEquals("foobar", columnFacade.getSqlType());
		// IColumn#getSqlType(IConfiguration)
		columnFacade = FACADE_FACTORY.createColumn(null);
		columnTarget = (ColumnWrapper)((IFacade)columnFacade).getTarget();
		columnTarget.setValue(createValue());
		IConfiguration configurationFacade = FACADE_FACTORY.createNativeConfiguration();
		configurationFacade.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configurationFacade.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		assertEquals("integer", columnFacade.getSqlType(configurationFacade));
	}
	
	@Test
	public void testGetLength() {
		assertEquals(Integer.MIN_VALUE, columnFacade.getLength());
		columnTarget.setLength(Integer.MAX_VALUE);
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
		columnTarget.setPrecision(Integer.MAX_VALUE);
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
		columnTarget.setScale(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnFacade.getScale());
	}
	
	@Test
	public void testGetDefaultScale() throws Exception {
		Field defaultScaleField = ColumnWrapper.class.getDeclaredField("DEFAULT_SCALE");
		defaultScaleField.setAccessible(true);
		assertEquals(defaultScaleField.get(null), columnFacade.getDefaultScale());
	}
	
	@Test
	public void testIsNullable() {
		columnTarget.setNullable(true);
		assertTrue(columnFacade.isNullable());
		columnTarget.setNullable(false);
		assertFalse(columnFacade.isNullable());
	}
	
	@Test
	public void testGetValue() {
		Value v = createValue();
		assertNull(columnFacade.getValue());
		columnTarget.setValue(v);
		IValue valueFacade = columnFacade.getValue();
		assertNotNull(valueFacade);
		assertSame(v, ((IFacade)valueFacade).getTarget());
		columnTarget.setValue(null);
		valueFacade = columnFacade.getValue();
		assertNull(valueFacade);
	}
	
	@Test
	public void testIsUnique() {
		assertFalse(columnFacade.isUnique());
		columnTarget.setUnique(true);
		assertTrue(columnFacade.isUnique());
		columnTarget.setUnique(false);
		assertFalse(columnFacade.isUnique());
	}
	
	@Test
	public void testSetSqlType() {
		assertNull(columnTarget.getSqlType());
		columnFacade.setSqlType("blah");
		assertEquals("blah", columnTarget.getSqlType());
	}
	
	private Value createValue() {
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
