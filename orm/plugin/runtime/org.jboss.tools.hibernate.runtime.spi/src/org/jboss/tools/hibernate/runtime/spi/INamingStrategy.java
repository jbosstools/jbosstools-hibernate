package org.jboss.tools.hibernate.runtime.spi;

public interface INamingStrategy {

	String collectionTableName(
			String ownerEntity, 
			String ownerEntityTable, 
			String associatedEntity, 
			String associatedEntityTable,
			String propertyName);
	String columnName(String specifiedName);
	String propertyToColumnName(String buildDefaultName);
	String tableName(String specifiedTableName);
	String joinKeyColumnName(
			String primaryKeyColumnName,
			String primaryTableName);
	String classToTableName(String defaultName);
	String getStrategyClassName();

}
