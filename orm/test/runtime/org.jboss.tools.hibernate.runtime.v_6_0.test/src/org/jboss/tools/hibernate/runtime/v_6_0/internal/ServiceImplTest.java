package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.export.java.JavaExporter;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.JdbcMetadataConfiguration;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.JpaConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ServiceImplTest {
	
	private ServiceImpl service = null;
	
	@Before
	public void before() {
		service = new ServiceImpl();
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
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
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
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
		ISchemaExport schemaExport = service.newSchemaExport(configuration);
		assertNotNull(schemaExport);
	}
	
	@Test
	public void testNewHQLCodeAssist() {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
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
	
	@Test
	public void testNewOverrideRepository() {
		IOverrideRepository overrideRepository = service.newOverrideRepository();
		assertNotNull(overrideRepository);
		Object target = ((IFacade)overrideRepository).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof OverrideRepository);
	}
	
	@Test
	public void testNewTableFilter() {
		ITableFilter tableFilter = service.newTableFilter();
		assertNotNull(tableFilter);
		Object target = ((IFacade)tableFilter).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof TableFilter);
	}
	
	@Test
	public void testNewReverseEngineeringSettings() {
		IReverseEngineeringStrategy strategy = 
				service.newDefaultReverseEngineeringStrategy();
		IReverseEngineeringSettings reverseEngineeringSettings = 
				service.newReverseEngineeringSettings(strategy);
		assertNotNull(reverseEngineeringSettings);
		Object target = ((IFacade)reverseEngineeringSettings).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof RevengSettings);
	}
	
	@Test
	public void testNewDefaultReverseEngineeringStrategy() throws Exception {
		IReverseEngineeringStrategy reverseEngineeringStrategy = 
				service.newDefaultReverseEngineeringStrategy();
		assertNotNull(reverseEngineeringStrategy);
		Object target = ((IFacade)reverseEngineeringStrategy).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof DefaultStrategy);
	}
	
	@Test
	public void testNewReverseEngineeringStrategy() throws Exception {
		IReverseEngineeringStrategy defaultStrategy = 
				service.newDefaultReverseEngineeringStrategy();
		IReverseEngineeringStrategy newStrategy = 
				service.newReverseEngineeringStrategy(
						DefaultStrategy.class.getName(), 
						defaultStrategy);
		Assert.assertNotNull(newStrategy);
		Object target = ((IFacade)newStrategy).getTarget();
		assertNotNull(target);
		assertFalse(target instanceof DelegatingStrategy);
		newStrategy = service.newReverseEngineeringStrategy(
				DelegatingStrategy.class.getName(), 
				defaultStrategy);
		Assert.assertNotNull(newStrategy);
		target = ((IFacade)newStrategy).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof DelegatingStrategy);
	}
	
	@Test
	public void testGetReverseEngineeringStrategyClassName() {
		assertEquals(
				RevengStrategy.class.getName(), 
				service.getReverseEngineeringStrategyClassName());
	}
	
	public static class TestDialect extends Dialect {
		@Override public int getVersion() { return 0; }	
	}
	
}
