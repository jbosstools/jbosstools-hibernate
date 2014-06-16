package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.spi.INamingStrategy;

public class NamingStrategyProxy implements INamingStrategy {
	
	private NamingStrategy target;

	public NamingStrategyProxy(NamingStrategy namingStrategy) {
		target = namingStrategy;
	}

	NamingStrategy getTarget() {
		return target;
	}

	@Override
	public String collectionTableName(
			String ownerEntityName, 
			String name,
			String targetEntityName, 
			String name2, 
			String propName) {
		return target.collectionTableName(
				ownerEntityName, 
				name, 
				targetEntityName, 
				name2, 
				propName);
	}

	@Override
	public String columnName(String name) {
		return target.columnName(name);
	}

	@Override
	public String propertyToColumnName(String name) {
		return target.propertyToColumnName(name);
	}

	@Override
	public String tableName(String name) {
		return target.tableName(name);
	}

	@Override
	public String joinKeyColumnName(String primaryKeyColumnName,
			String primaryTableName) {
		return target.joinKeyColumnName(primaryKeyColumnName, primaryTableName);
	}

	@Override
	public String classToTableName(String defaultName) {
		return target.classToTableName(defaultName);
	}

}
