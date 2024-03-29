package org.jboss.tools.hibernate.runtime.spi;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.xml.sax.EntityResolver;

public interface IService {

	IConfiguration newAnnotationConfiguration();

	IConfiguration newJpaConfiguration(
			String entityResolver,
			String persistenceUnit, 
			Map<Object, Object> overrides);
	
	IConfiguration newDefaultConfiguration();
	
	IHibernateMappingExporter newHibernateMappingExporter(
			IConfiguration hcfg, 
			File file);
	
	ISchemaExport newSchemaExport(
			IConfiguration hcfg);
	
	IHQLCodeAssist newHQLCodeAssist(
			IConfiguration hcfg);

	IConfiguration newJDBCMetaDataConfiguration();
	
	IExporter createCfgExporter();
	
	IExporter createExporter(
			String exporterClassName);
	
	IArtifactCollector newArtifactCollector();
	
	IHQLQueryPlan newHQLQueryPlan(
			String query, 
			boolean shallow, 
			ISessionFactory sessionFactory);
	
	ITypeFactory newTypeFactory();
	
	INamingStrategy newNamingStrategy(String strategyClassName);
	
	IOverrideRepository newOverrideRepository();

	ITableFilter newTableFilter();

	IReverseEngineeringSettings newReverseEngineeringSettings(
			IReverseEngineeringStrategy res);

	IReverseEngineeringStrategy newDefaultReverseEngineeringStrategy();

	Map<String, List<ITable>> collectDatabaseTables(
			Properties properties, 
			IReverseEngineeringStrategy strategy, 
			IProgressListener progressListener);

	IReverseEngineeringStrategy newReverseEngineeringStrategy(
			String strategyName, 
			IReverseEngineeringStrategy delegate);

	String getReverseEngineeringStrategyClassName();

	ICfg2HbmTool newCfg2HbmTool();
	
	IProperty newProperty();
	
	ITable newTable(String name);

	IColumn newColumn(String string);
	
	String newDialect(Properties properties, Connection connection);
	
	Class<?> getDriverManagerConnectionProviderClass();

	IEnvironment getEnvironment();

	IValue newSimpleValue();

	IValue newPrimitiveArray(IPersistentClass persistentClass);

	IValue newArray(IPersistentClass persistentClass);

	IValue newBag(IPersistentClass persistentClass);

	IValue newList(IPersistentClass persistentClass);

	IValue newMap(IPersistentClass persistentClass);

	IValue newSet(IPersistentClass persistentClass);

	IValue newManyToOne(ITable table);

	IValue newOneToMany(IPersistentClass persistentClass);

	IValue newOneToOne(IPersistentClass persistentClass);

	IPersistentClass newSingleTableSubclass(IPersistentClass persistentClass);

	IPersistentClass newJoinedSubclass(IPersistentClass persistentClass);

	IPersistentClass newSpecialRootClass(IProperty ormElement);

	IPersistentClass newRootClass();

	boolean isInitialized(Object object);

	List<String> getJPAMappingFilePaths(String persistenceUnitName,
			EntityResolver entityResolver);

	Class<?> getClassWithoutInitializingProxy(Object reflectedObject);
	
	ClassLoader getClassLoader();
	
}
