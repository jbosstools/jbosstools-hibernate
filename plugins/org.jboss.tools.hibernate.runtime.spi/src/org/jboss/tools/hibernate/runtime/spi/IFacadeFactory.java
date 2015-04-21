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
	IHibernateMappingGlobalSettings createHibernateMappingGlobalSettings(Object target);
	IMappings createMappings(Object target);
	IClassMetadata createClassMetadata(Object target);
	ICollectionMetadata createCollectionMetadata(Object target);
	IColumn createColumn(Object target);
	IConfiguration createConfiguration(Object target);
	ICriteria createCriteria(Object target);
	IDatabaseCollector createDatabaseCollector(Object target);
	IEntityMetamodel createEntityMetamodel(Object target);
	IEnvironment createEnvironment(Object target);
	IForeignKey createForeignKey(Object target);
	IHibernateMappingExporter createHibernateMappingExporter(Object target);
	IHQLCodeAssist createHQLCodeAssist(Object target);
	IHQLCompletionProposal createHQLCompletionProposal(Object target);
	IHQLQueryPlan createHQLQueryPlan(Object target);
	IJDBCReader createJDBCReader(Object target);
	IJoin createJoin(Object target);
	IMetaDataDialect createMetaDataDialect(Object target);
	IPersistentClass createPersistentClass(Object target);
	IPOJOClass createPOJOClass(Object target);
	IPrimaryKey createPrimaryKey(Object target);
	IProperty createProperty(Object target);
	IQuery createQuery(Object target);
	IQueryTranslator createQueryTranslator(Object target);
	ISessionFactory createSessionFactory(Object target);
	
}
