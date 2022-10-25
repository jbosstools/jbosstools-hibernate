package org.jboss.tools.hibernate.orm.runtime.exp.internal;

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
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.internal.export.query.QueryExporter;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.ConfigurationMetadataDescriptor;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
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
	public void testCreation() {
		assertNotNull(exporterFacade);
	}
	
	@Test
	public void testSetConfiguration() throws Exception {
		exporterTarget = new CfgExporter();
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporterTarget);
		Properties properties = new Properties();
		Configuration configurationTarget = new Configuration();
		configurationTarget.setProperties(properties);
		ConfigurationFacadeImpl configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configurationTarget);
		exporterFacade.setConfiguration(configurationFacade);	
		assertSame(properties, ((CfgExporter)exporterTarget).getCustomProperties());
		Object object = exporterTarget.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(object);
		assertTrue(object instanceof ConfigurationMetadataDescriptor);
		ConfigurationMetadataDescriptor configurationMetadataDescriptor = (ConfigurationMetadataDescriptor)object;
		Field field = ConfigurationMetadataDescriptor.class.getDeclaredField("configurationFacade");
		field.setAccessible(true);
		object = field.get(configurationMetadataDescriptor);
		assertNotNull(object);
		assertTrue(object instanceof ConfigurationFacadeImpl);
		assertSame(object, configurationFacade);
	}
	
	@Test
	public void testSetArtifactCollector() {
		assertNull(exporterTarget.getProperties().get(ExporterConstants.ARTIFACT_COLLECTOR));
		IArtifactCollector artifactCollectorFacade = FACADE_FACTORY.createArtifactCollector(null);
		exporterFacade.setArtifactCollector(artifactCollectorFacade);
		assertNotNull(exporterTarget.getProperties().get(ExporterConstants.ARTIFACT_COLLECTOR));
	}
	
	@Test
	public void testSetOutputDirectory() {
		File file = new File("");
		exporterFacade.setOutputDirectory(file);
		assertSame(
				exporterTarget.getProperties().get(ExporterConstants.DESTINATION_FOLDER),
				file);		
	}
	
	@Test
	public void testSetTemplatePath() {
		String[] templatePath = new String[] {};
		exporterFacade.setTemplatePath(templatePath);
		assertSame(
				exporterTarget.getProperties().get(ExporterConstants.TEMPLATE_PATH),
				templatePath);
	}
	
	@Test
	public void testStart() throws Exception {
		assertFalse(((TestExporter)exporterTarget).started);
		exporterFacade.start();
		assertTrue(((TestExporter)exporterTarget).started);
	}
	
	@Test
	public void testGetProperties() {
		Properties properties = new Properties();
		assertNotNull(exporterFacade.getProperties());
		assertNotSame(properties, exporterFacade.getProperties());
		((TestExporter)exporterTarget).properties = properties;
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
		exporterTarget = new DdlExporter();
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
		assertNotSame(properties, ((TestExporter)exporterTarget).properties);
		exporterTarget = new CfgExporter();
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporterTarget);
		assertNotSame(properties, ((CfgExporter)exporterTarget).getCustomProperties());
		exporterFacade.setCustomProperties(properties);
		assertSame(properties, ((CfgExporter)exporterTarget).getCustomProperties());
	}
	
	@Test
	public void testSetOutput() {
		StringWriter stringWriter = new StringWriter();
		exporterFacade.setOutput(stringWriter);
		assertNotSame(stringWriter, ((TestExporter)exporterTarget).writer);
		exporterTarget = new CfgExporter();
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporterTarget);
		exporterFacade.setOutput(stringWriter);
		assertSame(stringWriter, ((CfgExporter)exporterTarget).getOutput());
	}
		
	private static class TestExporter implements Exporter {		

		Writer writer = null;
		Properties properties = new Properties();
		boolean started = false;

		@Override
		public Properties getProperties() {
			return properties;
		}

		@Override
		public void start() {
			started = true;
		}
		
		@SuppressWarnings("unused")
		public void setCustomProperties(Properties p) {
			properties = p;
		}
		
		@SuppressWarnings("unused")
		public void setOutput(Writer w) {
			writer = w;
		}
		
	}

}
