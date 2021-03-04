package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.junit.Before;
import org.junit.Test;

public class ServiceImplTest {
	
	private ServiceImpl service = null;
	
	@Before
	public void before() {
		service = new ServiceImpl();
	}
	
	@Test
	public void testNewDefaultConfiguration() {
		IConfiguration defaultConfiguration = service.newDefaultConfiguration();
		assertNotNull(defaultConfiguration);
		assertTrue(((IFacade)defaultConfiguration).getTarget() instanceof Configuration);
	}

}
