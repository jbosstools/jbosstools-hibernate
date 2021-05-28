package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Hbm2DDLExporterFacadeTest {

	private static final FacadeFactoryImpl FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IHbm2DDLExporter ddlExporterFacade = null;
	private Hbm2DDLExporter ddlExporterTarget = null;
	
	@BeforeEach
	public void before() {
		ddlExporterTarget = new Hbm2DDLExporter();
		ddlExporterFacade = new Hbm2DDLExporterFacadeImpl(FACADE_FACTORY, ddlExporterTarget);
	}
	
	@Test 
	public void testConstruction() {
		assertNotNull(ddlExporterFacade);
	}
	
}
