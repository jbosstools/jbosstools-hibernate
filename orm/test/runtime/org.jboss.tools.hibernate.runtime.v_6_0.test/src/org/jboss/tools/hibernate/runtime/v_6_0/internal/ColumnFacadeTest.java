package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.hibernate.boot.internal.BootstrapContextImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.junit.Before;
import org.junit.Test;

public class ColumnFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IColumn columnFacade = null; 
	private Column column = null;
	
	@Before
	public void beefore() {
		column = new Column();
		columnFacade = new ColumnFacadeImpl(FACADE_FACTORY, column);
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
		configuration.setProperty(Environment.DIALECT, TestDialect.class.getName());
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
	
	private MetadataBuildingContext createMetadataBuildingContext() {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySetting("hibernate.dialect", TestDialect.class.getName());
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
						bootstrapContext, 
						metadataBuildingOptions, 
						inFlightMetadataCollector);
	}
	
	public static class TestDialect extends Dialect {
		@Override
		public int getVersion() { return 0; }	
	}
	
	
}