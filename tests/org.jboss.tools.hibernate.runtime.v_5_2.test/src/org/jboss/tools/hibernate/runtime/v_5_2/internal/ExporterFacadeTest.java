package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ExporterFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IExporter exporterFacade = null; 
	private Exporter exporter = null;
	
	private String methodName = null;
	private Object[] arguments = null;
	
	@Before
	public void setUp() throws Exception {
		exporter = (Exporter)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Exporter.class }, 
				new TestInvocationHandler());
		exporterFacade = new AbstractExporterFacade(FACADE_FACTORY, exporter) {};
	}
	
	@Test
	public void testSetConfiguration() {
		Configuration cfg = new Configuration();
		IConfiguration configuration = new AbstractConfigurationFacade(FACADE_FACTORY, cfg) {};
		exporterFacade.setConfiguration(configuration);
		Assert.assertEquals("setConfiguration", methodName);
		Assert.assertArrayEquals(new Object[] { cfg }, arguments);
	}
	
	@Test
	public void testSetProperties() {
		Properties properties = new Properties();
		exporterFacade.setProperties(properties);
		Assert.assertEquals("setProperties", methodName);
		Assert.assertArrayEquals(new Object[] { properties }, arguments);
	}
	
	@Test
	public void testSetArtifactCollector() {
		ArtifactCollector ac = new ArtifactCollector();
		IArtifactCollector artifactCollector = new AbstractArtifactCollectorFacade(FACADE_FACTORY, ac) {};
		exporterFacade.setArtifactCollector(artifactCollector);
		Assert.assertEquals("setArtifactCollector", methodName);
		Assert.assertArrayEquals(new Object[] { ac }, arguments);
	}
	
	@Test
	public void testSetOutputDirectory() {
		File file = new File("");
		exporterFacade.setOutputDirectory(file);
		Assert.assertEquals("setOutputDirectory", methodName);
		Assert.assertArrayEquals(new Object[] { file }, arguments);
	}
	
	@Test
	public void testSetTemplatePath() {
		String[] templatePath = new String[] {};
		exporterFacade.setTemplatePath(templatePath);
		Assert.assertEquals("setTemplatePath", methodName);
		Assert.assertArrayEquals(new Object[] { templatePath }, arguments);
	}
	
	@Test
	public void testStart() {
		exporterFacade.start();
		Assert.assertEquals("start", methodName);
		Assert.assertNull(arguments);
	}
	
	@Test
	public void testGetProperties() {
		Assert.assertNull(exporterFacade.getProperties());
		Assert.assertEquals("getProperties", methodName);
		Assert.assertNull(arguments);
	}
	
	@Test
	public void testGetGenericExporter() {
		IGenericExporter genericExporter = exporterFacade.getGenericExporter();
		Assert.assertNull(genericExporter);
		exporter = new GenericExporter();
		exporterFacade = new AbstractExporterFacade(FACADE_FACTORY, exporter) {};
		genericExporter = exporterFacade.getGenericExporter();
		Assert.assertNotNull(genericExporter);
		Assert.assertSame(exporter, ((IFacade)genericExporter).getTarget());
	}
	
	@Test
	public void testGetHbm2DDLExporter() {
		IHbm2DDLExporter hbm2DDLExporter = exporterFacade.getHbm2DDLExporter();
		Assert.assertNull(hbm2DDLExporter);
		exporter = new Hbm2DDLExporter();
		exporterFacade = new AbstractExporterFacade(FACADE_FACTORY, exporter) {};
		hbm2DDLExporter = exporterFacade.getHbm2DDLExporter();
		Assert.assertNotNull(hbm2DDLExporter);
		Assert.assertSame(exporter, ((IFacade)hbm2DDLExporter).getTarget());
	}
	
	@Test
	public void testGetQueryExporter() {
		IQueryExporter queryExporter = exporterFacade.getQueryExporter();
		Assert.assertNull(queryExporter);
		exporter = new QueryExporter();
		exporterFacade = new AbstractExporterFacade(FACADE_FACTORY, exporter) {};
		queryExporter = exporterFacade.getQueryExporter();
		Assert.assertNotNull(queryExporter);
		Assert.assertSame(exporter, ((IFacade)queryExporter).getTarget());
	}
	
	@Test
	public void testSetCustomProperties() {
		Properties properties = new Properties();
//		TODO: JBIDE-22579 - Implement this test the proper way when solving
//		try {
//			exporterFacade.setCustomProperties(properties);
//			Assert.fail();
//		} catch (RuntimeException e) {
//			Assert.assertTrue(e.getCause() instanceof NoSuchMethodException);
//		}
		exporter = new HibernateConfigurationExporter();
// 		TODO: JBIDE-22579 - Use AbstractExporterFacade again
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporter);
		exporterFacade.setCustomProperties(properties);
		Assert.assertSame(properties, ((HibernateConfigurationExporter)exporter).getCustomProperties());
	}
	
	@Test
	public void testSetOutput() {
		StringWriter stringWriter = new StringWriter();
//		TODO: JBIDE-22579 - Implement this test the proper way when solving
//		try {
//			exporterFacade.setOutput(stringWriter);
//			Assert.fail();
//		} catch (RuntimeException e) {
//			Assert.assertTrue(e.getCause() instanceof NoSuchMethodException);
//		}
		exporter = new HibernateConfigurationExporter();
// 		TODO: JBIDE-22579 - Use AbstractExporterFacade again
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporter);
		exporterFacade.setOutput(stringWriter);
		Assert.assertSame(stringWriter, ((HibernateConfigurationExporter)exporter).getOutput());
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
