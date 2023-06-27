package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.export.common.AbstractExporter;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.internal.export.query.QueryExporter;
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;
import org.hibernate.tool.orm.jbt.util.DummyMetadataDescriptor;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
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
		metadataDescriptor = exporterTarget.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor);
		assertTrue(metadataDescriptor instanceof ConfigurationMetadataDescriptor);
		configuration = field.get(metadataDescriptor);
		assertNotNull(configuration);
		assertTrue(configuration instanceof Configuration);
		assertNotSame(configuration, ((IFacade)configurationFacade).getTarget());
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
		metadataDescriptor = exporterTarget.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor);
		assertTrue(metadataDescriptor instanceof DummyMetadataDescriptor);
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
	
	@Test
	public void testGetGenericExporter() {
		// TestExporter should not return a GenericExporterFacade instance
		assertNull(exporterFacade.getGenericExporter());
		// try now with a GenericExporter
		exporterFacade = (IExporter)GenericFacadeFactory.createFacade(
				IExporter.class, 
				WrapperFactory.createExporterWrapper(GenericExporter.class.getName()));
		IGenericExporter genericExporterFacade = exporterFacade.getGenericExporter();
		assertNotNull(genericExporterFacade);
		Object exporterWrapper = ((IFacade)exporterFacade).getTarget();
		exporterTarget = (Exporter)((Wrapper)exporterWrapper).getWrappedObject();
		Object genericExporterWrapper = ((IFacade)genericExporterFacade).getTarget();
		Object genericExporterTarget = ((Wrapper)genericExporterWrapper).getWrappedObject();
		assertSame(exporterTarget, genericExporterTarget);
	}
	
	@Test
	public void testGetHbm2DDLExporter() {
		// TestExporter should not return a Hbm2DDLExporterFacade instance
		assertNull(exporterFacade.getHbm2DDLExporter());
		// try now with a DdlExporter
		exporterFacade = (IExporter)GenericFacadeFactory.createFacade(
				IExporter.class, 
				WrapperFactory.createExporterWrapper(DdlExporter.class.getName()));
		IHbm2DDLExporter hbm2DDLExporterFacade = exporterFacade.getHbm2DDLExporter();
		assertNotNull(hbm2DDLExporterFacade);
		Object exporterWrapper = ((IFacade)exporterFacade).getTarget();
		exporterTarget = (Exporter)((Wrapper)exporterWrapper).getWrappedObject();
		Object hbm2DDLExporterWrapper = ((IFacade)hbm2DDLExporterFacade).getTarget();
		Object hbm2DDLExporterTarget = ((Wrapper)hbm2DDLExporterWrapper).getWrappedObject();
		assertSame(exporterTarget, hbm2DDLExporterTarget);
	}
	
	@Test
	public void testGetQueryExporter() {
		// TestExporter should not return a Hbm2DDLExporterFacade instance
		assertNull(exporterFacade.getQueryExporter());
		// try now with a QueryExporter
		exporterFacade = (IExporter)GenericFacadeFactory.createFacade(
				IExporter.class, 
				WrapperFactory.createExporterWrapper(QueryExporter.class.getName()));
		IQueryExporter queryExporterFacade = exporterFacade.getQueryExporter();
		assertNotNull(queryExporterFacade);
		Object exporterWrapper = ((IFacade)exporterFacade).getTarget();
		exporterTarget = (Exporter)((Wrapper)exporterWrapper).getWrappedObject();
		Object queryExporterWrapper = ((IFacade)queryExporterFacade).getTarget();
		Object queryExporterTarget = ((Wrapper)queryExporterWrapper).getWrappedObject();
		assertSame(exporterTarget, queryExporterTarget);
	}
	
	@Test
	public void testSetCustomProperties() {
		Properties properties = new Properties();
		// 'setCustomProperties()' should not be called on other exporters than CfgExporter
		assertNull(((TestExporter)exporterTarget).props);
		exporterFacade.setCustomProperties(properties);
		assertNull(((TestExporter)exporterTarget).props);
		// try now with CfgExporter 
		exporterFacade = (IExporter)GenericFacadeFactory.createFacade(
				IExporter.class, 
				WrapperFactory.createExporterWrapper(CfgExporter.class.getName()));
		Object exporterWrapper = ((IFacade)exporterFacade).getTarget();
		exporterTarget = (Exporter)((Wrapper)exporterWrapper).getWrappedObject();
		assertNotSame(properties, ((CfgExporter)exporterTarget).getCustomProperties());
		exporterFacade.setCustomProperties(properties);
		assertSame(properties, ((CfgExporter)exporterTarget).getCustomProperties());
	}
	
	@Test
	public void testSetOutput() {
		StringWriter stringWriter = new StringWriter();
		// 'setCustomProperties()' should not be called on other exporters than CfgExporter
		assertNull(((TestExporter)exporterTarget).output);
		exporterFacade.setOutput(stringWriter);
		assertNull(((TestExporter)exporterTarget).output);
		// try now with CfgExporter 
		exporterFacade = (IExporter)GenericFacadeFactory.createFacade(
				IExporter.class, 
				WrapperFactory.createExporterWrapper(CfgExporter.class.getName()));
		Object exporterWrapper = ((IFacade)exporterFacade).getTarget();
		exporterTarget = (Exporter)((Wrapper)exporterWrapper).getWrappedObject();
		assertNotSame(stringWriter, ((CfgExporter)exporterTarget).getOutput());
		exporterFacade.setOutput(stringWriter);
		assertSame(stringWriter, ((CfgExporter)exporterTarget).getOutput());
	}
	
	public static class TestExporter extends AbstractExporter {
		private boolean started = false;
		private Properties props = null;
		private StringWriter output = null;
		@Override protected void doStart() {}
		@Override public void start() { started = true; }
		public void setCustomProperties(Properties p) {
			props = p;
		}
		
	}
	
}
