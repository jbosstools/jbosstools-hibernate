package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import java.sql.SQLException;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.service.ServiceRegistry;
import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

public class ConfigurationFacadeImpl extends AbstractConfigurationFacade {
	
	ServiceRegistry serviceRegistry = null;

	public ConfigurationFacadeImpl(
			IFacadeFactory facadeFactory,
			Configuration configuration) {
		super(facadeFactory, configuration);
	}
	
	protected Object buildTargetSessionFactory() {
		if (serviceRegistry == null) {
			buildServiceRegistry();
		}
		if (serviceRegistry.getService(org.hibernate.engine.jdbc.spi.JdbcServices.class).getDialect() == null) {
			throw new GenericJDBCException("Could not open connection", new SQLException());
		}
		return ((Configuration)getTarget()).buildSessionFactory(serviceRegistry);
	}

	protected Object buildTargetSettings() {
		if (serviceRegistry == null) {
			buildServiceRegistry();
		}
		return ((Configuration)getTarget()).buildSettings(serviceRegistry);
	}
	
	protected Object buildTargetDialect() {
		if (serviceRegistry == null) {
			buildServiceRegistry();
		}
		return serviceRegistry.getService(DialectFactory.class).buildDialect(
				getProperties(), null);
	}
	
	void buildServiceRegistry() {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(getProperties());
		serviceRegistry = builder.build();		
	}

}
