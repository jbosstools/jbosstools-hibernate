package org.jboss.tools.hibernate.orm.runtime.v_7_0;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
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

}
