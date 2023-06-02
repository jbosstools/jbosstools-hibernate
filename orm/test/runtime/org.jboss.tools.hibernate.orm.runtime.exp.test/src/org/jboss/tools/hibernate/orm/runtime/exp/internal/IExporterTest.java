package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.export.common.AbstractExporter;
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
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
				WrapperFactory.createExporterWrapper(TestExporter.class.getName()));
		Object exporterWrapper = ((IFacade)exporterFacade).getTarget();
		exporterTarget = (Exporter)((Wrapper)exporterWrapper).getWrappedObject();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(exporterFacade);
		assertNotNull(exporterTarget);
	}

	@Test
	public void testSetConfiguration() throws Exception {
		Object metadataDescriptor = null;
		Object configuration = null;
		Field field = ConfigurationMetadataDescriptor.class.getDeclaredField("configuration");
		field.setAccessible(true);
		Properties properties = new Properties();
		IConfiguration configurationFacade = NewFacadeFactory.INSTANCE.createNativeConfiguration();
		configurationFacade.setProperties(properties);
		// First use the TestExporter 
		assertNull(exporterTarget.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR));
		exporterFacade.setConfiguration(configurationFacade);	
		metadataDescriptor = exporterTarget.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor);
		assertTrue(metadataDescriptor instanceof ConfigurationMetadataDescriptor);
		configuration = field.get(metadataDescriptor);
		assertNotNull(configuration);
		assertTrue(configuration instanceof Configuration);
		assertSame(configuration, ((IFacade)configurationFacade).getTarget());
		// Now test with a CfgExporter
		exporterFacade = (IExporter)GenericFacadeFactory.createFacade(
				IExporter.class, 
				WrapperFactory.createExporterWrapper(CfgExporter.class.getName()));
		Object exporterWrapper = ((IFacade)exporterFacade).getTarget();
		exporterTarget = (Exporter)((Wrapper)exporterWrapper).getWrappedObject();
		assertNotSame(properties, ((CfgExporter)exporterTarget).getCustomProperties());
		assertNull(exporterTarget.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR));
		exporterFacade.setConfiguration(configurationFacade);	
		assertSame(properties, ((CfgExporter)exporterTarget).getCustomProperties());
		metadataDescriptor = exporterTarget.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor);
		assertTrue(metadataDescriptor instanceof ConfigurationMetadataDescriptor);
		configuration = field.get(metadataDescriptor);
		assertNotNull(configuration);
		assertTrue(configuration instanceof Configuration);
		assertSame(configuration, ((IFacade)configurationFacade).getTarget());
}
	
	@Test
	public void testSetArtifactCollector() {
		IArtifactCollector artifactCollectorFacade = NewFacadeFactory.INSTANCE.createArtifactCollector();
		Object artifactCollectorTarget = ((IFacade)artifactCollectorFacade).getTarget();
		assertNotSame(artifactCollectorTarget, exporterTarget.getProperties().get(ExporterConstants.ARTIFACT_COLLECTOR));
		exporterFacade.setArtifactCollector(artifactCollectorFacade);
		assertSame(artifactCollectorTarget, exporterTarget.getProperties().get(ExporterConstants.ARTIFACT_COLLECTOR));
	}
	
	@Test
	public void testSetOutputDirectory() {
		File file = new File("");
		assertNotSame(file, exporterTarget.getProperties().get(ExporterConstants.DESTINATION_FOLDER));		
		exporterFacade.setOutputDirectory(file);
		assertSame(file, exporterTarget.getProperties().get(ExporterConstants.DESTINATION_FOLDER));		
	}
	
	@Test
	public void testSetTemplatePath() {
		String[] templatePath = new String[] {};
		assertNotSame(templatePath, exporterTarget.getProperties().get(ExporterConstants.TEMPLATE_PATH));		
		exporterFacade.setTemplatePath(templatePath);
		assertSame(templatePath, exporterTarget.getProperties().get(ExporterConstants.TEMPLATE_PATH));		
	}
	
	@Test
	public void testStart() throws Exception {
		assertFalse(((TestExporter)exporterTarget).started);
		exporterFacade.start();
		assertTrue(((TestExporter)exporterTarget).started);
	}
	
	@Test
	public void testGetProperties() throws Exception {
		Field propertiesField = AbstractExporter.class.getDeclaredField("properties");
		propertiesField.setAccessible(true);
		Properties properties = new Properties();
		assertNotNull(exporterFacade.getProperties());
		assertNotSame(properties, exporterFacade.getProperties());
		propertiesField.set(exporterTarget, properties);
		assertSame(properties, exporterFacade.getProperties());
	}
	
	public static class TestExporter extends AbstractExporter {
		private boolean started = false;
		@Override protected void doStart() {}
		@Override public void start() { started = true; }		
	}
	
}
