package org.jboss.tools.hibernate.runtime.v_6_1.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.boot.internal.BootstrapContextImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.MockConnectionProvider;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.MockDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ColumnFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IColumn columnFacade = null; 
	private Column column = null;
	
	@BeforeEach
	public void beforeEach() {
		column = new Column();
		columnFacade = new ColumnFacadeImpl(FACADE_FACTORY, column);
	}
	
	@Test
	public void testInstance() {
		assertNotNull(column);
		assertNotNull(columnFacade);
	}
	
	@Test
	public void testGetName() {
		assertNull(columnFacade.getName());
		column.setName("foobar");
		assertEquals("foobar", columnFacade.getName());
	}
	
	@Test
	public void testGetSqlTypeCode() {
		assertNull(columnFacade.getSqlTypeCode());
		column.setSqlTypeCode(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnFacade.getSqlTypeCode().intValue());
	}

	@Test
	public void testGetSqlType() {
		assertNull(columnFacade.getSqlType());
		column.setSqlType("foobar");
		assertEquals("foobar", columnFacade.getSqlType());
		Configuration configuration = new Configuration();
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		BasicValue value = new BasicValue(createMetadataBuildingContext());
		value.setTypeName("int");
		column.setValue(value);
		IConfiguration configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		column.setSqlType(null);
		assertEquals("integer", columnFacade.getSqlType(configurationFacade));
	}
	
	@Test
	public void testGetLength() {
		assertEquals(Integer.MIN_VALUE, columnFacade.getLength());
		column.setLength(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnFacade.getLength());
	}
	
	@Test
	public void testGetDefaultLength() {
		assertEquals(ColumnFacadeImpl.DEFAULT_LENGTH, columnFacade.getDefaultLength());
	}
	
	@Test
	public void testGetPrecision() {
		assertEquals(Integer.MIN_VALUE, columnFacade.getPrecision());
		column.setPrecision(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnFacade.getPrecision());
	}
	
	@Test
	public void testGetDefaultPrecision() {
		assertEquals(ColumnFacadeImpl.DEFAULT_PRECISION, columnFacade.getDefaultPrecision());
	}
	
	@Test
	public void testGetScale() {
		assertEquals(Integer.MIN_VALUE, columnFacade.getScale());
		column.setScale(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnFacade.getScale());
	}
	
	@Test
	public void testGetDefaultScale() {
		assertEquals(ColumnFacadeImpl.DEFAULT_SCALE, columnFacade.getDefaultScale());
	}
	
	@Test
	public void testIsNullable() {
		column.setNullable(true);
		assertTrue(columnFacade.isNullable());
		column.setNullable(false);
		assertFalse(columnFacade.isNullable());
	}
	
	@Test
	public void testGetValue() {
		Value v = createValue();
		assertNull(((ColumnFacadeImpl)columnFacade).value);
		column.setValue(v);
		IValue valueFacade = columnFacade.getValue();
		assertSame(v, ((IFacade)valueFacade).getTarget());
		assertSame(valueFacade, ((ColumnFacadeImpl)columnFacade).value);
		((ColumnFacadeImpl)columnFacade).value = null;
		column.setValue(null);
		valueFacade = columnFacade.getValue();
		assertNull(valueFacade);
		assertNull(((ColumnFacadeImpl)columnFacade).value);
	}
	
	@Test
	public void testIsUnique() {
		assertFalse(columnFacade.isUnique());
		column.setUnique(true);
		assertTrue(columnFacade.isUnique());
		column.setUnique(false);
		assertFalse(columnFacade.isUnique());
	}
	
	private MetadataBuildingContext createMetadataBuildingContext() {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySetting(AvailableSettings.DIALECT, MockDialect.class.getName());
		builder.applySetting(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		StandardServiceRegistry serviceRegistry = builder.build();		
		MetadataBuildingOptionsImpl metadataBuildingOptions = 
				new MetadataBuildingOptionsImpl(serviceRegistry);	
		BootstrapContextImpl bootstrapContext = new BootstrapContextImpl(
				serviceRegistry, 
				metadataBuildingOptions);
		metadataBuildingOptions.setBootstrapContext(bootstrapContext);
		InFlightMetadataCollector inFlightMetadataCollector = 
				new InFlightMetadataCollectorImpl(
						bootstrapContext,
						metadataBuildingOptions);
		return new MetadataBuildingContextRootImpl(
						"JBoss Tools",
						bootstrapContext, 
						metadataBuildingOptions, 
						inFlightMetadataCollector);
	}
	
	private Value createValue() {
		return (Value)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { Value.class }, 
				new InvocationHandler() {		
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						// TODO Auto-generated method stub
						return null;
					}
		});
	}
	
}