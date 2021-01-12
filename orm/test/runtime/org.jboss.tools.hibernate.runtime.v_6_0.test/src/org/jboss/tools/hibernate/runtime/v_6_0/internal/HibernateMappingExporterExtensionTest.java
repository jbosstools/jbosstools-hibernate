package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertNotNull;

import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.junit.Before;
import org.junit.Test;

public class HibernateMappingExporterExtensionTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private HibernateMappingExporterExtension hibernateMappingExporterExtension;

	@Before
	public void setUp() throws Exception {
		hibernateMappingExporterExtension = new HibernateMappingExporterExtension(FACADE_FACTORY, null, null);
	}
	
	@Test
	public void testConstructor() {
		assertNotNull(hibernateMappingExporterExtension);
	}
	
}	
