package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.runtime.common.AbstractNamingStrategyFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class NamingStrategyProxy extends AbstractNamingStrategyFacade {
	
	public NamingStrategyProxy(
			IFacadeFactory facadeFactory, 
			NamingStrategy namingStrategy) {
		super(facadeFactory, namingStrategy);
	}

	public NamingStrategy getTarget() {
		return (NamingStrategy)super.getTarget();
	}

	@Override
	public String tableName(String name) {
		return getTarget().tableName(name);
	}

	@Override
	public String joinKeyColumnName(String primaryKeyColumnName,
			String primaryTableName) {
		return getTarget().joinKeyColumnName(primaryKeyColumnName, primaryTableName);
	}

	@Override
	public String classToTableName(String defaultName) {
		return getTarget().classToTableName(defaultName);
	}

	@Override
	public String getStrategyClassName() {
		return getTarget().getClass().getName();
	}

}
