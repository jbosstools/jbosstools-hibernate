package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class HibernateMappingExporterFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private IHibernateMappingExporter hibernateMappingExporterFacade = null; 
	private HibernateMappingExporterExtension hibernateMappingExporter = null;
	
	private File outputDir = null;
	
	@Before
	public void setUp() throws Exception {
		hibernateMappingExporter = new HibernateMappingExporterExtension(FACADE_FACTORY, null, null);
		hibernateMappingExporterFacade = 
				new HibernateMappingExporterFacadeImpl(FACADE_FACTORY, hibernateMappingExporter);
		outputDir = temporaryFolder.getRoot();
	}
	
	@Test
	public void testCreation() {
		assertNotNull(hibernateMappingExporterFacade);
	}
	
}
