package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.Hibernate;
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
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.Table;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.reveng.RevengDialect;
import org.hibernate.tool.api.reveng.RevengDialectFactory;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.reveng.RevengMetadataCollector;
import org.hibernate.tool.internal.reveng.reader.DatabaseReader;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.util.DummyMetadataDescriptor;
import org.hibernate.tool.orm.jbt.util.JpaMappingFileHelper;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.ConfigurationMetadataDescriptor;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
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
import org.xml.sax.EntityResolver;

public class ServiceImpl extends AbstractService {

	private static final String HIBERNATE_VERSION = "6.1";
	
	private IFacadeFactory facadeFactory = new FacadeFactoryImpl();
	private NewFacadeFactory newFacadeFactory = NewFacadeFactory.INSTANCE;

	@Override
	public IConfiguration newAnnotationConfiguration() {
		return newDefaultConfiguration();
	}

	@Override
	public IConfiguration newJpaConfiguration(
			String entityResolver, 
			String persistenceUnit,
			Map<Object, Object> overrides) {
		return newFacadeFactory.createJpaConfiguration(persistenceUnit, overrides);
	}

	@Override
	public IConfiguration newDefaultConfiguration() {
		getUsageTracker().trackNewConfigurationEvent(HIBERNATE_VERSION);
		return newFacadeFactory.createNativeConfiguration();
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
		if (hcfg instanceof IConfiguration) {
			result = facadeFactory.createHQLCodeAssist(
					new HQLCodeAssist(MetadataHelper.getMetadata((Configuration)((IFacade)hcfg).getTarget())));
		}
		return result;
	}

	@Override
	public IConfiguration newJDBCMetaDataConfiguration() {
		return newFacadeFactory.createRevengConfiguration();
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
					new ConfigurationMetadataDescriptor(newDefaultConfiguration()));
		}
		return facadeFactory.createExporter(exporter);
	}

	@Override
	public IArtifactCollector newArtifactCollector() {
		return newFacadeFactory.createArtifactCollector();
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
		return newFacadeFactory.createNamingStrategy(strategyClassName);
	}

	@Override
	public IOverrideRepository newOverrideRepository() {
		return newFacadeFactory.createOverrideRepository();
	}

	@Override
	public ITableFilter newTableFilter() {
		return facadeFactory.createTableFilter(new TableFilter());
	}


	@Override
	public IReverseEngineeringSettings newReverseEngineeringSettings(
			IReverseEngineeringStrategy res) {
		return newFacadeFactory.createReverseEngineeringSettings(((IFacade)res).getTarget());
	}

	@Override
	public IReverseEngineeringStrategy newDefaultReverseEngineeringStrategy() {
		return newFacadeFactory.createReverseEngineeringStrategy();
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
		return newFacadeFactory.createReverseEngineeringStrategy(
				strategyName, 
				((IFacade)delegate).getTarget());
	}

	@Override
	public String getReverseEngineeringStrategyClassName() {
		return RevengStrategy.class.getName();
	}

	@Override
	public ICfg2HbmTool newCfg2HbmTool() {
		return newFacadeFactory.createCfg2HbmTool();
	}

	@Override
	public IProperty newProperty() {
		return newFacadeFactory.createProperty();
	}

	@Override
	public ITable newTable(String name) {
		Table target = new Table("jboss tools", name);
		target.setPrimaryKey(new PrimaryKey(target));
		return facadeFactory.createTable(target);
	}

	@Override
	public IColumn newColumn(String string) {
		return newFacadeFactory.createColumn(string);
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
		return facadeFactory.createValue(
				new PrimitiveArray(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newArray(IPersistentClass persistentClass) {
		return facadeFactory.createValue(
				new Array(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newBag(IPersistentClass persistentClass) {
		return facadeFactory.createValue(
				new Bag(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}
	
	@Override
	public IValue newList(IPersistentClass persistentClass) {
		return facadeFactory.createValue(
				new org.hibernate.mapping.List(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newMap(IPersistentClass persistentClass) {
		return facadeFactory.createValue(
				new org.hibernate.mapping.Map(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newSet(IPersistentClass persistentClass) {
		return facadeFactory.createValue(
				new Set(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newManyToOne(ITable table) {
		return facadeFactory.createValue(
				new ManyToOne(
						DummyMetadataBuildingContext.INSTANCE, 
						(Table)((IFacade)table).getTarget()));
	}

	@Override
	public IValue newOneToMany(IPersistentClass persistentClass) {
		return facadeFactory.createValue(
				new OneToMany(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newOneToOne(IPersistentClass persistentClass) {
		return facadeFactory.createValue(
				new OneToOne(
						DummyMetadataBuildingContext.INSTANCE, 
						((PersistentClass)((IFacade)persistentClass).getTarget()).getTable(), 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IPersistentClass newSingleTableSubclass(IPersistentClass persistentClass) {
		return newFacadeFactory.createSingleTableSubclass(persistentClass);
	}

	@Override
	public IPersistentClass newJoinedSubclass(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		IPersistentClass result = facadeFactory.createPersistentClass(
				new JoinedSubclass(
						(PersistentClass)((IFacade)persistentClass).getTarget(),
						DummyMetadataBuildingContext.INSTANCE));
		((AbstractPersistentClassFacade)result).setSuperClass(persistentClass);
		return result;
	}

	@Override
	public IPersistentClass newSpecialRootClass(IProperty ormElement) {
		return facadeFactory.createSpecialRootClass(ormElement);
	}

	@Override
	public IPersistentClass newRootClass() {
		return facadeFactory.createPersistentClass(new RootClass(DummyMetadataBuildingContext.INSTANCE));
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
	protected String getCfgExporterClassName() {
		return CfgExporter.class.getName();
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
