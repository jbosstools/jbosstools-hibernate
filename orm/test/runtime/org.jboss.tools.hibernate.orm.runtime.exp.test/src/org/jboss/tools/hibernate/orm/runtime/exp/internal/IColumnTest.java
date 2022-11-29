package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.type.IntegerType;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IColumnTest {
	
	private static final NewFacadeFactory FACADE_FACTORY = NewFacadeFactory.INSTANCE;
	
	private IColumn columnFacade = null; 
	private Column columnTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		columnFacade = FACADE_FACTORY.createColumn();
		columnTarget = (Column)((IFacade)columnFacade).getTarget();
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
		assertNull(columnFacade.getSqlType());
		columnTarget.setSqlType("foobar");
		assertEquals("foobar", columnFacade.getSqlType());
		IConfiguration configurationFacade = FACADE_FACTORY.createNativeConfiguration();
		configurationFacade.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configurationFacade.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		columnTarget.setValue(createIntegerTypeValue());
		columnTarget.setSqlType(null);
		assertEquals("integer", columnFacade.getSqlType(configurationFacade));
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
