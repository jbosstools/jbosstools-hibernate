package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.runtime.common.AbstractNamingStrategyFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class NamingStrategyProxy extends AbstractNamingStrategyFacade {
	
	private NamingStrategy target;

	public NamingStrategyProxy(
			IFacadeFactory facadeFactory, 
			NamingStrategy namingStrategy) {
		super(facadeFactory, namingStrategy);
		target = namingStrategy;
	}

	public NamingStrategy getTarget() {
		return target;
	}

	@Override
	public String collectionTableName(
			String ownerEntityName, 
			String name,
			String targetEntityName, 
			String name2, 
			String propName) {
		return getTarget().collectionTableName(
				ownerEntityName, 
				name, 
				targetEntityName, 
				name2, 
				propName);
	}

	@Override
	public String columnName(String name) {
		return getTarget().columnName(name);
	}

	@Override
	public String propertyToColumnName(String name) {
		return getTarget().propertyToColumnName(name);
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
