package org.jboss.tools.hibernate.orm.runtime.exp.internal;

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
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.hibernate.type.spi.TypeConfiguration;
import org.jboss.tools.hibernate.runtime.common.AbstractColumnFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class ColumnFacadeImpl extends AbstractColumnFacade {

	final static int DEFAULT_LENGTH = 255;
	final static int DEFAULT_PRECISION = 19;
	final static int DEFAULT_SCALE = 2;

	IValue value = null;

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
		Metadata metadata = MetadataHelper.getMetadata(configurationTarget);
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
	
	@Override
	public int getPrecision() {
		Integer precision = ((Column)getTarget()).getPrecision();
		if (precision == null) {
			return Integer.MIN_VALUE;
		}
		return precision.intValue();
	}
	
	@Override
	public int getDefaultPrecision() {
		return DEFAULT_PRECISION;
	}
	
	@Override
	public int getScale() {
		Integer scale = ((Column)getTarget()).getScale();
		if (scale == null) {
			return Integer.MIN_VALUE;
		}
		return scale.intValue();
	}
	
	@Override
	public int getDefaultScale() {
		return DEFAULT_SCALE;
	}
	
	@Override 
	public IValue getValue() {
		if (value == null) {
			Value targetValue = ((Column)getTarget()).getValue();
			if (targetValue != null) {
				value = super.getValue();
			}
		}
		return value;
	}
	
	private Map<String, Object> transform(Properties properties) {
		Map<String, Object> result = new HashMap<String, Object>(properties.size());
		for (Object key : properties.keySet()) {
			result.put((String)key, properties.get(key));
		}
		return result;
	}

}
