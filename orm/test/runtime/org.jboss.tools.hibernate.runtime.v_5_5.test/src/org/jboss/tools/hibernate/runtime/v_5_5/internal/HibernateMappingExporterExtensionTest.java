package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.File;
import java.lang.reflect.Field;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.AbstractExporter;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.v_5_5.internal.util.ConfigurationMetadataDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HibernateMappingExporterExtensionTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private HibernateMappingExporterExtension hibernateMappingExporterExtension = null;
	private IConfiguration configurationFacade = null;
	private File file = null;

	@BeforeEach
	public void beforeEach() throws Exception {
		configurationFacade = FACADE_FACTORY.createConfiguration(new Configuration());
		file = new File("test");
		hibernateMappingExporterExtension = 
				new HibernateMappingExporterExtension(
						FACADE_FACTORY, 
						configurationFacade, 
						file);
	}
	
	@Test
	public void testConstruction() throws Exception {
		Field facadeFactoryField = HibernateMappingExporterExtension.class.getDeclaredField("facadeFactory");
		facadeFactoryField.setAccessible(true);
		assertSame(FACADE_FACTORY, facadeFactoryField.get(hibernateMappingExporterExtension));
		Field metadataDescriptorField = AbstractExporter.class.getDeclaredField("metadataDescriptor");
		metadataDescriptorField.setAccessible(true);
		ConfigurationMetadataDescriptor cmdd = (ConfigurationMetadataDescriptor)metadataDescriptorField.get(hibernateMappingExporterExtension);
		Field configurationFacadeField = ConfigurationMetadataDescriptor.class.getDeclaredField("configurationFacade");
		configurationFacadeField.setAccessible(true);
		assertSame(configurationFacade, configurationFacadeField.get(cmdd));
		Field outputDirField = AbstractExporter.class.getDeclaredField("outputdir");
		outputDirField.setAccessible(true);
		assertSame(file, outputDirField.get(hibernateMappingExporterExtension));
	}
	
}
