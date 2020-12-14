package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertSame;

import org.hibernate.cfg.Environment;
import org.jboss.tools.hibernate.runtime.common.AbstractEnvironmentFacade;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.junit.Before;
import org.junit.Test;

public class EnvironmentFacadeTest {
	
	private IEnvironment environmentFacade = null; 
	
	@Before
	public void before() {
		environmentFacade = new AbstractEnvironmentFacade(new FacadeFactoryImpl(), null) {};		
	}
	
	@Test
	public void testGetDefaultSchema() {
		assertSame(Environment.DEFAULT_SCHEMA, environmentFacade.getDefaultSchema());
	}
	
	@Test
	public void testGetWrappedClass() {
		assertSame(Environment.class, environmentFacade.getWrappedClass());
	}
	
}
