package org.jboss.tools.hibernate.runtime.v_6_1.internal;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.boot.internal.BootstrapContextImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfoSource;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.reveng.RevengDialect;
import org.hibernate.tool.api.reveng.RevengDialectFactory;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.reveng.RevengMetadataCollector;
import org.hibernate.tool.internal.reveng.reader.DatabaseReader;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.jboss.tools.hibernate.runtime.common.AbstractService;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.Util;
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
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.ConfigurationMetadataDescriptor;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.DummyMetadataBuildingContext;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.DummyMetadataDescriptor;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.JdbcMetadataConfiguration;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.JpaConfiguration;
import org.xml.sax.EntityResolver;

public class ServiceImpl extends AbstractService {

	private static final String HIBERNATE_VERSION = "6.1";
	
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
		SchemaExportFacadeImpl result = 
			(SchemaExportFacadeImpl)facadeFactory.createSchemaExport(new SchemaExport());
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
		if (CfgExporter.class.isAssignableFrom(exporter.getClass())) {
			exporter.getProperties().put(
					ExporterConstants.METADATA_DESCRIPTOR, 
					new DummyMetadataDescriptor());
		} else {
			exporter.getProperties().put(
					ExporterConstants.METADATA_DESCRIPTOR,
					new ConfigurationMetadataDescriptor((ConfigurationFacadeImpl)newDefaultConfiguration()));
		}
		return facadeFactory.createExporter(exporter);
	}

	@Override
	public IArtifactCollector newArtifactCollector() {
		return facadeFactory.createArtifactCollector(new DefaultArtifactCollector());
	}

	@Override
	public IHQLQueryPlan newHQLQueryPlan(String query, boolean shallow, ISessionFactory sessionFactory) {
		// TODO Auto-generated method stub
		return null;
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
					Class.forName(strategyClassName).getDeclaredConstructor().newInstance());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
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
		return facadeFactory.createReverseEngineeringSettings(
				new RevengSettings(
						(RevengStrategy)((IFacade)res).getTarget()));
	}

	@Override
	public IReverseEngineeringStrategy newDefaultReverseEngineeringStrategy() {
		return facadeFactory.createReverseEngineeringStrategy(
				new DefaultStrategy());
	}

	@Override
	public Map<String, List<ITable>> collectDatabaseTables(
			Properties properties, 
			IReverseEngineeringStrategy strategy,
			final IProgressListener progressListener) {
		StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(properties)
				.build();
		MetadataBuildingOptionsImpl metadataBuildingOptions = 
				new MetadataBuildingOptionsImpl(serviceRegistry);	
		BootstrapContextImpl bootstrapContext = new BootstrapContextImpl(
				serviceRegistry, 
				metadataBuildingOptions);
		metadataBuildingOptions.setBootstrapContext(bootstrapContext);
		InFlightMetadataCollectorImpl metadataCollector = new InFlightMetadataCollectorImpl(
				bootstrapContext,
				metadataBuildingOptions);
		RevengDialect mdd = RevengDialectFactory
				.createMetaDataDialect(
						serviceRegistry.getService(JdbcServices.class).getDialect(), 
						properties );
		RevengStrategy revengStrategy = (RevengStrategy)((IFacade)strategy).getTarget();
	    DatabaseReader reader = DatabaseReader.create(properties,revengStrategy,mdd, serviceRegistry);
	    MetadataBuildingContext metadataBuildingContext = new MetadataBuildingContextRootImpl("JBoss Tools", bootstrapContext, metadataBuildingOptions, metadataCollector);
	    RevengMetadataCollector revengMetadataCollector = new RevengMetadataCollector(metadataBuildingContext);
		reader.readDatabaseSchema(revengMetadataCollector);
		Map<String, List<ITable>> result = new HashMap<String, List<ITable>>();
		for (Table table : revengMetadataCollector.getTables()) {
			String qualifier = "";
			if (table.getCatalog() != null) {
				qualifier += table.getCatalog();
			}
			if (table.getSchema() != null) {
				if (!"".equals(qualifier)) {
					qualifier += ".";
				}
				qualifier += table.getSchema();
			}
			List<ITable> list = result.get(qualifier);
			if (list == null) {
				list = new ArrayList<ITable>();
				result.put(qualifier, list);
			}
			list.add(facadeFactory.createTable(table));
		}
		
		return result;
	}
	
	@Override
	public IReverseEngineeringStrategy newReverseEngineeringStrategy(
			String strategyName,
			IReverseEngineeringStrategy delegate) {
		RevengStrategy delegateTarget = 
				(RevengStrategy)((IFacade)delegate).getTarget();
		Object target = 
				newReverseEngineeringStrategy(strategyName, delegateTarget);
		return facadeFactory.createReverseEngineeringStrategy(target);
	}

	@Override
	public String getReverseEngineeringStrategyClassName() {
		return RevengStrategy.class.getName();
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
		Table target = new Table("jboss tools", name);
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
		return facadeFactory.createEnvironment();
	}

	@Override
	public IValue newSimpleValue() {
		return facadeFactory.createValue(new BasicValue(DummyMetadataBuildingContext.INSTANCE));
	}

	@Override
	public IValue newPrimitiveArray(IPersistentClass persistentClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue newArray(IPersistentClass persistentClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue newBag(IPersistentClass persistentClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue newList(IPersistentClass persistentClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue newMap(IPersistentClass persistentClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue newSet(IPersistentClass persistentClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue newManyToOne(ITable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue newOneToMany(IPersistentClass persistentClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue newOneToOne(IPersistentClass persistentClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPersistentClass newSingleTableSubclass(IPersistentClass persistentClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPersistentClass newJoinedSubclass(IPersistentClass persistentClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPersistentClass newSpecialRootClass(IProperty ormElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPersistentClass newRootClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInitialized(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getJPAMappingFilePaths(String persistenceUnitName, EntityResolver entityResolver) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getClassWithoutInitializingProxy(Object reflectedObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object newReverseEngineeringStrategy(final String className, Object delegate) {
        try {
            Class<?> clazz = classForName(className);
			Constructor<?> constructor = 
					clazz.getConstructor(
							new Class[] { RevengStrategy.class });
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
					return clazz.getDeclaredConstructor().newInstance();
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

	private Class<?> classForName(String name) throws ClassNotFoundException {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if ( classLoader != null ) {
				return classLoader.loadClass(name);
			}
		}
		catch ( Throwable ignore ) {
		}
		return Class.forName( name );
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
