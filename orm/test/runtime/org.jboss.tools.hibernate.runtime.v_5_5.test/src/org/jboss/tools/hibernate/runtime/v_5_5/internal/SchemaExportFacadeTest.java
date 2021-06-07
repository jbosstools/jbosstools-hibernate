package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;
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
		configurationTarget.setProperty("hibernate.dialect", TestDialect.class.getName());
		ConfigurationFacadeImpl configuration = new ConfigurationFacadeImpl(FACADE_FACTORY, configurationTarget);
		Metadata metadata = configuration.getMetadata();
		assertNull(metadataField.get(schemaExportFacade));
		schemaExportFacade.setConfiguration(configuration);
		assertSame(metadata, metadataField.get(schemaExportFacade));
	}
	
	public static class TestDialect extends Dialect {}
	
}
