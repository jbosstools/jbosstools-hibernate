package org.jboss.tools.hibernate.runtime.v_5_4.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.AbstractExporter;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;
import org.hibernate.tool.hbm2x.QueryExporter;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.jboss.tools.hibernate.runtime.v_5_4.internal.util.ConfigurationMetadataDescriptor;
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
	
	@Test
	public void testSetConfiguration() throws Exception {
		exporterTarget = new HibernateConfigurationExporter();
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporterTarget);
		Properties properties = new Properties();
		Configuration configurationTarget = new Configuration();
		configurationTarget.setProperties(properties);
		ConfigurationFacadeImpl configurationFacade1 = new ConfigurationFacadeImpl(FACADE_FACTORY, configurationTarget);
		exporterFacade.setConfiguration(configurationFacade1);	
		assertSame(properties, ((HibernateConfigurationExporter)exporterTarget).getCustomProperties());
		Field metadataDescriptorField = AbstractExporter.class.getDeclaredField("metadataDescriptor");
		metadataDescriptorField.setAccessible(true);
		ConfigurationMetadataDescriptor configurationMetadataDescriptor = (ConfigurationMetadataDescriptor)metadataDescriptorField.get(exporterTarget);
		assertNotNull(configurationMetadataDescriptor);
		Field configurationFacadeField = ConfigurationMetadataDescriptor.class.getDeclaredField("configuration");
		configurationFacadeField.setAccessible(true);
		ConfigurationFacadeImpl configurationFacade2 = (ConfigurationFacadeImpl)configurationFacadeField.get(configurationMetadataDescriptor);
		assertNotNull(configurationFacade2);
		assertSame(configurationFacade1, configurationFacade2);
	}
	
	@Test
	public void testSetArtifactCollector() {
		ArtifactCollector artifactCollectorTarget = new ArtifactCollector();
		IArtifactCollector artifactCollectorFacade = FACADE_FACTORY.createArtifactCollector(artifactCollectorTarget);
		exporterFacade.setArtifactCollector(artifactCollectorFacade);
		assertSame(
				exporterTarget.getArtifactCollector(), 
				artifactCollectorTarget);
	}
	
	@Test
	public void testSetOutputDirectory() {
		File file = new File("");
		exporterFacade.setOutputDirectory(file);
		assertSame(exporterTarget.getOutputDirectory(), file);		
	}
	
	@Test
	public void testSetTemplatePath() {
		String[] templatePath = new String[] {};
		exporterFacade.setTemplatePath(templatePath);
		assertSame(exporterTarget.getTemplatePath(), templatePath);
	}
	
	@Test
	public void testStart() throws Exception {
		assertFalse(((TestExporter)exporterTarget).started);
		exporterFacade.start();
		assertTrue(((TestExporter)exporterTarget).started);
	}
	
	@Test
	public void testGetProperties() throws Exception {
		Properties properties = new Properties();
		assertNotNull(exporterFacade.getProperties());
		assertNotSame(properties, exporterFacade.getProperties());
		Field propertiesField = AbstractExporter.class.getDeclaredField("properties");
		propertiesField.setAccessible(true);
		propertiesField.set(exporterTarget, properties);
		assertSame(properties, exporterFacade.getProperties());
	}
	
	@Test
	public void testGetGenericExporter() {
		IGenericExporter genericExporter = exporterFacade.getGenericExporter();
		assertNull(genericExporter);
		exporterTarget = new GenericExporter();
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporterTarget);
		genericExporter = exporterFacade.getGenericExporter();
		assertNotNull(genericExporter);
		assertSame(exporterTarget, ((IFacade)genericExporter).getTarget());
	}
	
	@Test
	public void testGetHbm2DDLExporter() {
		IHbm2DDLExporter hbm2DDLExporter = exporterFacade.getHbm2DDLExporter();
		assertNull(hbm2DDLExporter);
		exporterTarget = new Hbm2DDLExporter();
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporterTarget);
		hbm2DDLExporter = exporterFacade.getHbm2DDLExporter();
		assertNotNull(hbm2DDLExporter);
		assertSame(exporterTarget, ((IFacade)hbm2DDLExporter).getTarget());
	}
	
	@Test
	public void testGetQueryExporter() {
		IQueryExporter queryExporter = exporterFacade.getQueryExporter();
		assertNull(queryExporter);
		exporterTarget = new QueryExporter();
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporterTarget);
		queryExporter = exporterFacade.getQueryExporter();
		assertNotNull(queryExporter);
		assertSame(exporterTarget, ((IFacade)queryExporter).getTarget());
	}
	
	@Test
	public void testSetCustomProperties() {
		Properties properties = new Properties();
		exporterFacade.setCustomProperties(properties);
		assertNotSame(properties, ((TestExporter)exporterTarget).getProperties());
		exporterTarget = new HibernateConfigurationExporter();
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporterTarget);
		assertNotSame(properties, ((HibernateConfigurationExporter)exporterTarget).getCustomProperties());
		exporterFacade.setCustomProperties(properties);
		assertSame(properties, ((HibernateConfigurationExporter)exporterTarget).getCustomProperties());
	}
	
	@Test
	public void testSetOutput() {
		StringWriter stringWriter = new StringWriter();
		exporterFacade.setOutput(stringWriter);
		assertNotSame(stringWriter, ((TestExporter)exporterTarget).writer);
		exporterTarget = new HibernateConfigurationExporter();
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporterTarget);
		assertNotSame(stringWriter, ((HibernateConfigurationExporter)exporterTarget).getOutput());
		exporterFacade.setOutput(stringWriter);
		assertSame(stringWriter, ((HibernateConfigurationExporter)exporterTarget).getOutput());
	}
		
	private static class TestExporter extends AbstractExporter {
		Writer writer = new StringWriter();
		boolean started = false;
		@Override public void start() { started = true; }	
		@Override protected void doStart() {}
		@SuppressWarnings("unused")
		public void setOutput(Writer w) { writer = w; }
	}

}
