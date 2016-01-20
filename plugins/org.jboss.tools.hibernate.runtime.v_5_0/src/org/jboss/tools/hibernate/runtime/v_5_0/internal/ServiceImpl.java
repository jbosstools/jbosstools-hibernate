package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.Hibernate;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JPAConfiguration;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Table;
import org.hibernate.proxy.HibernateProxyHelper;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.util.MetadataHelper;
import org.jboss.tools.hibernate.runtime.common.AbstractPersistentClassFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractService;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
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
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ISettings;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.ITableIdentifier;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.xml.sax.EntityResolver;

public class ServiceImpl extends AbstractService {

	private static final String HIBERNATE_VERSION = "5.0";
	
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
		IConfiguration result = null;
		EntityManagerFactoryBuilderImpl entityManagerFactoryBuilder = 
			HibernateToolsPersistenceProvider
				.createEntityManagerFactoryBuilder(
						persistenceUnit, 
						overrides);
		if (entityManagerFactoryBuilder != null) {
			result = facadeFactory.createConfiguration(
					new JPAConfiguration(
							entityManagerFactoryBuilder));
		}
		return result;
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
			Configuration configuration = (Configuration)((IFacade)hcfg).getTarget();
			MetadataImplementor metadata = (MetadataImplementor)MetadataHelper.getMetadata(configuration);
			SchemaExport schemaExport = new SchemaExport(metadata);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExporter createExporter(String exporterClassName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IArtifactCollector newArtifactCollector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IHQLQueryPlan newHQLQueryPlan(String query, boolean shallow, ISessionFactory sessionFactory) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeFactory newTypeFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INamingStrategy newNamingStrategy(String strategyClassName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IOverrideRepository newOverrideRepository() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITableFilter newTableFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IReverseEngineeringSettings newReverseEngineeringSettings(IReverseEngineeringStrategy res) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IReverseEngineeringStrategy newDefaultReverseEngineeringStrategy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJDBCReader newJDBCReader(IConfiguration configuration, ISettings settings,
			IReverseEngineeringStrategy strategy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IReverseEngineeringStrategy newReverseEngineeringStrategy(String strategyName,
			IReverseEngineeringStrategy delegate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReverseEngineeringStrategyClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDatabaseCollector newDatabaseCollector(IMetaDataDialect metaDataDialect) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICfg2HbmTool newCfg2HbmTool() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProperty newProperty() {
		return facadeFactory.createProperty(new Property());
	}

	@Override
	public ITable newTable(String name) {
		return facadeFactory.createTable(new Table(name));
	}

	@Override
	public IColumn newColumn(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDialect newDialect(Properties properties, Connection connection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getDriverManagerConnectionProviderClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEnvironment getEnvironment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue newSimpleValue() {
		// TODO Auto-generated method stub
		return null;
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
		assert table instanceof IFacade;
		return facadeFactory.createValue(
				new ManyToOne(
						null, 
						(Table)((IFacade)table).getTarget()));
	}

	@Override
	public IValue newOneToMany(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(
				new OneToMany(
						null, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newOneToOne(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		return facadeFactory.createValue(
				new OneToOne(
						null, 
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
	public IPrimaryKey newPrimaryKey() {
		return facadeFactory.createPrimaryKey(new PrimaryKey(null));
	}

	@Override
	public IHibernateMappingGlobalSettings newHibernateMappingGlobalSettings() {
		return facadeFactory.createHibernateMappingGlobalSettings(
				new HibernateMappingGlobalSettings());
	}

	@Override
	public ITableIdentifier createTableIdentifier(ITable table) {
		assert table instanceof IFacade;
		return facadeFactory.createTableIdentifier(
				TableIdentifier.create(
						(Table)((IFacade)table).getTarget()));
	}

	@Override
	public ITableIdentifier newTableIdentifier(String catalog, String schema, String name) {
		return facadeFactory.createTableIdentifier(
				new TableIdentifier(catalog, schema, name));
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

}
