package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private ConfigurationFacadeImpl configurationFacade = null;
	private Configuration configuration = null;

	@Before
	public void setUp() throws Exception {
		configuration = new Configuration();
		configurationFacade = new ConfigurationFacadeImpl(
				FACADE_FACTORY, 
				configuration);
	}
	
	@Test
	public void testGetProperty() {
		configuration.setProperty("foo", "bar");
		String foo = configurationFacade.getProperty("foo");
		Assert.assertEquals("bar", foo);
	}

}
