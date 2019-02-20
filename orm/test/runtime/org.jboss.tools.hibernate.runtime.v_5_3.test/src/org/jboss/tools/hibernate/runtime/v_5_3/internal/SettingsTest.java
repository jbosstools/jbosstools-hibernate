package org.jboss.tools.hibernate.runtime.v_5_3.internal;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.junit.Assert;
import org.junit.Test;

public class SettingsTest {

	private Settings settings = new Settings();
	
	@Test
	public void testGetDefaultCatalogName() {
		Assert.assertEquals(
				Environment.getProperties().getProperty(
						AvailableSettings.DEFAULT_CATALOG), 
				settings.getDefaultCatalogName());
	}

	@Test
	public void testGetDefaultSchemaName() {
		Assert.assertEquals(
				Environment.getProperties().getProperty(
						AvailableSettings.DEFAULT_SCHEMA), 
				settings.getDefaultSchemaName());
	}

}
