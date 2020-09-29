package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.ConfigurationFacadeImpl;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.FacadeFactoryImpl;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationMetadataDescriptorTest {
	
	private static final FacadeFactoryImpl FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ConfigurationMetadataDescriptor configurationMetadataDescriptor = null;
	
	private Configuration configurationTarget = null;
	private ConfigurationFacadeImpl configurationFacade = null;
	
	@Before
	public void before() {
		configurationTarget = new Configuration();
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configurationTarget);
		configurationMetadataDescriptor = new ConfigurationMetadataDescriptor(configurationFacade);
	}
	
	@Test
	public void testCreation() {
		assertNotNull(configurationMetadataDescriptor);
		assertSame(configurationFacade, configurationMetadataDescriptor.configurationFacade);
	}

}
