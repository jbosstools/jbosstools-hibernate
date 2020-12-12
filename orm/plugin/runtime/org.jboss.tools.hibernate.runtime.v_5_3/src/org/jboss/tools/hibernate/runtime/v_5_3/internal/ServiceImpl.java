package org.jboss.tools.hibernate.runtime.v_5_3.internal;

import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.hibernate.Filter;
import org.hibernate.Hibernate;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.JDBCReaderFactory;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ProgressListener;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfoSource;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
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
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.util.ReflectHelper;
import org.jboss.tools.hibernate.runtime.common.AbstractPersistentClassFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractService;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.Util;
import org.jboss.tools.hibernate.runtime.spi.HibernateException;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseReader;
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
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.runtime.v_5_3.internal.util.ConfigurationMetadataDescriptor;
import org.jboss.tools.hibernate.runtime.v_5_3.internal.util.DummyMetadataBuildingContext;
import org.jboss.tools.hibernate.runtime.v_5_3.internal.util.DummyMetadataDescriptor;
import org.jboss.tools.hibernate.runtime.v_5_3.internal.util.JdbcMetadataConfiguration;
import org.jboss.tools.hibernate.runtime.v_5_3.internal.util.JpaConfiguration;
import org.xml.sax.EntityResolver;

public class ServiceImpl extends AbstractService {

	private static final String HIBERNATE_VERSION = "5.3";
	
	private IFacadeFactory facadeFactory = new FacadeFactoryImpl();

	@Override
	public IConfiguration newAnnotationConfiguration() {
		return newDefaultConfiguration();
	}


	@Override
	public IConfiguration newJpaConfiguration(
			String entityResolver, 
			String persistenceUnit,
			Map<Object, Object> overrides) {
		return facadeFactory.createConfiguration(
				new JpaConfiguration(persistenceUnit, overrides));
	}

	@Override
	public IConfiguration newDefaultConfiguration() {
		getUsageTracker().trackNewConfigurationEvent(HIBERNATE_VERSION);
		return facadeFactory.createConfiguration(new Configuration());
	}

	@Override
	public IHibernateMappingExporter newHibernateMappingExporter(
			IConfiguration hcfg, File file) {
		return facadeFactory.createHibernateMappingExporter(
				new HibernateMappingExporterExtension(
						facadeFactory,
						hcfg,
						file));
	}

	@Override
	public ISchemaExport newSchemaExport(IConfiguration hcfg) {
		SchemaExportFacadeImpl result = (SchemaExportFacadeImpl)facadeFactory
				.createSchemaExport(new SchemaExport());
		result.setConfiguration(hcfg);
		return result;
	}

	@Override
	public IHQLCodeAssist newHQLCodeAssist(IConfiguration hcfg) {
		IHQLCodeAssist result = null;
		if (hcfg instanceof ConfigurationFacadeImpl) {
			result = facadeFactory.createHQLCodeAssist(
					new HQLCodeAssist(((ConfigurationFacadeImpl)hcfg).getMetadata()));
		}
		return result;
	}

	@Override
	public IConfiguration newJDBCMetaDataConfiguration() {
		return facadeFactory.createConfiguration(new JdbcMetadataConfiguration());
	}

	@Override
	public IExporter createExporter(String exporterClassName) {
		Exporter exporter = (Exporter)Util.getInstance(
				exporterClassName, 
				facadeFactory.getClassLoader());
		if ("org.hibernate.tool.hbm2x.HibernateConfigurationExporter".equals(exporterClassName)) {
			exporter.setMetadataDescriptor(new DummyMetadataDescriptor());
		} else {
		  exporter.setMetadataDescriptor(
				new ConfigurationMetadataDescriptor(newDefaultConfiguration()));
		}
		return facadeFactory.createExporter(exporter);
	}

	@Override
	public IArtifactCollector newArtifactCollector() {
		return facadeFactory.createArtifactCollector(new ArtifactCollector());
	}

	@Override
	public IHQLQueryPlan newHQLQueryPlan(String query, boolean shallow, ISessionFactory sessionFactory) {
		SessionFactoryImpl factory = 
				(SessionFactoryImpl) ((IFacade)sessionFactory).getTarget();
		Map<String, Filter> enabledFilters = Collections.emptyMap(); 
		HQLQueryPlan queryPlan = new HQLQueryPlan(query, shallow, enabledFilters, factory);
		return facadeFactory.createHQLQueryPlan(queryPlan);
	}

	@Override
	public ITypeFactory newTypeFactory() {
		// target for ITypeFactory is a dummy Object
		return facadeFactory.createTypeFactory();
	}

	@Override
	public INamingStrategy newNamingStrategy(String strategyClassName) {
		try {
			return facadeFactory.createNamingStrategy(
					Class.forName(strategyClassName).newInstance());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			return null;
		}
	}

	@Override
	public IOverrideRepository newOverrideRepository() {
		return facadeFactory.createOverrideRepository(new OverrideRepository());
	}

	@Override
	public ITableFilter newTableFilter() {
		return facadeFactory.createTableFilter(new TableFilter());
	}

	@Override
	public IReverseEngineeringSettings newReverseEngineeringSettings(
			IReverseEngineeringStrategy res) {
		assert res instanceof IFacade;
		return facadeFactory.createReverseEngineeringSettings(
				new ReverseEngineeringSettings(
						(ReverseEngineeringStrategy)((IFacade)res).getTarget()));
	}

	@Override
	public IReverseEngineeringStrategy newDefaultReverseEngineeringStrategy() {
		return facadeFactory.createReverseEngineeringStrategy(
				new DefaultReverseEngineeringStrategy());
	}

	@Override
	public IDatabaseReader newDatabaseReader(
			Properties properties,
			IReverseEngineeringStrategy strategy) {
		assert strategy instanceof IFacade;
		JDBCReader target = 
				JDBCReaderFactory.newJDBCReader(
						properties, 
						(ReverseEngineeringStrategy)((IFacade)strategy).getTarget(),
						buildServiceRegistry(properties));
		return facadeFactory.createDatabaseReader(target);
	}

	@Override
	public Map<String, List<ITable>> collectDatabaseTables(
			Properties properties, 
			IReverseEngineeringStrategy strategy,
			final IProgressListener progressListener) {
		Map<String, List<ITable>> result = new HashMap<String, List<ITable>>();
		JDBCReader jdbcReader = 
				JDBCReaderFactory.newJDBCReader(
						properties, 
						(ReverseEngineeringStrategy)((IFacade)strategy).getTarget(),
						buildServiceRegistry(properties));
		MetaDataDialect metadataDialect = jdbcReader.getMetaDataDialect();
		DefaultDatabaseCollector databaseCollector = new DefaultDatabaseCollector(metadataDialect);
		ProgressListener progressListenerWrapper = new ProgressListener() {			
			@Override
			public void startSubTask(String name) {
				progressListener.startSubTask(name);
			}
		};
		jdbcReader.readDatabaseSchema(
				databaseCollector, 
				properties.getProperty(Environment.DEFAULT_CATALOG), 
				properties.getProperty(Environment.DEFAULT_SCHEMA),
				progressListenerWrapper);
		Iterator<?> iterator = databaseCollector.getQualifierEntries();
		while (iterator.hasNext()) {
			Entry<?, ?> entry = (Entry<?, ?>)iterator.next();
			ArrayList<ITable> list = new ArrayList<ITable>();
			for (Object table : (Iterable<?>)entry.getValue()) {
				list.add(facadeFactory.createTable(table));
			}
			result.put((String)entry.getKey(), list);
		}
		return result;
	}
	
	@Override
	public IReverseEngineeringStrategy newReverseEngineeringStrategy(
			String strategyName,
			IReverseEngineeringStrategy delegate) {
		assert delegate instanceof IFacade;
		ReverseEngineeringStrategy delegateTarget = 
				(ReverseEngineeringStrategy)((IFacade)delegate).getTarget();
		Object target = 
				newReverseEngineeringStrategy(strategyName, delegateTarget);
		return facadeFactory.createReverseEngineeringStrategy(target);
	}

	@Override
	public String getReverseEngineeringStrategyClassName() {
		return ReverseEngineeringStrategy.class.getName();
	}

	@Override
	public ICfg2HbmTool newCfg2HbmTool() {
		return facadeFactory.createCfg2HbmTool(new Cfg2HbmTool());
	}

	@Override
	public IProperty newProperty() {
		return facadeFactory.createProperty(new Property());
	}

	@Override
	public ITable newTable(String name) {
		Table target = new Table(name);
		target.setPrimaryKey(new PrimaryKey(target));
		return facadeFactory.createTable(target);
	}

	@Override
	public IColumn newColumn(String string) {
		return facadeFactory.createColumn(new Column(string));
	}

	@Override
	public String newDialect(Properties properties, final Connection connection) {
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
		return facadeFactory.createEnvironment();
	}

	@Override
	public IValue newSimpleValue() {
		return facadeFactory.createValue(new SimpleValue(DummyMetadataBuildingContext.INSTANCE));
	}

	@Override
	public IValue newPrimitiveArray(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(
				new PrimitiveArray(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newArray(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(
				new Array(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newBag(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(
				new Bag(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newList(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(
				new org.hibernate.mapping.List(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newMap(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(
				new org.hibernate.mapping.Map(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newSet(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(
				new Set(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newManyToOne(ITable table) {
		assert table instanceof IFacade;
		return facadeFactory.createValue(
				new ManyToOne(
						DummyMetadataBuildingContext.INSTANCE, 
						(Table)((IFacade)table).getTarget()));
	}

	@Override
	public IValue newOneToMany(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(
				new OneToMany(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newOneToOne(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(
				new OneToOne(
						DummyMetadataBuildingContext.INSTANCE, 
						((PersistentClass)((IFacade)persistentClass).getTarget()).getTable(), 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IPersistentClass newSingleTableSubclass(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		IPersistentClass result = facadeFactory.createPersistentClass(
				new SingleTableSubclass(
						(PersistentClass)((IFacade)persistentClass).getTarget(),
						null));
		((AbstractPersistentClassFacade)result).setSuperClass(persistentClass);
		return result;
	}

	@Override
	public IPersistentClass newJoinedSubclass(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		IPersistentClass result = facadeFactory.createPersistentClass(
				new JoinedSubclass(
						(PersistentClass)((IFacade)persistentClass).getTarget(),
						null));
		((AbstractPersistentClassFacade)result).setSuperClass(persistentClass);
		return result;
	}

	@Override
	public IPersistentClass newSpecialRootClass(IProperty ormElement) {
		return facadeFactory.createSpecialRootClass(ormElement);
	}

	@Override
	public IPersistentClass newRootClass() {
		return facadeFactory.createPersistentClass(new RootClass(null));
	}

	@Override
	public boolean isInitialized(Object object) {
		return Hibernate.isInitialized(object);
	}

	@Override
	public List<String> getJPAMappingFilePaths(String persistenceUnitName, EntityResolver entityResolver) {
		return OpenMappingUtilsEjb3.enumDocuments(persistenceUnitName, entityResolver);
	}

	@Override
	public Class<?> getClassWithoutInitializingProxy(Object reflectedObject) {
		return HibernateProxyHelper.getClassWithoutInitializingProxy(reflectedObject);
	}

	@Override
	public ClassLoader getClassLoader() {
		return ServiceImpl.class.getClassLoader();
	}

	private ServiceRegistry buildServiceRegistry(Properties properties) {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(properties);
		return builder.build();
	}

	private Object newReverseEngineeringStrategy(final String className, Object delegate) {
        try {
            Class<?> clazz = ReflectHelper.classForName(className);
            Class<?> revEngClass =  ReflectHelper.classForName(
            		"org.hibernate.cfg.reveng.ReverseEngineeringStrategy");
			Constructor<?> constructor = 
					clazz.getConstructor(
							new Class[] { revEngClass });
            return constructor.newInstance(new Object[] { delegate });
        }
        catch (NoSuchMethodException e) {
			try {
				ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
				Class<?> clazz = null;
				if ( contextClassLoader != null ) {
					clazz = contextClassLoader.loadClass(className);
				} else {
					clazz = Class.forName( className );
				}
				if (clazz != null) {
					return clazz.newInstance();
				} else {
					throw new HibernateException("Class " + className + " could not be found.");
				}
			}
			catch (Exception eq) {
				throw new HibernateException(eq);
			}
		}
        catch (Exception e) {
			throw new HibernateException(e);
		}
    }

}
