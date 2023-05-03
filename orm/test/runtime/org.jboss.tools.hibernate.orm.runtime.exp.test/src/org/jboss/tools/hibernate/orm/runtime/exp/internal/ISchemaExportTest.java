package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ISchemaExportTest {
	
	private ISchemaExport schemaExportFacade = null;
	private SchemaExport schemaExportTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		schemaExportFacade = (ISchemaExport)GenericFacadeFactory.createFacade(
				ISchemaExport.class, 
				WrapperFactory.createSchemaExport());
		schemaExportTarget = (SchemaExport)((IFacade)schemaExportFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(schemaExportFacade);
		assertNotNull(schemaExportTarget);
	}
	
	

}
