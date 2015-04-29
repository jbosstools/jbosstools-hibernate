package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.cfg.Environment;
import org.jboss.tools.hibernate.runtime.common.AbstractEnvironmentFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class EnvironmentFacadeImpl extends AbstractEnvironmentFacade {

	public EnvironmentFacadeImpl(
			IFacadeFactory facadeFactory) {
		super(facadeFactory, null);
	}

	@Override
	public String getDefaultSchema() {
		return Environment.DEFAULT_SCHEMA;
	}

	@Override
	public Class<?> getWrappedClass() {
		return Environment.class;
	}

}
