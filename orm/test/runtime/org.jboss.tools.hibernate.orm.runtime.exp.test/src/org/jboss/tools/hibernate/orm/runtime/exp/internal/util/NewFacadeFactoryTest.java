package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.orm.jbt.wrp.HbmExporterWrapper;
import org.hibernate.tool.orm.jbt.wrp.HqlCodeAssistWrapper;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NewFacadeFactoryTest {

	private NewFacadeFactory facadeFactory;

	@BeforeEach
	public void beforeEach() throws Exception {
		facadeFactory = NewFacadeFactory.INSTANCE;
	}
		
	@Test
	public void testCreateHibernateMappingExporter() {
		File file = new File("foo");
		IConfiguration configurationFacade = (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createNativeConfigurationWrapper());
		IHibernateMappingExporter hibernateMappingExporterFacade = 
				facadeFactory.createHibernateMappingExporter(configurationFacade, file);
		Object hibernateMappingExporterWrapper = ((IFacade)hibernateMappingExporterFacade).getTarget();
		assertTrue(hibernateMappingExporterWrapper instanceof HbmExporterWrapper);
		assertSame(
				((HbmExporterWrapper)hibernateMappingExporterWrapper)
					.getProperties().get(ExporterConstants.OUTPUT_FILE_NAME),
				file);
	}
	
	@Test
	public void testCreateExporter() {
		IExporter exporterFacade = facadeFactory.createExporter(GenericExporter.class.getName());
		assertNotNull(exporterFacade);
		Object exporterWrapper = ((IFacade)exporterFacade).getTarget();
		assertNotNull(exporterWrapper);
		Exporter wrappedExporter = (Exporter)((Wrapper)exporterWrapper).getWrappedObject();
		assertNotNull(wrappedExporter);
		assertTrue(wrappedExporter instanceof GenericExporter);
	}
	
	@Test
	public void testCreateHqlCodeAssist() {
		IConfiguration configuration = (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createNativeConfigurationWrapper());
		configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		IHQLCodeAssist hqlCodeAssistFacade = facadeFactory.createHQLCodeAssist(configuration);
		assertNotNull(hqlCodeAssistFacade);
		Object hqlCodeAssistWrapper = ((IFacade)hqlCodeAssistFacade).getTarget();
		assertNotNull(hqlCodeAssistWrapper);
		assertTrue(hqlCodeAssistWrapper instanceof HqlCodeAssistWrapper);
	}
	
	public static class TestRevengStrategy extends DelegatingStrategy {
		public TestRevengStrategy(RevengStrategy delegate) {
			super(delegate);
		}
	}
	
}
