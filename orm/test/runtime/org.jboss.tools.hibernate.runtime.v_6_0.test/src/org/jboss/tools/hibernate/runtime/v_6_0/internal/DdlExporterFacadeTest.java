package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertNotNull;

import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractHbm2DDLExporterFacade;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.junit.Before;
import org.junit.Test;

public class DdlExporterFacadeTest {
	
	private static final FacadeFactoryImpl FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IHbm2DDLExporter ddlExporterFacade = null;
	private DdlExporter ddlExporterTarget = null;
	
	@Before
	public void before() {
		ddlExporterTarget = new DdlExporter();
		ddlExporterFacade = new AbstractHbm2DDLExporterFacade(FACADE_FACTORY, ddlExporterTarget) {};
	}
	
	@Test 
	public void testGetProperties() {
		assertNotNull(ddlExporterFacade.getProperties());
	}

}
