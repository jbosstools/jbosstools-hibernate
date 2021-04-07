package org.jboss.tools.hibernate.runtime.v_5_4.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.AbstractExporter;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;
import org.hibernate.tool.hbm2x.QueryExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractArtifactCollectorFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.jboss.tools.hibernate.runtime.v_5_4.internal.util.ConfigurationMetadataDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExporterFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IExporter exporterFacade = null; 
	private Exporter exporter = null;
	
	private String methodName = null;
	private Object[] arguments = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		exporter = (Exporter)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Exporter.class }, 
				new TestInvocationHandler());
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporter);
	}
	
	@Test
	public void testSetConfiguration() {
		Configuration cfg = new Configuration();
		IConfiguration configuration = new AbstractConfigurationFacade(FACADE_FACTORY, cfg) {};
		exporterFacade.setConfiguration(configuration);
		assertEquals("setMetadataDescriptor", methodName);
		Object argument = arguments[0];
		assertTrue(argument instanceof ConfigurationMetadataDescriptor);
		ConfigurationMetadataDescriptor metadataDescriptor = (ConfigurationMetadataDescriptor)argument;
		assertSame(configuration, metadataDescriptor.getConfiguration());
		
	}
	
	@Test
	public void testSetArtifactCollector() {
		ArtifactCollector ac = new ArtifactCollector();
		IArtifactCollector artifactCollector = new AbstractArtifactCollectorFacade(FACADE_FACTORY, ac) {};
		exporterFacade.setArtifactCollector(artifactCollector);
		assertEquals("setArtifactCollector", methodName);
		assertArrayEquals(new Object[] { ac }, arguments);
	}
	
	@Test
	public void testSetOutputDirectory() {
		File file = new File("");
		exporterFacade.setOutputDirectory(file);
		assertEquals("setOutputDirectory", methodName);
		assertArrayEquals(new Object[] { file }, arguments);
	}
	
	@Test
	public void testSetTemplatePath() {
		String[] templatePath = new String[] {};
		exporterFacade.setTemplatePath(templatePath);
		assertEquals("setTemplatePath", methodName);
		assertArrayEquals(new Object[] { templatePath }, arguments);
	}
	
	@Test
	public void testStart() throws Exception {
		Exporter exporter = new AbstractExporter() {
			@Override
			protected void doStart() {
				methodName = "start";
			}		
			@Override
			protected Metadata buildMetadata() {
				return null;
			}
		};
		ArtifactCollector artifactCollector = new ArtifactCollector();
		File testFile = File.createTempFile("test", "xml");
		FileWriter writer = new FileWriter(testFile);
		writer.write("<test/>");
		writer.flush();
		writer.close();
		artifactCollector.addFile(testFile, "xml");
		exporter.setArtifactCollector(artifactCollector);
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporter);
		exporterFacade.start();
		assertEquals("start", methodName);
	}
	
	@Test
	public void testGetProperties() {
		assertNull(exporterFacade.getProperties());
		assertEquals("getProperties", methodName);
		assertNull(arguments);
	}
	
	@Test
	public void testGetGenericExporter() {
		IGenericExporter genericExporter = exporterFacade.getGenericExporter();
		assertNull(genericExporter);
		exporter = new GenericExporter();
		exporterFacade = new AbstractExporterFacade(FACADE_FACTORY, exporter) {};
		genericExporter = exporterFacade.getGenericExporter();
		assertNotNull(genericExporter);
		assertSame(exporter, ((IFacade)genericExporter).getTarget());
	}
	
	@Test
	public void testGetHbm2DDLExporter() {
		IHbm2DDLExporter hbm2DDLExporter = exporterFacade.getHbm2DDLExporter();
		assertNull(hbm2DDLExporter);
		exporter = new Hbm2DDLExporter();
		exporterFacade = new AbstractExporterFacade(FACADE_FACTORY, exporter) {};
		hbm2DDLExporter = exporterFacade.getHbm2DDLExporter();
		assertNotNull(hbm2DDLExporter);
		assertSame(exporter, ((IFacade)hbm2DDLExporter).getTarget());
	}
	
	@Test
	public void testGetQueryExporter() {
		IQueryExporter queryExporter = exporterFacade.getQueryExporter();
		assertNull(queryExporter);
		exporter = new QueryExporter();
		exporterFacade = new AbstractExporterFacade(FACADE_FACTORY, exporter) {};
		queryExporter = exporterFacade.getQueryExporter();
		assertNotNull(queryExporter);
		assertSame(exporter, ((IFacade)queryExporter).getTarget());
	}
	
	@Test
	public void testSetCustomProperties() {
		Exporter exporter = new GenericExporter();
		exporterFacade = FACADE_FACTORY.createExporter(exporter);
		Properties properties = new Properties();
		exporterFacade.setCustomProperties(properties);
		exporter = new HibernateConfigurationExporter();
		exporterFacade = FACADE_FACTORY.createExporter(exporter);
		exporterFacade.setCustomProperties(properties);
		assertSame(properties, ((HibernateConfigurationExporter)exporter).getCustomProperties());
	}
	
	@Test
	public void testSetOutput() {
		Exporter exporter = new GenericExporter();
		exporterFacade = FACADE_FACTORY.createExporter(exporter);
		StringWriter stringWriter = new StringWriter();
		exporterFacade.setOutput(stringWriter);
		exporter = new HibernateConfigurationExporter();
		exporterFacade = FACADE_FACTORY.createExporter(exporter);
		exporterFacade.setOutput(stringWriter);
		assertSame(stringWriter, ((HibernateConfigurationExporter)exporter).getOutput());
	}
		
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			methodName = method.getName();
			arguments = args;
			return null;
		}
	}
	
}
