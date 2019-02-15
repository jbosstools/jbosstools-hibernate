package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;

public class Settings {

	public String getDefaultCatalogName() {
		return Environment.getProperties().getProperty(
				AvailableSettings.DEFAULT_CATALOG);
	}

	public String getDefaultSchemaName() {
		return Environment.getProperties().getProperty(
				AvailableSettings.DEFAULT_SCHEMA);
	}	

}
