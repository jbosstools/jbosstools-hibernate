package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Column;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.jdbc.dialect.spi.DialectFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractColumnFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;

public class ColumnFacadeImpl extends AbstractColumnFacade {

	public ColumnFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String getSqlType(IConfiguration configuration) {
		Column targetColumn = (Column)getTarget();
		Configuration configurationTarget = 
				(Configuration)((IFacade)configuration).getTarget();
		Properties properties = configurationTarget.getProperties();
		ServiceRegistryBuilder ssrb = new ServiceRegistryBuilder();
		ssrb.applySettings(properties);
		ServiceRegistry ssr = ssrb.buildServiceRegistry();
		DialectFactory df = ssr.getService(DialectFactory.class);
		Dialect dialectTarget = df.buildDialect(properties, null);
		return targetColumn.getSqlType(
				dialectTarget, 
				configurationTarget.buildMapping());
	}
	
}
