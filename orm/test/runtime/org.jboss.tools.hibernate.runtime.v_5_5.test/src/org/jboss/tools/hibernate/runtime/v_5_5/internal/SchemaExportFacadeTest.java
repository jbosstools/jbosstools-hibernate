package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.jboss.tools.hibernate.runtime.common.AbstractSchemaExportFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SchemaExportFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	
	private ISchemaExport schemaExportFacade = null;
	private SchemaExport schemaExportTarget = null;
	
	@BeforeEach
	public void before() {
		schemaExportTarget = new SchemaExport();
		schemaExportFacade = new AbstractSchemaExportFacade(FACADE_FACTORY, schemaExportTarget) {};
	}
	
	@Test
	public void testCreation() {
		assertNotNull(schemaExportFacade);
		assertSame(((IFacade)schemaExportFacade).getTarget(), schemaExportTarget);
	}
	
}
