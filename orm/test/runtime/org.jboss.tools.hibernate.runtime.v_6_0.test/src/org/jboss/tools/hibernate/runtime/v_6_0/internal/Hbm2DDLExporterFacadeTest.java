package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.junit.Before;
import org.junit.Test;

public class Hbm2DDLExporterFacadeTest {
	
	private static final FacadeFactoryImpl FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IHbm2DDLExporter ddlExporterFacade = null;
	private DdlExporter ddlExporterTarget = null;
	
	@Before
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
