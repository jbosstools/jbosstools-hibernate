package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.hibernate.tool.api.export.Exporter;
import org.junit.Before;
import org.junit.Test;

public class ExporterFacadeTest {
	
	private static final FacadeFactoryImpl FACADE_FACTORY = new FacadeFactoryImpl();
	
	private TestExporter exporterTarget = null;
	private ExporterFacadeImpl exporterFacade = null;
	
	@Before
	public void before() {
		exporterTarget = new TestExporter();
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporterTarget);
	}
	
	@Test
	public void testCreation() {
		assertNotNull(exporterFacade);
	}
	
	private static class TestExporter implements Exporter {

		@Override
		public Properties getProperties() {
			return null;
		}

		@Override
		public void start() {
		}
		
	}

}
