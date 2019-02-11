package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.Filter;
import org.hibernate.Hibernate;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.JDBCReaderFactory;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.StringHelper;
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
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.classloading.internal.ClassLoaderServiceImpl;
import org.hibernate.service.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.service.jdbc.dialect.internal.DialectFactoryImpl;
import org.hibernate.service.jdbc.dialect.internal.StandardDialectResolver;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.jboss.tools.hibernate.runtime.common.AbstractService;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.Util;
import org.jboss.tools.hibernate.runtime.spi.HibernateException;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IJDBCReader;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
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

public class ServiceImpl extends AbstractService implements IService {

	private static final String HIBERNATE_VERSION = "4.0";
	
	private IFacadeFactory facadeFactory = new FacadeFactoryImpl();

	@Override
	public IConfiguration newAnnotationConfiguration() {
		getUsageTracker().trackNewConfigurationEvent(HIBERNATE_VERSION);
		Configuration configuration = new Configuration();
		return facadeFactory.createConfiguration(configuration);
	}

	@Override
	public IConfiguration newJpaConfiguration(
			String entityResolver, 
			String persistenceUnit, 
			Map<Object, Object> overrides) {
		getUsageTracker().trackNewConfigurationEvent(HIBERNATE_VERSION);
		Ejb3Configuration ejb3Configuration = new Ejb3Configuration();
		if (StringHelper.isNotEmpty(entityResolver)) {
			try {
				Class<?> resolver = ReflectHelper.classForName(entityResolver, this.getClass());
				Object object = resolver.newInstance();
				ejb3Configuration.setEntityResolver((EntityResolver)object);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				throw new HibernateException(e);
			}
		}
		ejb3Configuration.configure(persistenceUnit, overrides);
		Configuration configuration = ejb3Configuration.getHibernateConfiguration();
		return facadeFactory.createConfiguration(configuration);
	}

	@Override
	public IConfiguration newDefaultConfiguration() {
		getUsageTracker().trackNewConfigurationEvent(HIBERNATE_VERSION);
		return facadeFactory.createConfiguration(new Configuration());
	}

	@Override
	public IHibernateMappingExporter newHibernateMappingExporter(
			IConfiguration hcfg, File file) {
		assert hcfg instanceof IFacade;
		HibernateMappingExporterExtension target = new HibernateMappingExporterExtension(
				facadeFactory,
				(Configuration)((IFacade)hcfg).getTarget(),
				file);
		return facadeFactory.createHibernateMappingExporter(target);
	}

	@Override
	public ISchemaExport newSchemaExport(IConfiguration hcfg) {
		ISchemaExport result = null;
		if (hcfg instanceof IFacade) {
			SchemaExport schemaExport = 
					new SchemaExport((Configuration)((IFacade)hcfg).getTarget());
			result = facadeFactory.createSchemaExport(schemaExport);
		}
		return result;
	}

	@Override
	public IHQLCodeAssist newHQLCodeAssist(IConfiguration hcfg) {
		IHQLCodeAssist result = null;
		if (hcfg instanceof IFacade) {
			result = facadeFactory.createHQLCodeAssist(
					new HQLCodeAssist((Configuration)((IFacade)hcfg).getTarget()));
		}
		return result;
	}

	@Override
	public IConfiguration newJDBCMetaDataConfiguration() {
		Configuration configuration = new JDBCMetaDataConfiguration();
		return facadeFactory.createConfiguration(configuration);
	}

	@Override
	public IExporter createExporter(String exporterClassName) {
		Exporter exporter = (Exporter)Util.getInstance(
				exporterClassName, 
				facadeFactory.getClassLoader());
		return facadeFactory.createExporter(exporter);
	}

	@Override
	public IArtifactCollector newArtifactCollector() {
		return facadeFactory.createArtifactCollector(new ArtifactCollector());
	}

	@Override
	public IHQLQueryPlan newHQLQueryPlan(
			String query, 
			boolean shallow,
			ISessionFactory sessionFactory) {
		assert sessionFactory instanceof IFacade;
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
			NamingStrategy ns = (NamingStrategy) ReflectHelper.classForName(
					strategyClassName).newInstance();
			return facadeFactory.createNamingStrategy(ns);
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
	public IJDBCReader newJDBCReader(
			IConfiguration configuration,
			IReverseEngineeringStrategy strategy) {
		assert strategy instanceof IFacade;
		JDBCReader target = 
				JDBCReaderFactory.newJDBCReader(
						configuration.getProperties(), 
						(Settings)((ConfigurationFacadeImpl)configuration).buildTargetSettings(),
						(ReverseEngineeringStrategy)((IFacade)strategy).getTarget(),
						buildServiceRegistry(configuration));
		return facadeFactory.createJDBCReader(target);
	}

	private ServiceRegistry buildServiceRegistry(IConfiguration configuration) {
		ServiceRegistryBuilder builder = new ServiceRegistryBuilder();
		builder.applySettings(configuration.getProperties());
		return builder.buildServiceRegistry();
	}

	@Override
	public IReverseEngineeringStrategy newReverseEngineeringStrategy(
			String strategyName, IReverseEngineeringStrategy delegate) {
		assert delegate instanceof IFacade;
		ReverseEngineeringStrategy target = 
				newReverseEngineeringStrategy(strategyName, (ReverseEngineeringStrategy)((IFacade)delegate).getTarget());
		return facadeFactory.createReverseEngineeringStrategy(target);
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
				throw new HibernateException(eq);
			}
		}
        catch (Exception e) {
			throw new HibernateException(e);
		}
    }

	@Override
	public String getReverseEngineeringStrategyClassName() {
		return ReverseEngineeringStrategy.class.getName();
	}
	
	@Override
	public IDatabaseCollector newDatabaseCollector(IJDBCReader jdbcReader) {
		assert jdbcReader instanceof IFacade;
		JDBCReader jdbcReaderTarget = (JDBCReader)((IFacade)jdbcReader).getTarget();
		MetaDataDialect metadataDialect = jdbcReaderTarget.getMetaDataDialect();
		return facadeFactory.createDatabaseCollector(
				new DefaultDatabaseCollector(metadataDialect));
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
		target.setPrimaryKey(new PrimaryKey());
		return facadeFactory.createTable(target);
	}

	@Override
	public IColumn newColumn(String string) {
		return facadeFactory.createColumn(new Column(string));
	}

	@Override
	public String newDialect(Properties properties, Connection connection) {
		DialectFactoryImpl dialectFactory = new DialectFactoryImpl();
		dialectFactory.setClassLoaderService(new ClassLoaderServiceImpl());
		dialectFactory.setDialectResolver(new StandardDialectResolver());
		Dialect dialect = dialectFactory.buildDialect(properties, connection);
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
		return facadeFactory.createValue(new SimpleValue(null));
	}

	@Override
	public IValue newPrimitiveArray(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(new PrimitiveArray(null, (PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newArray(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(new Array(null, (PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newBag(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(new Bag(null, (PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newList(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(new org.hibernate.mapping.List(null, (PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newMap(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(new org.hibernate.mapping.Map(null, (PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newSet(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(new Set(null, (PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newManyToOne(ITable table) {
		assert table instanceof IFacade;
		return facadeFactory.createValue(new ManyToOne(null, (Table)((IFacade)table).getTarget()));
	}

	@Override
	public IValue newOneToMany(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(new OneToMany(null, (PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newOneToOne(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(new OneToOne(null, ((PersistentClass)((IFacade)persistentClass).getTarget()).getTable(), (PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IPersistentClass newSingleTableSubclass(
			IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createPersistentClass(new SingleTableSubclass((PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IPersistentClass newJoinedSubclass(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createPersistentClass(new JoinedSubclass((PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IPersistentClass newSpecialRootClass(IProperty ormElement) {
		return facadeFactory.createSpecialRootClass(ormElement);
	}

	@Override
	public IPersistentClass newRootClass() {
		return facadeFactory.createPersistentClass(new RootClass());
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
		return ServiceImpl.class.getClassLoader();
	}

}
