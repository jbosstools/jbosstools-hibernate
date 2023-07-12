package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.wrp.EnvironmentWrapper;
import org.hibernate.tool.orm.jbt.wrp.HbmExporterWrapper;
import org.hibernate.tool.orm.jbt.wrp.HqlCodeAssistWrapper;
import org.hibernate.tool.orm.jbt.wrp.SchemaExportWrapper;
import org.hibernate.tool.orm.jbt.wrp.TypeFactoryWrapper;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NewFacadeFactoryTest {

	private NewFacadeFactory facadeFactory;

	@BeforeEach
	public void beforeEach() throws Exception {
		facadeFactory = NewFacadeFactory.INSTANCE;
	}
		
	@Test
	public void testCreateComponentValue() {
		IPersistentClass rootClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
		PersistentClass rootClass = (PersistentClass)((Wrapper)((IFacade)rootClassFacade).getTarget()).getWrappedObject();
		IValue componentFacade = facadeFactory.createComponent(rootClassFacade);
		Object componentWrapper = ((IFacade)componentFacade).getTarget();
		assertNotNull(componentWrapper);
		assertTrue(componentWrapper instanceof Wrapper);
		Object wrappedComponent = ((Wrapper)componentWrapper).getWrappedObject();
		assertTrue(wrappedComponent instanceof Component);
		assertSame(rootClass, ((Component)wrappedComponent).getOwner());
	}
	
	@Test
	public void testCreateTableFilter() {
		ITableFilter tableFilterFacade = facadeFactory.createTableFilter();
		Object tableFilterWrapper = ((IFacade)tableFilterFacade).getTarget();
		assertTrue(tableFilterWrapper instanceof TableFilter);
	}
	
	@Test
	public void testCreateTypeFactory() {
		ITypeFactory typeFactoryFacade = facadeFactory.createTypeFactory();
		assertSame(TypeFactoryWrapper.INSTANCE, ((IFacade)typeFactoryFacade).getTarget());
	}
	
	@Test
	public void testCreateEnvironment() {
		IEnvironment environmentFacade = facadeFactory.createEnvironment();
		assertNotNull(environmentFacade);
		Object environmentWrapper = ((IFacade)environmentFacade).getTarget();
		assertTrue(environmentWrapper instanceof EnvironmentWrapper);
	}
	
	@Test
	public void testCreateSchemaExport() {
		IConfiguration configurationFacade = (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createNativeConfigurationWrapper());
		ISchemaExport schemaExportFacade = facadeFactory.createSchemaExport(configurationFacade);
		Object schemaExportWrapper = ((IFacade)schemaExportFacade).getTarget();
		assertTrue(schemaExportWrapper instanceof SchemaExportWrapper);
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
