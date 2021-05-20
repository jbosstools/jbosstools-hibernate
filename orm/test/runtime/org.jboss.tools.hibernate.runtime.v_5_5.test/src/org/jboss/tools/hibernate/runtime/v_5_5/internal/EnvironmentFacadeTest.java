package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnvironmentFacadeTest {

	private IEnvironment environmentFacade = null; 
	
	@BeforeEach
	public void beforeEach() {
		environmentFacade = new EnvironmentFacadeImpl(new FacadeFactoryImpl());		
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(environmentFacade);
	}
	
}
