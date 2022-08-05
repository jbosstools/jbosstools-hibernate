package org.jboss.tools.hibernate.runtime.v_6_1.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.export.java.JavaExporter;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.JdbcMetadataConfiguration;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.JpaConfiguration;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.MockConnectionProvider;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.MockDialect;
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
	public void testNewAnnotationConfiguration() {
		IConfiguration annotationConfiguration = service.newAnnotationConfiguration();
		assertNotNull(annotationConfiguration);
		assertTrue(((IFacade)annotationConfiguration).getTarget() instanceof Configuration);
	}

	@Test
	public void testNewJpaConfiguration() {
		IConfiguration jpaConfiguration = service.newJpaConfiguration(null, "test", null);
		assertNotNull(jpaConfiguration);
		Object target = ((IFacade)jpaConfiguration).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof JpaConfiguration);
		assertEquals("test", ((JpaConfiguration)target).getPersistenceUnit());
		
	}
	
	@Test
	public void testNewDefaultConfiguration() {
		IConfiguration defaultConfiguration = service.newDefaultConfiguration();
		assertNotNull(defaultConfiguration);
		assertTrue(((IFacade)defaultConfiguration).getTarget() instanceof Configuration);
	}

	@Test
	public void testNewHibernateMappingExporter() {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		File file = new File("");
		IHibernateMappingExporter hibernateMappingExporter = 
				service.newHibernateMappingExporter(configuration, file);
		HibernateMappingExporterExtension hmee = 
				(HibernateMappingExporterExtension)((IFacade)hibernateMappingExporter).getTarget();
		assertSame(file, hmee.getProperties().get(ExporterConstants.OUTPUT_FILE_NAME));
		assertSame(
				((ConfigurationFacadeImpl)configuration).getMetadata(), 
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
		assertTrue(target instanceof JdbcMetadataConfiguration);
	}
	
	@Test
	public void testCreateExporter() {
		IExporter exporter = service.createExporter(JavaExporter.class.getName());
		assertNotNull(exporter);
		Object target = ((IFacade)exporter).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof JavaExporter);
		MetadataDescriptor metadataDescriptor = 
				(MetadataDescriptor)((JavaExporter)target)
					.getProperties()
					.get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor.getProperties()); // Normal metadata descriptor
		exporter = service.createExporter(CfgExporter.class.getName());
		assertNotNull(exporter);
		target = ((IFacade)exporter).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof CfgExporter);
		metadataDescriptor = 
				(MetadataDescriptor)((CfgExporter)target)
					.getProperties()
					.get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNull(metadataDescriptor.getProperties()); // Dummy metadata descriptor
	}
	
	@Test
	public void testNewArtifactCollector() {
		IArtifactCollector artifactCollector = service.newArtifactCollector();
		assertNotNull(artifactCollector);
		Object target = ((IFacade)artifactCollector).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof ArtifactCollector);
	}
	
	@Test 
	public void testNewTypeFactory() {
		ITypeFactory typeFactory = service.newTypeFactory();
		assertNotNull(typeFactory);
	}
	
	@Test
	public void testNewNamingStrategy() {
		String strategyClassName = DefaultNamingStrategy.class.getName();
		INamingStrategy namingStrategy = service.newNamingStrategy(strategyClassName);
		assertNotNull(namingStrategy);
		Object target = ((IFacade)namingStrategy).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof DefaultNamingStrategy);
		assertNull(service.newNamingStrategy("some unexistant class"));
	}
	
}
