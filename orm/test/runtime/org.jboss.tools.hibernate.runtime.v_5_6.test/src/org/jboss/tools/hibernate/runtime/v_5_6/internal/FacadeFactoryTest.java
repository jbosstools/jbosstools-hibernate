package org.jboss.tools.hibernate.runtime.v_5_6.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FacadeFactoryTest {

	private FacadeFactoryImpl facadeFactory;

	@BeforeEach
	public void beforeEach() throws Exception {
		facadeFactory = new FacadeFactoryImpl();
	}
	
	@Test
	public void testFacadeFactoryCreation() {
		assertNotNull(facadeFactory);
	}
	
	@Test
	public void testGetClassLoader() {
		assertSame(
				FacadeFactoryImpl.class.getClassLoader(), 
				facadeFactory.getClassLoader());
	}
	
	@Test
	public void testCreateConfiguration() {
		Configuration configuration = new Configuration();
		IConfiguration facade = facadeFactory.createConfiguration(configuration);
		assertTrue(facade instanceof ConfigurationFacadeImpl);
		assertSame(configuration, ((IFacade)facade).getTarget());		
	}
	
}
