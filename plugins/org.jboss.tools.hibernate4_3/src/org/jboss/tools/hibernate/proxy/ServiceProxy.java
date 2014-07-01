package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
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
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.dialect.internal.DialectFactoryImpl;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfoSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.util.xpl.ReflectHelper;
import org.jboss.tools.hibernate.spi.IArtifactCollector;
import org.jboss.tools.hibernate.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.spi.IColumn;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.spi.IDialect;
import org.jboss.tools.hibernate.spi.IEnvironment;
import org.jboss.tools.hibernate.spi.IExporter;
import org.jboss.tools.hibernate.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.spi.IJDBCReader;
import org.jboss.tools.hibernate.spi.IMetaDataDialect;
import org.jboss.tools.hibernate.spi.INamingStrategy;
import org.jboss.tools.hibernate.spi.IOverrideRepository;
import org.jboss.tools.hibernate.spi.IProgressListener;
import org.jboss.tools.hibernate.spi.IProperty;
import org.jboss.tools.hibernate.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.spi.ISchemaExport;
import org.jboss.tools.hibernate.spi.IService;
import org.jboss.tools.hibernate.spi.ISessionFactory;
import org.jboss.tools.hibernate.spi.ISettings;
import org.jboss.tools.hibernate.spi.ITable;
import org.jboss.tools.hibernate.spi.ITableFilter;
import org.jboss.tools.hibernate.spi.ITypeFactory;
import org.jboss.tools.hibernate.spi.IValue;

public class ServiceProxy implements IService {
	
	private ServiceRegistry serviceRegistry = null;

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
			Object serviceRegistry = buildServiceRegistry.invoke(entityManagerFactoryBuilder, null);		
			Class serviceRegistryClass = StandardClassLoaderDelegateImpl.INSTANCE.classForName(
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
	public void setExporterConfiguration(
			IExporter exporter,
			IConfiguration hcfg) {
		exporter.setConfiguration(hcfg);
	}

	@Override
	public HibernateMappingExporter newHibernateMappingExporter(
			IConfiguration hcfg, File file) {
		HibernateMappingExporter result = null;
		if (hcfg instanceof ConfigurationProxy) {
			result = new HibernateMappingExporter(((ConfigurationProxy)hcfg).getConfiguration()	, file);
		}
		return result;
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
	public HQLCodeAssist newHQLCodeAssist(IConfiguration hcfg) {
		HQLCodeAssist result = null;
		if (hcfg instanceof ConfigurationProxy) {
			result = new HQLCodeAssist(((ConfigurationProxy)hcfg).getConfiguration());
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
		return new ArtifactCollectorProxy();
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
	public IJDBCReader newJDBCReader(Properties properties, ISettings settings,
			IReverseEngineeringStrategy strategy) {
		assert strategy instanceof ReverseEngineeringStrategyProxy;
		assert settings instanceof SettingsProxy;
		JDBCReader target = 
				JDBCReaderFactory.newJDBCReader(
						properties, 
						((SettingsProxy)settings).getTarget(), 
						((ReverseEngineeringStrategyProxy)strategy).getTarget(),
						getServiceRegistry());
		return new JDBCReaderProxy(target);
	}

	private ServiceRegistry getServiceRegistry() {
		if (serviceRegistry == null) {
			StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
			builder.configure();
			serviceRegistry = builder.build();
		}
		return serviceRegistry;
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
            Class<ReverseEngineeringStrategy> clazz = ReflectHelper.classForName(className);
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
	public IProgressListener newProgressListener(IProgressMonitor monitor) {
		return new ProgressListenerProxy(monitor);
	}

	@Override
	public ICfg2HbmTool newCfg2HbmTool() {
		return new Cfg2HbmToolProxy(new Cfg2HbmTool());
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
		Dialect dialect = new DialectFactoryImpl().buildDialect(
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
	public IValue newPrimitiveArray(PersistentClass persistentClass) {
		return new ValueProxy(new PrimitiveArray(null, persistentClass));
	}

	@Override
	public IValue newArray(PersistentClass persistentClass) {
		return new ValueProxy(new Array(null, persistentClass));
	}

	@Override
	public IValue newBag(PersistentClass persistentClass) {
		return new ValueProxy(new Bag(null, persistentClass));
	}

}
