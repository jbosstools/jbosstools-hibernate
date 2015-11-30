package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.jdbc.dialect.spi.DialectFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

public class ConfigurationFacadeImpl extends AbstractConfigurationFacade {
	
	public ConfigurationFacadeImpl(
			IFacadeFactory facadeFactory, 
			Configuration configuration) {
		super(facadeFactory, configuration);
	}
	
	protected Object buildTargetSessionFactory() {
		return ((Configuration)getTarget()).buildSessionFactory(buildServiceRegistry());
	}

	protected Object buildTargetSettings() {
		return ((Configuration)getTarget()).buildSettings(buildServiceRegistry());
	}
	
	protected Object buildTargetDialect() {
		return buildServiceRegistry()
				.getService(DialectFactory.class)
				.buildDialect(getProperties(), null);
	}
	
	private ServiceRegistry buildServiceRegistry() {
		ServiceRegistryBuilder builder = new ServiceRegistryBuilder();
		builder.applySettings(((Configuration)getTarget()).getProperties());
		return builder.buildServiceRegistry();		
	}

}
