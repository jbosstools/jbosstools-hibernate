package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
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
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.List;
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
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.service.jdbc.dialect.internal.DialectFactoryImpl;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.util.xpl.ReflectHelper;
import org.hibernate.util.xpl.StringHelper;
import org.jboss.tools.hibernate.spi.IArtifactCollector;
import org.jboss.tools.hibernate.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.spi.IColumn;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.spi.IDialect;
import org.jboss.tools.hibernate.spi.IEnvironment;
import org.jboss.tools.hibernate.spi.IExporter;
import org.jboss.tools.hibernate.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.spi.IHibernateMappingGlobalSettings;
import org.jboss.tools.hibernate.spi.IJDBCReader;
import org.jboss.tools.hibernate.spi.IMetaDataDialect;
import org.jboss.tools.hibernate.spi.INamingStrategy;
import org.jboss.tools.hibernate.spi.IOverrideRepository;
import org.jboss.tools.hibernate.spi.IPersistentClass;
import org.jboss.tools.hibernate.spi.IPrimaryKey;
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
import org.jboss.tools.hibernate.spi.ITableIdentifier;
import org.jboss.tools.hibernate.spi.IType;
import org.jboss.tools.hibernate.spi.ITypeFactory;
import org.jboss.tools.hibernate.spi.IValue;
import org.jboss.tools.hibernate.util.HibernateHelper;
import org.jboss.tools.hibernate.util.TypeFormats;
import org.xml.sax.EntityResolver;

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
		Ejb3Configuration ejb3Configuration = new Ejb3Configuration();
		if (StringHelper.isNotEmpty(entityResolver)) {
			try {
				Class<?> resolver = ReflectHelper.classForName(entityResolver, this.getClass());
				Object object = resolver.newInstance();
				ejb3Configuration.setEntityResolver((EntityResolver)object);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				throw new HibernateConsoleRuntimeException(e);
			}
		}
		ejb3Configuration.configure(persistenceUnit, overrides);
		Configuration configuration = ejb3Configuration.getHibernateConfiguration();
		return new ConfigurationProxy(configuration);
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
	public IHibernateMappingExporter newHibernateMappingExporter(
			IConfiguration hcfg, File file) {
		IHibernateMappingExporter result = null;
		if (hcfg instanceof ConfigurationProxy) {
			result = new HibernateMappingExporterProxy(new HibernateMappingExporter(((ConfigurationProxy)hcfg).getConfiguration()	, file));
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
		return HibernateHelper.INSTANCE.getHibernateService().newArtifactCollector();
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
			ServiceRegistryBuilder builder = new ServiceRegistryBuilder();
			builder.configure();
			serviceRegistry = builder.buildServiceRegistry();
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
	public IDialect newDialect(Properties properties, Connection connection) {
		Dialect dialect = new DialectFactoryImpl().buildDialect(properties, connection);
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
		return new ValueProxy(new List(null, ((PersistentClassProxy)persistentClass).getTarget()));
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
	public Map<IType, String> getTypeFormats() {
		return TypeFormats.getTypeFormats();
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

}
