package org.jboss.tools.hibernate.orm.runtime.v_6_6;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.List;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.hibernate.tool.orm.jbt.wrp.SchemaExportWrapper;
import org.hibernate.tool.schema.TargetType;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
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
	
	@Test
	public void testGetExceptions() throws Exception {
		Field exceptionsField = SchemaExport.class.getDeclaredField("exceptions");
		exceptionsField.setAccessible(true);
		@SuppressWarnings("unchecked")
		List<Throwable> exceptionList = (List<Throwable>)exceptionsField.get(schemaExportTarget);
		assertTrue(exceptionList.isEmpty());
		Throwable t = new RuntimeException("foobar");
		exceptionList.add(t);
		List<Throwable> list = schemaExportFacade.getExceptions();
		assertSame(list, exceptionList);
		assertTrue(list.contains(t));
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
