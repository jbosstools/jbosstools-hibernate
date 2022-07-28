package org.jboss.tools.hibernate.runtime.v_6_1.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Hbm2DDLExporterFacadeTest {
	
	private static final FacadeFactoryImpl FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IHbm2DDLExporter ddlExporterFacade = null;
	private DdlExporter ddlExporterTarget = null;
	
	@BeforeEach
	public void before() {
		ddlExporterTarget = new DdlExporter();
		ddlExporterFacade = new Hbm2DDLExporterFacadeImpl(FACADE_FACTORY, ddlExporterTarget) {};
	}
	
	@Test 
	public void testGetProperties() {
		assertNotNull(ddlExporterFacade.getProperties());
	}
	
	@Test
	public void testSetExport() {
		assertNull(ddlExporterFacade.getProperties().get(ExporterConstants.EXPORT_TO_DATABASE));
		ddlExporterFacade.setExport(false);
		assertFalse((Boolean)ddlExporterFacade.getProperties().get(ExporterConstants.EXPORT_TO_DATABASE));
		ddlExporterFacade.setExport(true);
		assertTrue((Boolean)ddlExporterFacade.getProperties().get(ExporterConstants.EXPORT_TO_DATABASE));
	}

}
