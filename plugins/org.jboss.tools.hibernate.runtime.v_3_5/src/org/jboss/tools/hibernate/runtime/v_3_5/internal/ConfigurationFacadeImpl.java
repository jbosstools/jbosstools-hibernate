package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.resolver.DialectFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class ConfigurationFacadeImpl extends AbstractConfigurationFacade {
	
	private IDialect dialect = null;
	
	public ConfigurationFacadeImpl(
			IFacadeFactory facadeFactory, 
			Configuration configuration) {
		super(facadeFactory, configuration);
	}
	
	public Configuration getTarget() {
		return (Configuration)super.getTarget();
	}


	@Override
	public IDialect getDialect() {
		if (dialect == null) {
			Object d = buildTargetDialect();
			if (d != null) {
				dialect = getFacadeFactory().createDialect(d);
			}
		}
		return dialect;
	}

	protected Object buildTargetDialect() {
		return DialectFactory.buildDialect(getProperties());
	}
	
}
