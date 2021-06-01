package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;

import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HibernateMappingExporterFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	@TempDir
	public File outputDir = new File("output");
	
	private IHibernateMappingExporter hibernateMappingExporterFacade = null; 
	private HibernateMappingExporterExtension hibernateMappingExporter = null;
		
	@BeforeEach
	public void beforeEach() throws Exception {
		hibernateMappingExporter = new HibernateMappingExporterExtension(FACADE_FACTORY, null, null);
		hibernateMappingExporterFacade = 
				new HibernateMappingExporterFacadeImpl(FACADE_FACTORY, hibernateMappingExporter);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(hibernateMappingExporterFacade);
	}
	
}
