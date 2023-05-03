package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.hibernate.tool.orm.jbt.wrp.SchemaExportWrapper;
import org.hibernate.tool.schema.TargetType;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ISchemaExportTest {
	
	private ISchemaExport schemaExportFacade = null;
	private TestSchemaExport schemaExportTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		Configuration configuration = new Configuration();
		configuration.setProperty(Environment.DIALECT, MockDialect.class.getName());
		configuration.setProperty(Environment.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		schemaExportTarget = new TestSchemaExport(configuration);
		schemaExportFacade = (ISchemaExport)GenericFacadeFactory.createFacade(
				ISchemaExport.class, 
				schemaExportTarget);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(schemaExportFacade);
		assertNotNull(schemaExportTarget);
	}
	
	@Test
	public void testCreate() {
		assertFalse(schemaExportTarget.created);
		schemaExportFacade.create();
		assertTrue(schemaExportTarget.created);
	}
	
	private class TestSchemaExport extends SchemaExportWrapper {
		
		private boolean created = false;

		public TestSchemaExport(Configuration configuration) {
			super(configuration);
		}
		
		@Override 
		public void create(EnumSet<TargetType> targetTypes, Metadata metadata) {
			created = true;
		}

		
	}
	
}
