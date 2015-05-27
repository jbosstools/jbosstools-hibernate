package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.Settings;
import org.hibernate.connection.ConnectionProvider;
import org.jboss.tools.hibernate.runtime.common.AbstractSettingsFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class SettingsProxy extends AbstractSettingsFacade {
	
	public SettingsProxy(
			IFacadeFactory facadeFactory, 
			Settings settings) {
		super(facadeFactory, settings);
	}

	public Settings getTarget() {
		return (Settings)super.getTarget();
	}

	/*
	 * @deprecated This method is not supported anymore in recent Hibernate versions
	 */
	@Deprecated
	public ConnectionProvider getConnectionProvider() {
		return getTarget().getConnectionProvider();
	}

}
