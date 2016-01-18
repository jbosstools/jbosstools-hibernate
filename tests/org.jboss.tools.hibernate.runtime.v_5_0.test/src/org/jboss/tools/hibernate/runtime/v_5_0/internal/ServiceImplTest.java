package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
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
