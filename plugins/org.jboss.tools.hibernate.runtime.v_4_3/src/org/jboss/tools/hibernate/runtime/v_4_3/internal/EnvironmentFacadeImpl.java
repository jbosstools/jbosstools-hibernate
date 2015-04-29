package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.cfg.Environment;
import org.jboss.tools.hibernate.runtime.common.AbstractEnvironmentFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class EnvironmentFacadeImpl extends AbstractEnvironmentFacade {

	public EnvironmentFacadeImpl(
			IFacadeFactory facadeFactory) {
		super(facadeFactory, null);
	}

	@Override
	public String getTransactionManagerStrategy() {
		// Unsupported in 4.3 Environment
		return "hibernate.transaction.manager_lookup_class";
	}

	@Override
	public String getDefaultCatalog() {
		return Environment.DEFAULT_CATALOG;
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
