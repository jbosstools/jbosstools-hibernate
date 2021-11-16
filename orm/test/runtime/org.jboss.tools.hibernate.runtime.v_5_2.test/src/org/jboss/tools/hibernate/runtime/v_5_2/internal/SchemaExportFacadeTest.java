package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.jboss.tools.hibernate.runtime.common.AbstractFacade;
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
	public void testCreation() throws Exception {
		assertNotNull(schemaExportFacade);
		Field metadataField = SchemaExportFacadeImpl.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		assertNull(metadataField.get(schemaExportFacade));
	}
	
	@Test
	public void testSetConfiguration() throws Exception {
		Field metadataField = SchemaExportFacadeImpl.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		Configuration configurationTarget = new Configuration();
		ConfigurationFacadeImpl configuration = new ConfigurationFacadeImpl(FACADE_FACTORY, configurationTarget);
		Metadata metadata = configuration.getMetadata();
		assertNull(metadataField.get(schemaExportFacade));
		schemaExportFacade.setConfiguration(configuration);
		assertSame(metadata, metadataField.get(schemaExportFacade));
	}
	
	@Test
	public void testCreate() throws Exception {
		TestSchemaExport target = new TestSchemaExport();
		Field targetField = AbstractFacade.class.getDeclaredField("target");
		targetField.setAccessible(true);
		targetField.set(schemaExportFacade, target);
		Field metadataField = SchemaExportFacadeImpl.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		metadataField.set(schemaExportFacade, createTestMetadata());
		assertNull(target.metadata);
		assertNull(target.targetTypes);
		schemaExportFacade.create();	
		assertSame(target.metadata, metadataField.get(schemaExportFacade));
		assertTrue(target.targetTypes.contains(TargetType.DATABASE));
	}
	
	@Test
	public void testGetExceptions() throws Exception {
		List<Throwable> exceptions = Collections.emptyList();
		Field exceptionsField = SchemaExport.class.getDeclaredField("exceptions");
		exceptionsField.setAccessible(true);
		assertNotSame(exceptions, schemaExportFacade.getExceptions());
		exceptionsField.set(schemaExportTarget, exceptions);
		assertSame(exceptions, schemaExportFacade.getExceptions());
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
