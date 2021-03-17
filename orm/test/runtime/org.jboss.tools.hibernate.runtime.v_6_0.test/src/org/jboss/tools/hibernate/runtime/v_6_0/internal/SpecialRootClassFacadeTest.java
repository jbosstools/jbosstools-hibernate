package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.junit.Before;
import org.junit.Test;

public class SpecialRootClassFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private SpecialRootClassFacadeImpl specialRootClassFacade = null;
	
	@Before
	public void before() {
		specialRootClassFacade = new SpecialRootClassFacadeImpl(
				FACADE_FACTORY, 
				FACADE_FACTORY.createProperty(null));
	}
	
	@Test
	public void testIsInstanceOfSpecialRootClass() {
		assertTrue(specialRootClassFacade.isInstanceOfSpecialRootClass());
		assertFalse(specialRootClassFacade.isInstanceOfSubclass());
	}
	

}
