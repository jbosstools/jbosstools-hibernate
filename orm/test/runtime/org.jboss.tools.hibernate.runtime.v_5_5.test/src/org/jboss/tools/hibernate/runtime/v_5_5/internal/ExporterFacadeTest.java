package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.hbm2x.AbstractExporter;
import org.hibernate.tool.hbm2x.Exporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExporterFacadeTest {

	private static final FacadeFactoryImpl FACADE_FACTORY = new FacadeFactoryImpl();
	
	private Exporter exporterTarget = null;
	private ExporterFacadeImpl exporterFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		exporterTarget = new TestExporter();
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporterTarget);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(exporterFacade);
	}
	
	private static class TestExporter extends AbstractExporter {

		@Override
		protected void doStart() {
		}		

	}

}
