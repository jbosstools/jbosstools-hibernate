package org.jboss.tools.hibernate.runtime.v_6_1.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.internal.MetadataImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.mapping.Column;
import org.hibernate.type.spi.TypeConfiguration;
import org.jboss.tools.hibernate.runtime.common.AbstractColumnFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;

public class ColumnFacadeImpl extends AbstractColumnFacade {

	final static int DEFAULT_LENGTH = 255;

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
		Dialect dialectTarget = df.buildDialect(transform(properties), null);
		Metadata metadata = ((ConfigurationFacadeImpl)configuration).getMetadata();
		TypeConfiguration tc = ((MetadataImpl)metadata).getTypeConfiguration();
		return targetColumn.getSqlType(
				tc,
				dialectTarget, 
				metadata);
	}
	
	@Override
	public int getLength() {
		Long length = ((Column)getTarget()).getLength();
		if (length == null) {
			return Integer.MIN_VALUE;
		}
		return length.intValue();
	}
	
	@Override
	public int getDefaultLength() {
		return DEFAULT_LENGTH;
	}
	
	private Map<String, Object> transform(Properties properties) {
		Map<String, Object> result = new HashMap<String, Object>(properties.size());
		for (Object key : properties.keySet()) {
			result.put((String)key, properties.get(key));
		}
		return result;
	}

}
