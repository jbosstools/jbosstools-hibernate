package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class IHibernateMappingExporterTest {
	
	@TempDir
	public File outputDir;
	
	private IHibernateMappingExporter hbmExporterFacade = null;
	private HbmExporter hbmExporterTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		Configuration configuration = new Configuration();
		File file = new File(outputDir, "foo");
		hbmExporterTarget = (HbmExporter)WrapperFactory.createHbmExporterWrapper(configuration, file);
		hbmExporterFacade = (IHibernateMappingExporter)GenericFacadeFactory
				.createFacade(IHibernateMappingExporter.class, hbmExporterTarget);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(hbmExporterTarget);
		assertNotNull(hbmExporterFacade);
	}

}
