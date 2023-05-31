package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IExporterTest {
	
	private IExporter exporterFacade = null;
	private Exporter exporterTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		exporterFacade = (IExporter)GenericFacadeFactory.createFacade(
				IExporter.class, 
				WrapperFactory.createExporterWrapper(DdlExporter.class.getName()));
		Object exporterWrapper = ((IFacade)exporterFacade).getTarget();
		exporterTarget = (Exporter)((Wrapper)exporterWrapper).getWrappedObject();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(exporterFacade);
		assertNotNull(exporterTarget);
	}

}
