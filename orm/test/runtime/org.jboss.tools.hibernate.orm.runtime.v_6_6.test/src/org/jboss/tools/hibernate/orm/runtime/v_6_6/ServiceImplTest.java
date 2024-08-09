package org.jboss.tools.hibernate.orm.runtime.v_6_6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.h2.Driver;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.export.java.JavaExporter;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.api.factory.WrapperFactory;
import org.hibernate.tool.orm.jbt.api.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.internal.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.internal.util.MetadataHelper;
import org.hibernate.tool.orm.jbt.internal.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.internal.util.MockDialect;
import org.hibernate.tool.orm.jbt.internal.util.RevengConfiguration;
import org.hibernate.tool.orm.jbt.internal.util.SpecialRootClass;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProgressListener;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServiceImplTest {

	private ServiceImpl service = null;
	
	@BeforeAll
	public static void beforeAll() throws Exception {
		DriverManager.registerDriver(new Driver());		
	}

	@BeforeEach
	public void beforeEach() {
		service = new ServiceImpl();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(service);
	}
	
	@Test
	public void testNewDefaultConfiguration() {
		IConfiguration defaultConfiguration = service.newDefaultConfiguration();
		assertNotNull(defaultConfiguration);
		Object target = ((IFacade)defaultConfiguration).getTarget();
		assertTrue(target instanceof Wrapper);
		target = ((Wrapper)target).getWrappedObject();
		assertTrue( target instanceof Configuration);
	}

	@Test
	public void testNewAnnotationConfiguration() {
		IConfiguration annotationConfiguration = service.newAnnotationConfiguration();
		assertNotNull(annotationConfiguration);
		Object target = ((IFacade)annotationConfiguration).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		target = ((Wrapper)target).getWrappedObject();
		assertNotNull(target);
		assertTrue(target instanceof Configuration);
	}

	@Test
	public void testNewJpaConfiguration() {
		IConfiguration jpaConfiguration = service.newJpaConfiguration(null, "test", null);
		assertNotNull(jpaConfiguration);
		Object wrapper = ((IFacade)jpaConfiguration).getTarget();
		assertNotNull(wrapper);
		assertTrue(wrapper instanceof Wrapper);
		Object target = ((Wrapper)wrapper).getWrappedObject();
		assertTrue(target instanceof JpaConfiguration);
		assertEquals("test", ((JpaConfiguration)target).getPersistenceUnit());
		
	}
	
	@Test
	public void testNewHibernateMappingExporter() {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		File file = new File("");
		IHibernateMappingExporter hibernateMappingExporter = 
				service.newHibernateMappingExporter(configuration, file);
		HbmExporter hmee = 
				(HbmExporter)((Wrapper)((IFacade)hibernateMappingExporter).getTarget()).getWrappedObject();
		assertSame(file, hmee.getProperties().get(ExporterConstants.OUTPUT_FILE_NAME));
		assertSame(
				MetadataHelper.getMetadata((Configuration)((Wrapper)((IFacade)configuration).getTarget()).getWrappedObject()),
				hmee.getMetadata());
	}
	
	@Test
	public void testNewSchemaExport() {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		ISchemaExport schemaExport = service.newSchemaExport(configuration);
		assertNotNull(schemaExport);
	}
	
	@Test
	public void testNewHQLCodeAssist() {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		IHQLCodeAssist hqlCodeAssist = service.newHQLCodeAssist(configuration);
		assertNotNull(hqlCodeAssist);
	}
	
	@Test
	public void testNewJDBCMetaDataConfiguration() {
		IConfiguration configuration = service.newJDBCMetaDataConfiguration();
		assertNotNull(configuration);
		Object target = ((IFacade)configuration).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		target = ((Wrapper)target).getWrappedObject();
		assertTrue(target instanceof RevengConfiguration);
	}
	
	@Test
	public void testCreateExporter() {
		IExporter exporter = service.createExporter(JavaExporter.class.getName());
		assertNotNull(exporter);
		Object exporterWrapper = ((IFacade)exporter).getTarget();
		assertNotNull(exporterWrapper);
		Exporter wrappedExporter = (Exporter)((Wrapper)exporterWrapper).getWrappedObject();
		assertTrue(wrappedExporter instanceof JavaExporter);
		MetadataDescriptor metadataDescriptor = 
				(MetadataDescriptor)((JavaExporter)wrappedExporter)
					.getProperties()
					.get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor.getProperties()); // Normal metadata descriptor
		exporter = service.createExporter(CfgExporter.class.getName());
		assertNotNull(exporter);
		exporterWrapper = ((IFacade)exporter).getTarget();
		assertNotNull(exporterWrapper);
		wrappedExporter = (Exporter)((Wrapper)exporterWrapper).getWrappedObject();
		assertTrue(wrappedExporter instanceof CfgExporter);
		metadataDescriptor = 
				(MetadataDescriptor)((CfgExporter)wrappedExporter)
					.getProperties()
					.get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNull(metadataDescriptor.getProperties()); // Dummy metadata descriptor
	}
	
	@Test
	public void testCreateCfgExporter() {
		IExporter exporter = service.createCfgExporter();
		assertNotNull(exporter);
		Object exporterWrapper = ((IFacade)exporter).getTarget();
		assertNotNull(exporterWrapper);
		Exporter wrappedExporter = (Exporter)((Wrapper)exporterWrapper).getWrappedObject();
		assertTrue(wrappedExporter instanceof CfgExporter);
	}
	
	@Test
	public void testNewArtifactCollector() {
		IArtifactCollector artifactCollector = service.newArtifactCollector();
		assertNotNull(artifactCollector);
		Object target = ((IFacade)artifactCollector).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		Object wrappedObject = ((Wrapper)target).getWrappedObject();
		assertTrue(wrappedObject instanceof ArtifactCollector);
	}
	
	@Test 
	public void testNewTypeFactory() {
		ITypeFactory typeFactory = service.newTypeFactory();
		assertNotNull(typeFactory);
	}
	
	@Test
	public void testNewNamingStrategy() {
		String strategyClassName = DefaultNamingStrategy.class.getName();
		INamingStrategy namingStrategy = service.newNamingStrategy(strategyClassName);
		assertNotNull(namingStrategy);
		Object target = ((IFacade)namingStrategy).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		target = ((Wrapper)target).getWrappedObject();
		assertNotNull(target);
		assertTrue(NamingStrategy.class.isAssignableFrom(target.getClass()));
		namingStrategy = null;
		assertNull(namingStrategy);
		try {
			namingStrategy = service.newNamingStrategy("some unexistent class");
		} catch (Throwable t) {
			assertTrue(t.getMessage().contains(
					"Exception while looking up class 'some unexistent class'"));
		}
		assertNull(namingStrategy);
	}
	
	@Test
	public void testNewOverrideRepository() {
		IOverrideRepository overrideRepository = service.newOverrideRepository();
		assertNotNull(overrideRepository);
		Object target = ((IFacade)overrideRepository).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		target = ((Wrapper)target).getWrappedObject();
		assertNotNull(target);
		assertTrue(target instanceof OverrideRepository);
	}
	
	@Test
	public void testNewTableFilter() {
		ITableFilter tableFilter = service.newTableFilter();
		assertNotNull(tableFilter);
		Object target = ((IFacade)tableFilter).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		target = ((Wrapper)target).getWrappedObject();
		assertTrue(target instanceof TableFilter);
	}
	
	@Test
	public void testNewDefaultReverseEngineeringStrategy() throws Exception {
		IReverseEngineeringStrategy reverseEngineeringStrategy = 
				service.newDefaultReverseEngineeringStrategy();
		assertNotNull(reverseEngineeringStrategy);
		Object target = ((IFacade)reverseEngineeringStrategy).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		target = ((Wrapper)target).getWrappedObject();
		assertNotNull(target);
		assertTrue(target instanceof DefaultStrategy);
	}
	
	@Test
	public void testNewReverseEngineeringSettings() {
		IReverseEngineeringStrategy strategy = 
				service.newDefaultReverseEngineeringStrategy();
		IReverseEngineeringSettings reverseEngineeringSettings = 
				service.newReverseEngineeringSettings(strategy);
		assertNotNull(reverseEngineeringSettings);
		Object target = ((IFacade)reverseEngineeringSettings).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		target = ((Wrapper)target).getWrappedObject();
		assertTrue(target instanceof RevengSettings);
	}
	
	@Test
	public void testCollectDatabaseTables() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		Properties properties = new Properties();
		properties.put("hibernate.connection.url", "jdbc:h2:mem:test");
		java.util.Map<String, List<ITable>> tableMap = service.collectDatabaseTables(
				properties, 
				service.newDefaultReverseEngineeringStrategy(),
				new IProgressListener() {				
					@Override public void startSubTask(String name) {}
				});
		assertEquals(1, tableMap.size());
		List<ITable> tables = tableMap.get("TEST.PUBLIC");
		assertEquals(1, tables.size());
		ITable table = tables.get(0);
		assertEquals("TEST", table.getCatalog());
		assertEquals("PUBLIC", table.getSchema());
		assertEquals("FOO", table.getName());
		assertTrue(table.getColumnIterator().hasNext());
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
	}
	
	@Test
	public void testNewReverseEngineeringStrategy() throws Exception {
		IReverseEngineeringStrategy defaultStrategy = 
				service.newDefaultReverseEngineeringStrategy();
		IReverseEngineeringStrategy newStrategy = 
				service.newReverseEngineeringStrategy(DefaultStrategy.class.getName(), 
						defaultStrategy);
		assertNotNull(newStrategy);
		Object target = ((IFacade)newStrategy).getTarget();
		assertNotNull(target);
		assertFalse(target instanceof DelegatingStrategy);
		newStrategy = service.newReverseEngineeringStrategy(
				DelegatingStrategy.class.getName(), 
				defaultStrategy);
		assertNotNull(newStrategy);
		target = ((IFacade)newStrategy).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		target = ((Wrapper)target).getWrappedObject();
		assertTrue(target instanceof DelegatingStrategy);
	}
	
	@Test
	public void testGetReverseEngineeringStrategyClassName() {
		assertEquals(
				RevengStrategy.class.getName(), 
				service.getReverseEngineeringStrategyClassName());
	}
	
	@Test
	public void testNewCfg2HbmTool() {
		ICfg2HbmTool cfg2HbmTool = service.newCfg2HbmTool();
		assertNotNull(cfg2HbmTool);
		Object target = ((IFacade)cfg2HbmTool).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		Object wrappedObject = ((Wrapper)target).getWrappedObject();
		assertTrue(wrappedObject instanceof Cfg2HbmTool);
	}
	
	@Test
	public void testNewProperty() {
		IProperty propertyFacade = service.newProperty();
		assertNotNull(propertyFacade);
		Object propertyWrapper = ((IFacade)propertyFacade).getTarget();
		assertNotNull(propertyWrapper);
		assertTrue(propertyWrapper instanceof Wrapper);
		Object propertyTarget = ((Wrapper)propertyWrapper).getWrappedObject();
		assertNotNull(propertyWrapper);
		assertTrue(propertyTarget instanceof Property);
	}
	
	@Test
	public void testNewTable() {
		ITable table = service.newTable("foo");
		assertNotNull(table);
		Object target = ((IFacade)table).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		target = ((Wrapper)target).getWrappedObject();
		assertNotNull(target);
		assertTrue(target instanceof Table);
		assertEquals("foo", ((Table)target).getName());
		assertNotNull(((Table)target).getPrimaryKey());
	}
	
	@Test
	public void testNewColumn() {
		IColumn column = service.newColumn("foo");
		assertNotNull(column);
		Object target = ((IFacade)column).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof ColumnWrapper);
		assertEquals("foo", ((ColumnWrapper)target).getName());
	}
	
	@Test
	public void testNewDialect() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:");
		String dialect = service.newDialect(new Properties(), connection);
		assertTrue(dialect.contains("org.hibernate.dialect.H2Dialect"));
	}

	@Test
	public void testGetDriverManagerManagerConnectionProviderClass() {
		assertSame(
				DriverManagerConnectionProviderImpl.class, 
				service.getDriverManagerConnectionProviderClass());
	}
	
	@Test
	public void testGetEnvironment() {
		IEnvironment environment = service.getEnvironment();
		assertNotNull(environment);
		assertEquals(
				environment.getTransactionManagerStrategy(), 
				Environment.TRANSACTION_COORDINATOR_STRATEGY);
	}
	
	@Test
	public void testSimpleValue() {
		IValue simpleValue = service.newSimpleValue();
		assertNotNull(simpleValue);
		Object simpleValueWrapper = ((IFacade)simpleValue).getTarget();
		assertNotNull(simpleValueWrapper);
		assertTrue(simpleValueWrapper instanceof Wrapper);
		assertTrue(((Wrapper)simpleValueWrapper).getWrappedObject() instanceof SimpleValue);
	}
	
	@Test
	public void testNewRootClass() {
		IPersistentClass rootClass = service.newRootClass();
		assertNotNull(rootClass);
		Object target = ((IFacade)rootClass).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		assertTrue(((Wrapper)target).getWrappedObject() instanceof RootClass);
	}
	
	@Test
	public void testNewPrimitiveArray() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue primitiveArray = service.newPrimitiveArray(persistentClass);
		assertNotNull(primitiveArray);
		Object primitiveArrayWrapper = ((IFacade)primitiveArray).getTarget();
		assertNotNull(primitiveArrayWrapper);
		assertTrue(primitiveArrayWrapper instanceof Wrapper);
		assertTrue(((Wrapper)primitiveArrayWrapper).getWrappedObject() instanceof PrimitiveArray);
	}
	
	@Test
	public void testNewArray() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue arrayFacade = service.newArray(persistentClass);
		assertNotNull(arrayFacade);
		Object arrayWrapper = ((IFacade)arrayFacade).getTarget();
		assertNotNull(arrayWrapper);
		assertTrue(arrayWrapper instanceof Wrapper);
		assertTrue(((Wrapper)arrayWrapper).getWrappedObject() instanceof Array);
	}
	
	@Test
	public void testNewBag() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue bagFacade = service.newBag(persistentClass);
		assertNotNull(bagFacade);
		Object bagWrapper = ((IFacade)bagFacade).getTarget();
		assertNotNull(bagWrapper);
		assertTrue(bagWrapper instanceof Wrapper);
		assertTrue(((Wrapper)bagWrapper).getWrappedObject() instanceof Bag);
	}
	
	@Test
	public void testNewList() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue listFacade = service.newList(persistentClass);
		assertNotNull(listFacade);
		Object listWrapper = ((IFacade)listFacade).getTarget();
		assertNotNull(listWrapper);
		assertTrue(listWrapper instanceof Wrapper);
		assertTrue(((Wrapper)listWrapper).getWrappedObject() instanceof org.hibernate.mapping.List);
	}
	
	@Test
	public void testNewMap() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue map = service.newMap(persistentClass);
		assertNotNull(map);
		Object mapWrapper = ((IFacade)map).getTarget();
		assertNotNull(mapWrapper);
		assertTrue(mapWrapper instanceof Wrapper);
		assertTrue(((Wrapper)mapWrapper).getWrappedObject() instanceof Map);
	}
	
 	@Test
	public void testNewSet() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue set = service.newSet(persistentClass);
		assertNotNull(set);
		Object setWrapper = ((IFacade)set).getTarget();
		assertNotNull(setWrapper);
		assertTrue(setWrapper instanceof Wrapper);
		assertTrue(((Wrapper)setWrapper).getWrappedObject() instanceof Set);
	}
	
	@Test
	public void testNewManyToOne() {
		ITable table = service.newTable("foo");
		IValue manyToOne = service.newManyToOne(table);
		assertNotNull(manyToOne);
		Object manyToOneWrapper = ((IFacade)manyToOne).getTarget();
		assertNotNull(manyToOneWrapper);
		assertTrue(manyToOneWrapper instanceof Wrapper);
		assertTrue(((Wrapper)manyToOneWrapper).getWrappedObject() instanceof ManyToOne);
	}
	
	@Test
	public void testNewOneToMany() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue oneToMany = service.newOneToMany(persistentClass);
		assertNotNull(oneToMany);
		Object oneToManyWrapper = ((IFacade)oneToMany).getTarget();
		assertNotNull(oneToManyWrapper);
		assertTrue(oneToManyWrapper instanceof Wrapper);
		assertTrue(((Wrapper)oneToManyWrapper).getWrappedObject() instanceof OneToMany);
	}
	
	@Test
	public void testNewOneToOne() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue oneToOne = service.newOneToOne(persistentClass);
		assertNotNull(oneToOne);
		Object oneToOneWrapper = ((IFacade)oneToOne).getTarget();
		assertNotNull(oneToOneWrapper);
		assertTrue(oneToOneWrapper instanceof Wrapper);
		assertTrue(((Wrapper)oneToOneWrapper).getWrappedObject() instanceof OneToOne);
	}
	
	@Test
	public void testNewSingleTableSubclass() {
		IPersistentClass persistentClass =(IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
		IPersistentClass singleTableSublass = service.newSingleTableSubclass(persistentClass);
		assertNotNull(singleTableSublass);
		Object target = ((IFacade)singleTableSublass).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		assertTrue(((Wrapper)target).getWrappedObject() instanceof SingleTableSubclass);
		assertEquals(persistentClass, singleTableSublass.getSuperclass());
		assertSame(
				((Wrapper)((IFacade)persistentClass).getTarget()).getWrappedObject(), 
				((SingleTableSubclass)((Wrapper)target).getWrappedObject()).getSuperclass());
	}
	
	@Test
	public void testNewJoinedSubclass() {
		IPersistentClass persistentClass = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
		IPersistentClass joinedSubclass = service.newJoinedSubclass(persistentClass);
		assertNotNull(joinedSubclass);
		Object target = ((IFacade)joinedSubclass).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		assertTrue(((Wrapper)target).getWrappedObject() instanceof JoinedSubclass);
		assertEquals(persistentClass, joinedSubclass.getSuperclass());
		assertSame(
				((Wrapper)((IFacade)persistentClass).getTarget()).getWrappedObject(), 
				((JoinedSubclass)((Wrapper)target).getWrappedObject()).getSuperclass());
	}
	
	@Test
	public void testNewSpecialRootClass() {
		IProperty property = service.newProperty();
		IPersistentClass pc = service.newRootClass();
		property.setPersistentClass(pc);
		IPersistentClass specialRootClass = service.newSpecialRootClass(property);
		assertNotNull(specialRootClass);
		Object target = ((IFacade)specialRootClass).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Wrapper);
		assertTrue(((Wrapper)target).getWrappedObject() instanceof SpecialRootClass);
		assertEquals(
				((Wrapper)((IFacade)property).getTarget()).getWrappedObject(), 
				((Wrapper)((IFacade)specialRootClass.getProperty()).getTarget()).getWrappedObject());
	}
	
	@Test
	public void testIsInitialized() {
		assertTrue(service.isInitialized(new Object()));
	}
	
	@Test
	public void testGetJPAMappingFilePaths() {
		List<String> result = service.getJPAMappingFilePaths("test", null);
		assertEquals(0, result.size());
	}
	
	@Test
	public void testGetClassWithoutInitializingProxy() {
		assertSame(
				Object.class, 
				service.getClassWithoutInitializingProxy(new Object()));
	}
	
	@Test
	public void testGetClassLoader(){
		assertSame(
				ServiceImpl.class.getClassLoader(), 
				service.getClassLoader());
	}
	
}
