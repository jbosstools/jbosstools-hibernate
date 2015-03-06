package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.Hibernate;
import org.hibernate.annotations.common.util.StandardClassLoaderDelegateImpl;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.JDBCReaderFactory;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfoSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Table;
import org.hibernate.proxy.HibernateProxyHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.util.xpl.ReflectHelper;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingGlobalSettings;
import org.jboss.tools.hibernate.runtime.spi.IJDBCReader;
import org.jboss.tools.hibernate.runtime.spi.IMetaDataDialect;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ISettings;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.ITableIdentifier;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.runtime.v_4_3.internal.FacadeFactoryImpl;
import org.jboss.tools.hibernate.util.OpenMappingUtilsEjb3;
import org.xml.sax.EntityResolver;

public class ServiceProxy implements IService {
	
	private IFacadeFactory facadeFactory = new FacadeFactoryImpl();

	@Override
	public IConfiguration newAnnotationConfiguration() {
		Configuration configuration = new Configuration();
		return new ConfigurationProxy(configuration);
	}
	
	@Override
	public IConfiguration newJpaConfiguration(
			String entityResolver, 
			String persistenceUnit, 
			Map<Object, Object> overrides) {
		IConfiguration result = null;
		try {
			HibernatePersistenceProvider hibernatePersistenceProvider = new HibernatePersistenceProvider();
			Method getEntityManagerFactoryBuilderOrNull = hibernatePersistenceProvider.getClass().getDeclaredMethod(
					"getEntityManagerFactoryBuilderOrNull", 
					new Class[] { String.class, Map.class });
			getEntityManagerFactoryBuilderOrNull.setAccessible(true);
			Object entityManagerFactoryBuilder = 
					getEntityManagerFactoryBuilderOrNull.invoke(
							hibernatePersistenceProvider, 
							new Object[] { persistenceUnit, overrides});	
			if (entityManagerFactoryBuilder == null) {
				throw new HibernateConsoleRuntimeException(
						"Persistence unit not found: '" + 
						persistenceUnit + 
						"'.");
			}		
			Method buildServiceRegistry = 
					entityManagerFactoryBuilder.getClass().getMethod(
							"buildServiceRegistry", new Class[0]);
			Object serviceRegistry = buildServiceRegistry.invoke(entityManagerFactoryBuilder, (Object[])null);		
			Class<?> serviceRegistryClass = StandardClassLoaderDelegateImpl.INSTANCE.classForName(
					"org.hibernate.service.ServiceRegistry");
			Method buildHibernateConfiguration = 
					entityManagerFactoryBuilder.getClass().getMethod(
							"buildHibernateConfiguration", 
							new Class[] { serviceRegistryClass });		
			Configuration configuration = (Configuration)buildHibernateConfiguration.invoke(entityManagerFactoryBuilder, new Object[] { serviceRegistry });		
			result = new ConfigurationProxy(configuration);
		} catch (SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException  e) {
			throw new HibernateConsoleRuntimeException(e);
		}
		return result;
	}

	@Override
	public IConfiguration newDefaultConfiguration() {
		return new ConfigurationProxy(new Configuration());
	}

	@Override
	public IHibernateMappingExporter newHibernateMappingExporter(
			IConfiguration hcfg, File file) {
		return new HibernateMappingExporterProxy(hcfg, file);
	}

	@Override
	public ISchemaExport newSchemaExport(IConfiguration hcfg) {
		ISchemaExport result = null;
		if (hcfg instanceof ConfigurationProxy) {
			SchemaExport schemaExport = 
					new SchemaExport(((ConfigurationProxy)hcfg).getConfiguration());
			result = new SchemaExportProxy(schemaExport);
		}
		return result;
	}

	@Override
	public IHQLCodeAssist newHQLCodeAssist(IConfiguration hcfg) {
		IHQLCodeAssist result = null;
		if (hcfg instanceof ConfigurationProxy) {
			result = new HQLCodeAssistProxy(new HQLCodeAssist(((ConfigurationProxy)hcfg).getConfiguration()));
		}
		return result;
	}

	@Override
	public IConfiguration newJDBCMetaDataConfiguration() {
		Configuration configuration = new JDBCMetaDataConfiguration();
		return new ConfigurationProxy(configuration);
	}

	@Override
	public IExporter createExporter(String exporterClassName) {
		return new ExporterProxy(exporterClassName);
	}

	@Override
	public IArtifactCollector newArtifactCollector() {
		return facadeFactory.createArtifactCollector();
	}

	@Override
	public IHQLQueryPlan newHQLQueryPlan(
			String query, 
			boolean shallow,
			ISessionFactory sessionFactory) {
		return new HQLQueryPlanProxy(query, shallow, sessionFactory);
	}

	@Override
	public ITypeFactory newTypeFactory() {
		return new TypeFactoryProxy();
	}

	@Override
	public INamingStrategy newNamingStrategy(String strategyClassName) {
		try {
			NamingStrategy ns = (NamingStrategy) ReflectHelper.classForName(
					strategyClassName).newInstance();
			return new NamingStrategyProxy(ns);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			return null;
		}
	}

	@Override
	public IOverrideRepository newOverrideRepository() {
		return new OverrideRepositoryProxy(new OverrideRepository());
	}

	@Override
	public ITableFilter newTableFilter() {
		return new TableFilterProxy(new TableFilter());
	}

	@Override
	public IReverseEngineeringSettings newReverseEngineeringSettings(
			IReverseEngineeringStrategy res) {
		assert res instanceof ReverseEngineeringStrategyProxy;
		return new ReverseEngineeringSettingsProxy(
				new ReverseEngineeringSettings(
						((ReverseEngineeringStrategyProxy)res).getTarget()));
	}

	@Override
	public IReverseEngineeringStrategy newDefaultReverseEngineeringStrategy() {
		return new ReverseEngineeringStrategyProxy(new DefaultReverseEngineeringStrategy());
	}

	@Override
	public IJDBCReader newJDBCReader(IConfiguration configuration, ISettings settings,
			IReverseEngineeringStrategy strategy) {
		assert strategy instanceof ReverseEngineeringStrategyProxy;
		assert settings instanceof SettingsProxy;
		JDBCReader target = 
				JDBCReaderFactory.newJDBCReader(
						configuration.getProperties(), 
						((SettingsProxy)settings).getTarget(), 
						((ReverseEngineeringStrategyProxy)strategy).getTarget(),
						buildServiceRegistry(configuration.getProperties()));
		return new JDBCReaderProxy(target);
	}

	private ServiceRegistry buildServiceRegistry(Properties properties) {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(properties);
		return builder.build();
	}

	@Override
	public IReverseEngineeringStrategy newReverseEngineeringStrategy(
			String strategyName, IReverseEngineeringStrategy delegate) {
		assert delegate instanceof ReverseEngineeringStrategyProxy;
		ReverseEngineeringStrategy target = 
				newReverseEngineeringStrategy(strategyName, ((ReverseEngineeringStrategyProxy)delegate).getTarget());
		return new ReverseEngineeringStrategyProxy(target);
	}
	
	@SuppressWarnings("unchecked")
	private ReverseEngineeringStrategy newReverseEngineeringStrategy(final String className, ReverseEngineeringStrategy delegate) {
        try {
            Class<ReverseEngineeringStrategy> clazz = (Class<ReverseEngineeringStrategy>)ReflectHelper.classForName(className);
			Constructor<ReverseEngineeringStrategy> constructor = clazz.getConstructor(new Class[] { ReverseEngineeringStrategy.class });
            return constructor.newInstance(new Object[] { delegate });
        }
        catch (NoSuchMethodException e) {
			try {
				Class<?> clazz = ReflectHelper.classForName(className);
				ReverseEngineeringStrategy rev = (ReverseEngineeringStrategy) clazz.newInstance();
				return rev;
			}
			catch (Exception eq) {
				throw new HibernateConsoleRuntimeException(eq);
			}
		}
        catch (Exception e) {
			throw new HibernateConsoleRuntimeException(e);
		}
    }

	@Override
	public String getReverseEngineeringStrategyClassName() {
		return ReverseEngineeringStrategy.class.getName();
	}
	
	@Override
	public IDatabaseCollector newDatabaseCollector(IMetaDataDialect metaDataDialect) {
		assert metaDataDialect instanceof MetaDataDialectProxy;
		return new DatabaseCollectorProxy(
				new DefaultDatabaseCollector(
						((MetaDataDialectProxy)metaDataDialect).getTarget()));
	}

	@Override
	public ICfg2HbmTool newCfg2HbmTool() {
		return facadeFactory.createCfg2HbmTool();
	}

	@Override
	public IProperty newProperty() {
		return new PropertyProxy(new Property());
	}

	@Override
	public ITable newTable(String name) {
		return new TableProxy(new Table(name));
	}

	@Override
	public IColumn newColumn(String string) {
		return new ColumnProxy(new Column(string));
	}

	@Override
	public IDialect newDialect(Properties properties, final Connection connection) {
		ServiceRegistry serviceRegistry = buildServiceRegistry(properties);
		DialectFactory dialectFactory = serviceRegistry.getService(DialectFactory.class);
		Dialect dialect = dialectFactory.buildDialect(
				properties, 
				new DialectResolutionInfoSource() {
					@Override
					public DialectResolutionInfo getDialectResolutionInfo() {
						try {
							return new DatabaseMetaDataDialectResolutionInfoAdapter( connection.getMetaData() );
						}
						catch ( SQLException sqlException ) {
							throw new HibernateConsoleRuntimeException(
									"Unable to access java.sql.DatabaseMetaData to determine appropriate Dialect to use",
									sqlException
							);
						}
					}
				}
		);
		return dialect != null ? new DialectProxy(dialect) : null;
	}

	@Override
	public Class<?> getDriverManagerConnectionProviderClass() {
		return DriverManagerConnectionProviderImpl.class;
	}

	@Override
	public IEnvironment getEnvironment() {
		return new EnvironmentProxy();
	}
	
	@Override
	public IValue newSimpleValue() {
		return new ValueProxy(new SimpleValue(null));
	}

	@Override
	public IValue newPrimitiveArray(IPersistentClass persistentClass) {
		assert persistentClass instanceof PersistentClassProxy;
		return new ValueProxy(new PrimitiveArray(null, ((PersistentClassProxy)persistentClass).getTarget()));
	}

	@Override
	public IValue newArray(IPersistentClass persistentClass) {
		assert persistentClass instanceof PersistentClassProxy;
		return new ValueProxy(new Array(null, ((PersistentClassProxy)persistentClass).getTarget()));
	}

	@Override
	public IValue newBag(IPersistentClass persistentClass) {
		assert persistentClass instanceof PersistentClassProxy;
		return new ValueProxy(new Bag(null, ((PersistentClassProxy)persistentClass).getTarget()));
	}

	@Override
	public IValue newList(IPersistentClass persistentClass) {
		assert persistentClass instanceof PersistentClassProxy;
		return new ValueProxy(new org.hibernate.mapping.List(null, ((PersistentClassProxy)persistentClass).getTarget()));
	}

	@Override
	public IValue newMap(IPersistentClass persistentClass) {
		assert persistentClass instanceof PersistentClassProxy;
		return new ValueProxy(new org.hibernate.mapping.Map(null, ((PersistentClassProxy)persistentClass).getTarget()));
	}

	@Override
	public IValue newSet(IPersistentClass persistentClass) {
		assert persistentClass instanceof PersistentClassProxy;
		return new ValueProxy(new Set(null, ((PersistentClassProxy)persistentClass).getTarget()));
	}

	@Override
	public IValue newManyToOne(ITable table) {
		assert table instanceof TableProxy;
		return new ValueProxy(new ManyToOne(null, ((TableProxy)table).getTarget()));
	}

	@Override
	public IValue newOneToMany(IPersistentClass persistentClass) {
		assert persistentClass instanceof PersistentClassProxy;
		return new ValueProxy(new OneToMany(null, ((PersistentClassProxy)persistentClass).getTarget()));
	}

	@Override
	public IValue newOneToOne(IPersistentClass persistentClass) {
		assert persistentClass instanceof PersistentClassProxy;
		return new ValueProxy(new OneToOne(null, ((PersistentClassProxy)persistentClass).getTarget().getTable(), ((PersistentClassProxy)persistentClass).getTarget()));
	}

	@Override
	public IPersistentClass newSingleTableSubclass(
			IPersistentClass persistentClass) {
		assert persistentClass instanceof PersistentClassProxy;
		return new PersistentClassProxy(new SingleTableSubclass(((PersistentClassProxy)persistentClass).getTarget()));
	}

	@Override
	public IPersistentClass newJoinedSubclass(IPersistentClass persistentClass) {
		assert persistentClass instanceof PersistentClassProxy;
		return new PersistentClassProxy(new JoinedSubclass(((PersistentClassProxy)persistentClass).getTarget()));
	}

	@Override
	public IPersistentClass newSpecialRootClass(IProperty ormElement) {
		return new SpecialRootClassProxy(ormElement);
	}

	@Override
	public IPersistentClass newRootClass() {
		return new PersistentClassProxy(new RootClass());
	}

	@Override
	public IPrimaryKey newPrimaryKey() {
		return new PrimaryKeyProxy(new PrimaryKey());
	}

	@Override
	public IHibernateMappingGlobalSettings newHibernateMappingGlobalSettings() {
		return new HibernateMappingGlobalSettingsProxy(new HibernateMappingGlobalSettings());
	}

	@Override
	public ITableIdentifier createTableIdentifier(ITable table) {
		assert table instanceof TableProxy;
		return new TableIdentifierProxy(TableIdentifier.create(((TableProxy)table).getTarget()));
	}

	@Override
	public ITableIdentifier newTableIdentifier(String catalog, String schema,
			String name) {
		return new TableIdentifierProxy(new TableIdentifier(catalog, schema, name));
	}

	@Override
	public boolean isInitialized(Object object) {
		return Hibernate.isInitialized(object);
	}

	@Override
	public List<String> getJPAMappingFilePaths(
			String persistenceUnitName, EntityResolver entityResolver) {
		return OpenMappingUtilsEjb3.enumDocuments(persistenceUnitName, entityResolver);
	}

	@Override
	public Class<?> getClassWithoutInitializingProxy(Object reflectedObject) {
		return HibernateProxyHelper.getClassWithoutInitializingProxy(reflectedObject);
	}

	@Override
	public ClassLoader getClassLoader() {
		return ServiceProxy.class.getClassLoader();
	}

}
