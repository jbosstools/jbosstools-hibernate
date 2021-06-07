package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.v_5_5.internal.util.JpaConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServiceImplTest {

	private ServiceImpl service = null;
	
	@BeforeEach
	public void beforeEach() {
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

}
