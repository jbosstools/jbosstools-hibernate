package org.jboss.tools.hibernate.runtime.v_5_5.internal.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;

import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.runtime.v_5_5.internal.ConfigurationFacadeImpl;
import org.jboss.tools.hibernate.runtime.v_5_5.internal.FacadeFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigurationMetadataDescriptorTest {

	private static final FacadeFactoryImpl FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ConfigurationMetadataDescriptor configurationMetadataDescriptor = null;
	
	private Configuration configurationTarget = null;
	private ConfigurationFacadeImpl configurationFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		configurationTarget = new Configuration();
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configurationTarget);
		configurationMetadataDescriptor = new ConfigurationMetadataDescriptor(configurationFacade);
	}
	
	@Test
	public void testConstruction() throws Exception {
		Field configurationFacadeField = 
				ConfigurationMetadataDescriptor.class.getDeclaredField("configurationFacade");
		configurationFacadeField.setAccessible(true);
		assertNotNull(configurationMetadataDescriptor);
		assertSame(configurationFacade, configurationFacadeField.get(configurationMetadataDescriptor));
	}
	
}
