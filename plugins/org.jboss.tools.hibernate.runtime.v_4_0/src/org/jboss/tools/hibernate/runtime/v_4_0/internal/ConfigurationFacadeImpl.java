package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.jdbc.dialect.spi.DialectFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class ConfigurationFacadeImpl extends AbstractConfigurationFacade {
	
	private ServiceRegistry serviceRegistry = null;
	
	public ConfigurationFacadeImpl(
			IFacadeFactory facadeFactory, 
			Configuration configuration) {
		super(facadeFactory, configuration);
	}
	
	public Configuration getTarget() {
		return (Configuration)super.getTarget();
	}

	protected Object buildTargetSessionFactory() {
		if (serviceRegistry == null) {
			buildServiceRegistry();
		}
		return getTarget().buildSessionFactory(serviceRegistry);
	}

	protected Object buildTargetSettings() {
		if (serviceRegistry == null) {
			buildServiceRegistry();
		}
		return getTarget().buildSettings(serviceRegistry);
	}
	
	@Override
	public IDialect getDialect() {
		if (dialect != null) {
			Object d = buildTargetDialect();
			if (d != null) {
				dialect = getFacadeFactory().createDialect(d);
			}
		}
		return dialect;
	}
	
	protected Object buildTargetDialect() {
		if (serviceRegistry == null) {
			buildServiceRegistry();
		}
		return serviceRegistry.getService(DialectFactory.class).buildDialect(
				getProperties(), null);
	}
	
	private void buildServiceRegistry() {
		ServiceRegistryBuilder builder = new ServiceRegistryBuilder();
		builder.applySettings(getTarget().getProperties());
		serviceRegistry = builder.buildServiceRegistry();		
	}

}
