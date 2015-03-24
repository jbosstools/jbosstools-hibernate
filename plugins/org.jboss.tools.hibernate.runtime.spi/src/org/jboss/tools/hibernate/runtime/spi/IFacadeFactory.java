package org.jboss.tools.hibernate.runtime.spi;

public interface IFacadeFactory {
	
	ClassLoader getClassLoader();
	IArtifactCollector createArtifactCollector();
	ICfg2HbmTool createCfg2HbmTool();
	INamingStrategy createNamingStrategy(Object target);
	IDialect createDialect(Object target);
	IMapping createMapping(Object target);
	IReverseEngineeringSettings createReverseEngineeringSettings(Object target);
	IReverseEngineeringStrategy createReverseEngineeringStrategy(Object target);
	IOverrideRepository createOverrideRepository(Object target);
	ISchemaExport createSchemaExport(Object target);
	IGenericExporter createGenericExporter(Object target);
	IHbm2DDLExporter createHbm2DDLExporter(Object target);
	IQueryExporter createQueryExporter(Object target);
	ITableFilter createTableFilter(Object target);
	IExporter createExporter(Object target);
	ITableIdentifier createTableIdentifier(Object target);
	
}
