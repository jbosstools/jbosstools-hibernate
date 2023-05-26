package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IHbm2DDLExporterTest {
	
	private IHbm2DDLExporter ddlExporterFacade = null;
	private DdlExporter ddlExporterTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		ddlExporterFacade = (IHbm2DDLExporter)GenericFacadeFactory.createFacade(
				IHbm2DDLExporter.class, 
				WrapperFactory.createDdlExporterWrapper());
		ddlExporterTarget = (DdlExporter)((IFacade)ddlExporterFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(ddlExporterTarget);
		assertNotNull(ddlExporterFacade);
	}

}
