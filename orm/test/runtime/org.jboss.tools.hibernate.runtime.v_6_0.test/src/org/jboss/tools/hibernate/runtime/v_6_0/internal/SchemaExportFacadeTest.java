package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.junit.Before;
import org.junit.Test;

public class SchemaExportFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	
	private SchemaExportFacadeImpl schemaExportFacade = null;
	private SchemaExport schemaExportTarget = null;
	
	@Before
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
		configurationTarget.setProperty("hibernate.dialect", TestDialect.class.getName());
		ConfigurationFacadeImpl configuration = new ConfigurationFacadeImpl(FACADE_FACTORY, configurationTarget);
		Metadata metadata = configuration.getMetadata();
		assertNull(schemaExportFacade.metadata);
		schemaExportFacade.setConfiguration(configuration);
		assertSame(metadata, schemaExportFacade.metadata);
	}
	
	public static class TestDialect extends Dialect {
		@Override
		public int getVersion() {
			return 0;
		}
	}

}
