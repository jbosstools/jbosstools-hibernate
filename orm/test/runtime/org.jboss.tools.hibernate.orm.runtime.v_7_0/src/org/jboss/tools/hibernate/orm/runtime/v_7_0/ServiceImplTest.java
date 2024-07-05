package org.jboss.tools.hibernate.orm.runtime.v_7_0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.export.java.JavaExporter;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.internal.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.internal.util.MetadataHelper;
import org.hibernate.tool.orm.jbt.internal.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.internal.util.MockDialect;
import org.hibernate.tool.orm.jbt.internal.util.RevengConfiguration;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServiceImplTest {

	private ServiceImpl service = null;
	
	@BeforeEach
	public void beforeEach() {
		service = new ServiceImpl();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(service);
	}
	
	@Test
	public void testNewDefaultConfiguration() {
		IConfiguration defaultConfiguration = service.newDefaultConfiguration();
		assertNotNull(defaultConfiguration);
		Object target = ((IFacade)defaultConfiguration).getTarget();
		assertTrue(target instanceof Wrapper);
		target = ((Wrapper)target).getWrappedObject();
		assertTrue( target instanceof Configuration);
	}

	@Test
	public void testNewAnnotationConfiguration() {
		IConfiguration annotationConfiguration = service.newAnnotationConfiguration();
		assertNotNull(annotationConfiguration);
		Object target = ((IFacade)annotationConfiguration).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		target = ((Wrapper)target).getWrappedObject();
		assertNotNull(target);
		assertTrue(target instanceof Configuration);
	}

	@Test
	public void testNewJpaConfiguration() {
		IConfiguration jpaConfiguration = service.newJpaConfiguration(null, "test", null);
		assertNotNull(jpaConfiguration);
		Object wrapper = ((IFacade)jpaConfiguration).getTarget();
		assertNotNull(wrapper);
		assertTrue(wrapper instanceof Wrapper);
		Object target = ((Wrapper)wrapper).getWrappedObject();
		assertTrue(target instanceof JpaConfiguration);
		assertEquals("test", ((JpaConfiguration)target).getPersistenceUnit());
		
	}
	
	@Test
	public void testNewHibernateMappingExporter() {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		File file = new File("");
		IHibernateMappingExporter hibernateMappingExporter = 
				service.newHibernateMappingExporter(configuration, file);
		HbmExporter hmee = 
				(HbmExporter)((Wrapper)((IFacade)hibernateMappingExporter).getTarget()).getWrappedObject();
		assertSame(file, hmee.getProperties().get(ExporterConstants.OUTPUT_FILE_NAME));
		assertSame(
				MetadataHelper.getMetadata((Configuration)((Wrapper)((IFacade)configuration).getTarget()).getWrappedObject()),
				hmee.getMetadata());
	}
	
	@Test
	public void testNewSchemaExport() {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		ISchemaExport schemaExport = service.newSchemaExport(configuration);
		assertNotNull(schemaExport);
	}
	
	@Test
	public void testNewHQLCodeAssist() {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		IHQLCodeAssist hqlCodeAssist = service.newHQLCodeAssist(configuration);
		assertNotNull(hqlCodeAssist);
	}
	
	@Test
	public void testNewJDBCMetaDataConfiguration() {
		IConfiguration configuration = service.newJDBCMetaDataConfiguration();
		assertNotNull(configuration);
		Object target = ((IFacade)configuration).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		target = ((Wrapper)target).getWrappedObject();
		assertTrue(target instanceof RevengConfiguration);
	}
	
	@Test
	public void testCreateExporter() {
		IExporter exporter = service.createExporter(JavaExporter.class.getName());
		assertNotNull(exporter);
		Object exporterWrapper = ((IFacade)exporter).getTarget();
		assertNotNull(exporterWrapper);
		Exporter wrappedExporter = (Exporter)((Wrapper)exporterWrapper).getWrappedObject();
		assertTrue(wrappedExporter instanceof JavaExporter);
		MetadataDescriptor metadataDescriptor = 
				(MetadataDescriptor)((JavaExporter)wrappedExporter)
					.getProperties()
					.get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor.getProperties()); // Normal metadata descriptor
		exporter = service.createExporter(CfgExporter.class.getName());
		assertNotNull(exporter);
		exporterWrapper = ((IFacade)exporter).getTarget();
		assertNotNull(exporterWrapper);
		wrappedExporter = (Exporter)((Wrapper)exporterWrapper).getWrappedObject();
		assertTrue(wrappedExporter instanceof CfgExporter);
		metadataDescriptor = 
				(MetadataDescriptor)((CfgExporter)wrappedExporter)
					.getProperties()
					.get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNull(metadataDescriptor.getProperties()); // Dummy metadata descriptor
	}
	
}
