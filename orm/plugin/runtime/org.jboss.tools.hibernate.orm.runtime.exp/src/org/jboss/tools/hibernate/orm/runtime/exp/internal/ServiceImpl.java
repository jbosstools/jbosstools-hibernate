package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.Hibernate;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfoSource;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.orm.jbt.util.JpaMappingFileHelper;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IDatabaseReader;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.HibernateException;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProgressListener;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.xml.sax.EntityResolver;

public class ServiceImpl implements  IService {

	@Override
	public IConfiguration newAnnotationConfiguration() {
		return newDefaultConfiguration();
	}

	@Override
	public IConfiguration newJpaConfiguration(
			String entityResolver, 
			String persistenceUnit,
			Map<Object, Object> overrides) {
		return (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createJpaConfigurationWrapper(persistenceUnit, overrides));
	}

	@Override
	public IConfiguration newDefaultConfiguration() {
		return (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createNativeConfigurationWrapper());
	}

	@Override
	public IHibernateMappingExporter newHibernateMappingExporter(
			IConfiguration configuration, File file) {
		return (IHibernateMappingExporter)GenericFacadeFactory.createFacade(
				IHibernateMappingExporter.class, 
				WrapperFactory.createHbmExporterWrapper(((IFacade)configuration).getTarget(), file));
	}

	@Override
	public ISchemaExport newSchemaExport(IConfiguration configuration) {
		return (ISchemaExport)GenericFacadeFactory.createFacade(
				ISchemaExport.class, 
				WrapperFactory.createSchemaExport(((IFacade)configuration).getTarget()));
	}

	@Override
	public IHQLCodeAssist newHQLCodeAssist(IConfiguration configuration) {
		IHQLCodeAssist result = null;
		if (configuration instanceof IConfiguration) {
			result = (IHQLCodeAssist)GenericFacadeFactory.createFacade(
					IHQLCodeAssist.class, 
					WrapperFactory.createHqlCodeAssistWrapper(((IFacade)configuration).getTarget()));
		}
		return result;
	}

	@Override
	public IConfiguration newJDBCMetaDataConfiguration() {
		return (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createRevengConfigurationWrapper());
	}

	@Override
	public IExporter createExporter(String exporterClassName) {
		return (IExporter)GenericFacadeFactory.createFacade(
				IExporter.class, 
				WrapperFactory.createExporterWrapper(exporterClassName));
	}

	@Override
	public IArtifactCollector newArtifactCollector() {
		return (IArtifactCollector)GenericFacadeFactory.createFacade(
				IArtifactCollector.class, 
				WrapperFactory.createArtifactCollectorWrapper());
	}

	@Override
	public IHQLQueryPlan newHQLQueryPlan(String query, boolean shallow, ISessionFactory sessionFactory) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeFactory newTypeFactory() {
		return (ITypeFactory)GenericFacadeFactory.createFacade(
				ITypeFactory.class, 
				WrapperFactory.createTypeFactoryWrapper());
	}

	@Override
	public INamingStrategy newNamingStrategy(String strategyClassName) {
		return (INamingStrategy)GenericFacadeFactory.createFacade(
				INamingStrategy.class, 
				WrapperFactory.createNamingStrategyWrapper(strategyClassName));
	}

	@Override
	public IOverrideRepository newOverrideRepository() {
		return (IOverrideRepository)GenericFacadeFactory.createFacade(
				IOverrideRepository.class, 
				WrapperFactory.createOverrideRepositoryWrapper());
	}

	@Override
	public ITableFilter newTableFilter() {
		return (ITableFilter)GenericFacadeFactory.createFacade(
				ITableFilter.class, 
				WrapperFactory.createTableFilterWrapper());
	}


	@Override
	public IReverseEngineeringSettings newReverseEngineeringSettings(
			IReverseEngineeringStrategy res) {
		return (IReverseEngineeringSettings)GenericFacadeFactory.createFacade(
				IReverseEngineeringSettings.class, 
				WrapperFactory.createRevengSettingsWrapper(((IFacade)res).getTarget()));
	}

	@Override
	public IReverseEngineeringStrategy newDefaultReverseEngineeringStrategy() {
		return (IReverseEngineeringStrategy)GenericFacadeFactory.createFacade(
				IReverseEngineeringStrategy.class, 
				WrapperFactory.createRevengStrategyWrapper());
	}
	
	@Override
	public Map<String, List<ITable>> collectDatabaseTables(
			Properties properties, 
			IReverseEngineeringStrategy strategy,
			final IProgressListener progressListener) {
		return ((IDatabaseReader)GenericFacadeFactory.createFacade(
				IDatabaseReader.class, 
				WrapperFactory.createDatabaseReaderWrapper(
						properties,
						((IFacade)strategy).getTarget())))
				.collectDatabaseTables();
	}

	@Override
	public IReverseEngineeringStrategy newReverseEngineeringStrategy(
			String strategyName,
			IReverseEngineeringStrategy delegate) {
		return (IReverseEngineeringStrategy)GenericFacadeFactory.createFacade(
				IReverseEngineeringStrategy.class, 
				WrapperFactory.createRevengStrategyWrapper(strategyName, ((IFacade)delegate).getTarget()));
	}

	@Override
	public String getReverseEngineeringStrategyClassName() {
		return RevengStrategy.class.getName();
	}

	@Override
	public ICfg2HbmTool newCfg2HbmTool() {
		return (ICfg2HbmTool)GenericFacadeFactory.createFacade(
				ICfg2HbmTool.class,
				WrapperFactory.createCfg2HbmWrapper());
	}

	@Override
	public IProperty newProperty() {
		return (IProperty)GenericFacadeFactory.createFacade(
				IProperty.class, 
				WrapperFactory.createPropertyWrapper());
	}

	@Override
	public ITable newTable(String name) {
		return (ITable)GenericFacadeFactory.createFacade(
				ITable.class, 
				WrapperFactory.createTableWrapper(name));
	}

	@Override
	public IColumn newColumn(String name) {
		return (IColumn)GenericFacadeFactory.createFacade(
				IColumn.class, 
				WrapperFactory.createColumnWrapper(name));
	}

	@Override
	public String newDialect(Properties properties, final Connection connection) {
		ServiceRegistry serviceRegistry = buildServiceRegistry(properties);
		DialectFactory dialectFactory = serviceRegistry.getService(DialectFactory.class);
		Dialect dialect = dialectFactory.buildDialect(
				transform(properties), 
				new DialectResolutionInfoSource() {
					@Override
					public DialectResolutionInfo getDialectResolutionInfo() {
						try {
							return new DatabaseMetaDataDialectResolutionInfoAdapter( connection.getMetaData() );
						}
						catch ( SQLException sqlException ) {
							throw new HibernateException(
									"Unable to access java.sql.DatabaseMetaData to determine appropriate Dialect to use",
									sqlException
							);
						}
					}
				}
		);
		return dialect != null ? dialect.toString() : null;
	}

	@Override
	public Class<?> getDriverManagerConnectionProviderClass() {
		return DriverManagerConnectionProviderImpl.class;
	}

	@Override
	public IEnvironment getEnvironment() {
		return (IEnvironment)GenericFacadeFactory.createFacade(
				IEnvironment.class, 
				WrapperFactory.createEnvironmentWrapper());
	}

	@Override
	public IValue newSimpleValue() {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createSimpleValueWrapper());
	}

	@Override
	public IValue newPrimitiveArray(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createPrimitiveArrayWrapper(((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newArray(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createArrayWrapper(((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newBag(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createBagWrapper(((IFacade)persistentClass).getTarget()));
	}
	
	@Override
	public IValue newList(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createListWrapper(((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newMap(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createMapWrapper(((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newSet(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createSetWrapper(((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newManyToOne(ITable table) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createManyToOneWrapper(((IFacade)table).getTarget()));
	}

	@Override
	public IValue newOneToMany(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createOneToManyWrapper(((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newOneToOne(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createOneToOneWrapper(((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IPersistentClass newSingleTableSubclass(IPersistentClass persistentClass) {
		return (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createSingleTableSubClassWrapper(((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IPersistentClass newJoinedSubclass(IPersistentClass persistentClass) {
		return (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createJoinedTableSubClassWrapper(((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IPersistentClass newSpecialRootClass(IProperty property) {
		return (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createSpecialRootClassWrapper(((IFacade)property).getTarget()));
	}

	@Override
	public IPersistentClass newRootClass() {
		return (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
	}

	@Override
	public boolean isInitialized(Object object) {
		return Hibernate.isInitialized(object);
	}


	@Override
	public List<String> getJPAMappingFilePaths(String persistenceUnitName, EntityResolver entityResolver) {
		return JpaMappingFileHelper.findMappingFiles(persistenceUnitName);
	}
	
	@Override
	public Class<?> getClassWithoutInitializingProxy(Object reflectedObject) {
		if (reflectedObject instanceof HibernateProxy) {
			HibernateProxy proxy = (HibernateProxy) reflectedObject;
			LazyInitializer li = proxy.getHibernateLazyInitializer();
			return li.getPersistentClass();
		}
		else {
			return (Class<?>) reflectedObject.getClass();
		}
	}

	@Override
	public ClassLoader getClassLoader() {
		return ServiceImpl.class.getClassLoader();
	}
	
	@Override
	public IExporter createCfgExporter() {
		return createExporter(CfgExporter.class.getName());
	}
	

	private ServiceRegistry buildServiceRegistry(Properties properties) {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(properties);
		return builder.build();
	}

	private Map<String, Object> transform(Properties properties) {
		Map<String, Object> result = new HashMap<String, Object>(properties.size());
		for (Object key : properties.keySet()) {
			result.put((String)key, properties.get(key));
		}
		return result;
	}

}
