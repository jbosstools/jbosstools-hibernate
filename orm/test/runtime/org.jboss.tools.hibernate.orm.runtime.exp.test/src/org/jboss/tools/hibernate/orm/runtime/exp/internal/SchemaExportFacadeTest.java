package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.EnumSet;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.hibernate.tool.schema.TargetType;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SchemaExportFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	
	private SchemaExportFacadeImpl schemaExportFacade = null;
	private SchemaExport schemaExportTarget = null;
	
	@BeforeEach
	public void before() {
		schemaExportTarget = new SchemaExport();
		schemaExportFacade = new SchemaExportFacadeImpl(FACADE_FACTORY, schemaExportTarget);
	}
	
	@Test
	public void testCreation() {
		assertNotNull(schemaExportFacade);
		assertNull(schemaExportFacade.metadata);
		assertSame(schemaExportFacade.target, schemaExportTarget);
	}
	
	@Test
	public void testSetConfiguration() {
		Configuration configurationTarget = new Configuration();
		configurationTarget.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configurationTarget.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		ConfigurationFacadeImpl configuration = new ConfigurationFacadeImpl(FACADE_FACTORY, configurationTarget);
		Metadata metadata = configuration.getMetadata();
		assertNull(schemaExportFacade.metadata);
		schemaExportFacade.setConfiguration(configuration);
		assertSame(metadata, schemaExportFacade.metadata);
	}
	
	@Test
	public void testCreate() {
		TestSchemaExport target = new TestSchemaExport();
		schemaExportFacade.target = target;
		schemaExportFacade.metadata = createTestMetadata();
		assertNull(target.metadata);
		assertNull(target.targetTypes);
		schemaExportFacade.create();	
		assertSame(target.metadata, schemaExportFacade.metadata);
		assertTrue(target.targetTypes.contains(TargetType.DATABASE));
	}
	
	private static class TestSchemaExport extends SchemaExport {
		Metadata metadata = null;
		EnumSet<TargetType> targetTypes = null;
		@Override
		public void create(EnumSet<TargetType> targetTypes, Metadata metadata) {
			this.targetTypes = targetTypes;
			this.metadata = metadata;
		}
	}
	
	private static Metadata createTestMetadata() {
		return (Metadata)Proxy.newProxyInstance(
				SchemaExportFacadeTest.class.getClassLoader(), 
				new Class[] { Metadata.class },  
				new InvocationHandler() {					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
				});
	}

}
