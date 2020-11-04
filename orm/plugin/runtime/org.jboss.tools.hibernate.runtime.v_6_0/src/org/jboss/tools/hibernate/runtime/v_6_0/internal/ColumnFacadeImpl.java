package org.jboss.tools.hibernate.runtime.v_6_0.internal;

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

public class ColumnFacadeImpl extends AbstractColumnFacade {
	
	final static int DEFAULT_LENGTH = 255;
	final static int DEFAULT_PRECISION = 19;
	static final int DEFAULT_SCALE = 2;

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
				((ConfigurationFacadeImpl)configuration).getMetadata());
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
	
}
