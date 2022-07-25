package org.jboss.tools.hibernate.runtime.v_6_1.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
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
	
}