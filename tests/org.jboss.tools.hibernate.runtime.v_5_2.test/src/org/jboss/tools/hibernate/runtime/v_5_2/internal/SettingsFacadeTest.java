package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.jboss.tools.hibernate.runtime.common.AbstractSettingsFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISettings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SettingsFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ISettings settingsFacade = null;
	
	@Before
	public void setUp() {
		settingsFacade = new AbstractSettingsFacade(FACADE_FACTORY, new Settings()) {};
	}
	
	@Test
	public void testGetDefaultSchemaName() {
		Assert.assertEquals(
				Environment.getProperties().getProperty(
						AvailableSettings.DEFAULT_SCHEMA), 
				settingsFacade.getDefaultSchemaName());
	}

}
