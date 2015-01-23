package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.Settings;
import org.hibernate.connection.ConnectionProvider;
import org.jboss.tools.hibernate.runtime.spi.ISettings;

public class SettingsProxy implements ISettings {
	
	private Settings target;

	public SettingsProxy(Settings settings) {
		target = settings;
	}

	public Settings getTarget() {
		return target;
	}

	@Override
	public String getDefaultCatalogName() {
		return target.getDefaultCatalogName();
	}

	@Override
	public String getDefaultSchemaName() {
		return target.getDefaultSchemaName();
	}
	
	/*
	 * @deprecated This method is not supported anymore in recent Hibernate versions
	 */
	@Deprecated
	public ConnectionProvider getConnectionProvider() {
		return target.getConnectionProvider();
	}

}
