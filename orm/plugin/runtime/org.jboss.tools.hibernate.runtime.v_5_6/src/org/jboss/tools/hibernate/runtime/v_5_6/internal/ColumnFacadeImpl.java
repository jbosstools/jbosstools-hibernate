package org.jboss.tools.hibernate.runtime.v_5_6.internal;

import java.util.Properties;

import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.mapping.Column;
import org.jboss.tools.hibernate.runtime.common.AbstractColumnFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.v_5_6.internal.util.MetadataHelper;

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
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		ssrb.applySettings(properties);
		StandardServiceRegistry ssr = ssrb.build();
		DialectFactory df = ssr.getService(DialectFactory.class);
		Dialect dialectTarget = df.buildDialect(properties, null);
		return targetColumn.getSqlType(
				dialectTarget, 
				MetadataHelper.getMetadata(configurationTarget));
	}
	
}
