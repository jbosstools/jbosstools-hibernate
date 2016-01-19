package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import java.io.File;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.junit.Assert;
import org.junit.Test;

public class ServiceImplTest {
	
	private ServiceImpl service = new ServiceImpl();
	
	@Test
	public void testServiceCreation() {
		Assert.assertNotNull(service);
	}
	
	@Test
	public void testNewAnnotationConfiguration() {
		IConfiguration configuration = service.newAnnotationConfiguration();
		Assert.assertNotNull(configuration);
		Object target = ((IFacade)configuration).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Configuration);
	}
	
	@Test
	public void testNewJpaConfiguration() {
		IConfiguration configuration = service.newJpaConfiguration(null, "test", null);
		Assert.assertNotNull(configuration);
		Object target = ((IFacade)configuration).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Configuration);
	}
	
	@Test
	public void testNewDefaultConfiguration() {
		IConfiguration configuration = service.newDefaultConfiguration();
		Assert.assertNotNull(configuration);
		Object target = ((IFacade)configuration).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Configuration);
	}
	
	@Test
	public void testNewHibernateMappingExporter() {
		IConfiguration configuration = service.newDefaultConfiguration();
		File file = new File("");
		IHibernateMappingExporter hibernateMappingExporter = 
				service.newHibernateMappingExporter(configuration, file);
		Configuration cfg = (Configuration)((IFacade)configuration).getTarget();
		HibernateMappingExporterExtension hmee = 
				(HibernateMappingExporterExtension)((IFacade)hibernateMappingExporter).getTarget();
		Assert.assertSame(file, hmee.getOutputDirectory());
		Assert.assertSame(cfg, hmee.getConfiguration());
	}
	
	@Test
	public void testNewSchemaExport() {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		ISchemaExport schemaExport = service.newSchemaExport(configuration);
		Assert.assertNotNull(schemaExport);
	}
	
	@Test
	public void testGetJPAMappingFilePaths() {
		List<String> result = service.getJPAMappingFilePaths("test", null);
		Assert.assertEquals(0, result.size());
	}
	
	@Test
	public void testGetClassWithoutInitializingProxy() {
		Assert.assertSame(
				Object.class, 
				service.getClassWithoutInitializingProxy(new Object()));
	}
	
	@Test
	public void testGetClassLoader(){
		Assert.assertSame(
				ServiceImpl.class.getClassLoader(), 
				service.getClassLoader());
	}
	
}
