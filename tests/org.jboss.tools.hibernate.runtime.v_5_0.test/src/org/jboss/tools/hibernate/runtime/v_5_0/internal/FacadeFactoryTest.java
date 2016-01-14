package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import org.jboss.tools.hibernate.runtime.v_5_0.internal.FacadeFactoryImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FacadeFactoryTest {

	private FacadeFactoryImpl facadeFactory;

	@Before
	public void setUp() throws Exception {
		facadeFactory = new FacadeFactoryImpl();
	}
	
	@Test
	public void testFacadeFactoryCreation() {
		Assert.assertNotNull(facadeFactory);
	}
	
}
