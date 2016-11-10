package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DatabaseCollector;
import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
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
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.jboss.tools.hibernate.runtime.common.IFacade;
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
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.ITableIdentifier;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.Assert;
import org.junit.Test;

public class ServiceImplTest {
	
	static class Foo { 
		private int id; 
		public void setId(int id) { }
		public int getId() { return id; }
	}
	
	private static final String TEST_HBM_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_5_0.internal'>" +
			"  <class name='ServiceImplTest$Foo'>" + 
			"    <id name='id'/>" + 
			"  </class>" + 
			"</hibernate-mapping>";

	private ServiceImpl service = new ServiceImpl();
	
	@Test
	public void testServiceCreation() {
		Assert.assertNotNull(service);
	}
	
	@Test
	public void testNewAnnotationConfiguration() {
		IConfiguration configuration = service.newAnnotationConfiguration();
		Assert.assertNotNull(configuration);
		Object target = ((IFacade)configuration).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Configuration);
	}
	
	@Test
	public void testNewJpaConfiguration() {
		IConfiguration configuration = service.newJpaConfiguration(null, "test", null);
		Assert.assertNotNull(configuration);
		Object target = ((IFacade)configuration).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Configuration);
	}
	
	@Test
	public void testNewDefaultConfiguration() {
		IConfiguration configuration = service.newDefaultConfiguration();
		Assert.assertNotNull(configuration);
		Object target = ((IFacade)configuration).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Configuration);
	}
	
	@Test
	public void testNewHibernateMappingExporter() {
		IConfiguration configuration = service.newDefaultConfiguration();
		File file = new File("");
		IHibernateMappingExporter hibernateMappingExporter = 
				service.newHibernateMappingExporter(configuration, file);
		Configuration cfg = (Configuration)((IFacade)configuration).getTarget();
		HibernateMappingExporterExtension hmee = 
				(HibernateMappingExporterExtension)((IFacade)hibernateMappingExporter).getTarget();
		Assert.assertSame(file, hmee.getOutputDirectory());
		Assert.assertSame(cfg, hmee.getConfiguration());
	}
	
	@Test
	public void testNewSchemaExport() {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		ISchemaExport schemaExport = service.newSchemaExport(configuration);
		Assert.assertNotNull(schemaExport);
	}
	
	@Test
	public void testNewHQLCodeAssist() {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		IHQLCodeAssist hqlCodeAssist = service.newHQLCodeAssist(configuration);
		Assert.assertNotNull(hqlCodeAssist);
	}
	
	@Test
	public void testNewJDBCMetaDataConfiguration() {
		IConfiguration configuration = service.newJDBCMetaDataConfiguration();
		Assert.assertNotNull(configuration);
		Object target = ((IFacade)configuration).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof JDBCMetaDataConfiguration);
	}
	
	@Test
	public void testCreateExporter() {
		IExporter exporter = service.createExporter("org.hibernate.tool.hbm2x.POJOExporter");
		Assert.assertNotNull(exporter);
		Object target = ((IFacade)exporter).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof POJOExporter);
	}
	
	@Test
	public void testNewHQLQueryPlan() throws Exception {
		IConfiguration configuration = service.newDefaultConfiguration();
		File testFile = File.createTempFile("test", "tmp");
		testFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(testFile);
		fileWriter.write(TEST_HBM_STRING);
		fileWriter.close();
		configuration.addFile(testFile);
		ISessionFactory sfi = configuration.buildSessionFactory();
		IHQLQueryPlan queryPlan = service.newHQLQueryPlan("from ServiceImplTest$Foo", true, sfi);
		Assert.assertNotNull(queryPlan);
		Object target = ((IFacade)queryPlan).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof HQLQueryPlan);
	}
	
	@Test
	public void testNewArtifactCollector() {
		IArtifactCollector artifactCollector = service.newArtifactCollector();
		Assert.assertNotNull(artifactCollector);
		Object target = ((IFacade)artifactCollector).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof ArtifactCollector);
	}
	
	@Test
	public void testNewDefaultReverseEngineeringStrategy() throws Exception {
		IReverseEngineeringStrategy reverseEngineeringStrategy = 
				service.newDefaultReverseEngineeringStrategy();
		Assert.assertNotNull(reverseEngineeringStrategy);
		Object target = ((IFacade)reverseEngineeringStrategy).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof ReverseEngineeringStrategy);
	}
	
	@Test 
	public void testNewTypeFactory() {
		ITypeFactory typeFactory = service.newTypeFactory();
		Assert.assertNotNull(typeFactory);
	}
	
	@Test
	public void testNewNamingStrategy() {
		String strategyClassName = DefaultNamingStrategy.class.getName();
		INamingStrategy namingStrategy = service.newNamingStrategy(strategyClassName);
		Assert.assertNotNull(namingStrategy);
		Object target = ((IFacade)namingStrategy).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof DefaultNamingStrategy);
	}
	
	@Test
	public void testNewOverrideRepository() {
		IOverrideRepository overrideRepository = service.newOverrideRepository();
		Assert.assertNotNull(overrideRepository);
		Object target = ((IFacade)overrideRepository).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof OverrideRepository);
	}
	
	@Test
	public void testNewTableFilter() {
		ITableFilter tableFilter = service.newTableFilter();
		Assert.assertNotNull(tableFilter);
		Object target = ((IFacade)tableFilter).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof TableFilter);
	}
	
	@Test
	public void testNewReverseEngineeringSettings() {
		IReverseEngineeringStrategy strategy = 
				service.newDefaultReverseEngineeringStrategy();
		IReverseEngineeringSettings reverseEngineeringSettings = 
				service.newReverseEngineeringSettings(strategy);
		Assert.assertNotNull(reverseEngineeringSettings);
		Object target = ((IFacade)reverseEngineeringSettings).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof ReverseEngineeringSettings);
	}
	
	@Test
	public void testNewJDBCReader() {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		IReverseEngineeringStrategy strategy = service.newDefaultReverseEngineeringStrategy();
		IJDBCReader jdbcReader = service.newJDBCReader(
				configuration, 
				strategy);
		Assert.assertNotNull(jdbcReader);
		Object target = ((IFacade)jdbcReader).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof JDBCReader);
	}
	
	@Test
	public void testNewReverseEngineeringStrategy() throws Exception {
		String defaultRevEngStratClassName = 
				"org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy";
		IReverseEngineeringStrategy defaultStrategy = 
				service.newDefaultReverseEngineeringStrategy();
		IReverseEngineeringStrategy newStrategy = 
				service.newReverseEngineeringStrategy(
						defaultRevEngStratClassName, 
						defaultStrategy);
		Assert.assertNotNull(newStrategy);
		Object target = ((IFacade)newStrategy).getTarget();
		Assert.assertNotNull(target);
		Assert.assertFalse(target instanceof DelegatingReverseEngineeringStrategy);
		newStrategy = service.newReverseEngineeringStrategy(
				"org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy", 
				defaultStrategy);
		Assert.assertNotNull(newStrategy);
		target = ((IFacade)newStrategy).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof DelegatingReverseEngineeringStrategy);
	}
	
	@Test
	public void testGetReverseEngineeringStrategyClassName() {
		Assert.assertEquals(
				"org.hibernate.cfg.reveng.ReverseEngineeringStrategy", 
				service.getReverseEngineeringStrategyClassName());
	}
	
	@Test
	public void testNewDatabaseCollector() {
		IMetaDataDialect metaDataDialect = 
				(IMetaDataDialect)Proxy.newProxyInstance(
						service.getClassLoader(), 
						new Class[] { IMetaDataDialect.class, IFacade.class }, 
						new InvocationHandler() {				
							@Override
							public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
								return null;
							}
						});
		IDatabaseCollector databaseCollector = 
				service.newDatabaseCollector(metaDataDialect);
		Assert.assertNotNull(databaseCollector);
		Object target = ((IFacade)databaseCollector).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof DatabaseCollector);
	}
	
	@Test
	public void testNewCfg2HbmTool() {
		ICfg2HbmTool cfg2HbmTool = service.newCfg2HbmTool();
		Assert.assertNotNull(cfg2HbmTool);
		Object target = ((IFacade)cfg2HbmTool).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Cfg2HbmTool);
	}
	
	@Test
	public void testNewProperty() {
		IProperty property = service.newProperty();
		Assert.assertNotNull(property);
		Object target = ((IFacade)property).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Property);
	}
	
	@Test
	public void testNewTable() {
		ITable table = service.newTable("foo");
		Assert.assertNotNull(table);
		Object target = ((IFacade)table).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Table);
		Assert.assertEquals("foo", ((Table)target).getName());
	}
	
	@Test
	public void testNewColumn() {
		IColumn column = service.newColumn("foo");
		Assert.assertNotNull(column);
		Object target = ((IFacade)column).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Column);
		Assert.assertEquals("foo", ((Column)target).getName());
	}
	
	@Test
	public void testNewDialect() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:");
		IDialect dialect = service.newDialect(new Properties(), connection);
		Assert.assertNotNull(dialect);
		Object target = ((IFacade)dialect).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Dialect);
	}
	
	@Test
	public void testGetDriverManagerManagerConnectionProviderClass() {
		Assert.assertSame(
				DriverManagerConnectionProviderImpl.class, 
				service.getDriverManagerConnectionProviderClass());
	}
	
	@Test
	public void testGetEnvironment() {
		IEnvironment environment = service.getEnvironment();
		Assert.assertNotNull(environment);
		Assert.assertEquals(
				environment.getTransactionManagerStrategy(), 
				Environment.TRANSACTION_COORDINATOR_STRATEGY);
	}
	
	@Test
	public void testSimpleValue() {
		IValue simpleValue = service.newSimpleValue();
		Assert.assertNotNull(simpleValue);
		Object target = ((IFacade)simpleValue).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof SimpleValue);
	}
	
	@Test
	public void testNewPrimitiveArray() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue primitiveArray = service.newPrimitiveArray(persistentClass);
		Assert.assertNotNull(primitiveArray);
		Object target = ((IFacade)primitiveArray).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof PrimitiveArray);
	}
	
	@Test
	public void testNewArray() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue array = service.newArray(persistentClass);
		Assert.assertNotNull(array);
		Object target = ((IFacade)array).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Array);
	}
	
	@Test
	public void testNewBag() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue bag = service.newBag(persistentClass);
		Assert.assertNotNull(bag);
		Object target = ((IFacade)bag).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Bag);
	}
	
	@Test
	public void testNewList() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue list = service.newList(persistentClass);
		Assert.assertNotNull(list);
		Object target = ((IFacade)list).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof org.hibernate.mapping.List);
	}
	
	@Test
	public void testNewMap() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue map = service.newMap(persistentClass);
		Assert.assertNotNull(map);
		Object target = ((IFacade)map).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Map);
	}
	
	@Test
	public void testNewSet() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue set = service.newSet(persistentClass);
		Assert.assertNotNull(set);
		Object target = ((IFacade)set).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Set);
	}
	
	@Test
	public void testNewManyToOne() {
		ITable table = service.newTable("foo");
		IValue manyToOne = service.newManyToOne(table);
		Assert.assertNotNull(manyToOne);
		Object target = ((IFacade)manyToOne).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof ManyToOne);
	}
	
	@Test
	public void testNewOneToMany() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue oneToMany = service.newOneToMany(persistentClass);
		Assert.assertNotNull(oneToMany);
		Object target = ((IFacade)oneToMany).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof OneToMany);
	}
	
	@Test
	public void testNewOneToOne() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue oneToOne = service.newOneToOne(persistentClass);
		Assert.assertNotNull(oneToOne);
		Object target = ((IFacade)oneToOne).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof OneToOne);
	}
	
	@Test
	public void testNewSingleTableSubclass() {
		IPersistentClass persistentClass = service.newRootClass();
		IPersistentClass singleTableSublass = service.newSingleTableSubclass(persistentClass);
		Assert.assertNotNull(singleTableSublass);
		Object target = ((IFacade)singleTableSublass).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof SingleTableSubclass);
		Assert.assertSame(persistentClass, singleTableSublass.getSuperclass());
		Assert.assertSame(
				((IFacade)persistentClass).getTarget(), 
				((SingleTableSubclass)target).getSuperclass());
	}
	
	@Test
	public void testNewJoinedSubclass() {
		IPersistentClass persistentClass = service.newRootClass();
		IPersistentClass joinedSubclass = service.newJoinedSubclass(persistentClass);
		Assert.assertNotNull(joinedSubclass);
		Object target = ((IFacade)joinedSubclass).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof JoinedSubclass);
		Assert.assertSame(persistentClass, joinedSubclass.getSuperclass());
		Assert.assertSame(
				((IFacade)persistentClass).getTarget(), 
				((JoinedSubclass)target).getSuperclass());
	}
	
	@Test
	public void testNewSpecialRootClass() {
		IProperty property = service.newProperty();
		IPersistentClass pc = service.newRootClass();
		property.setPersistentClass(pc);
		IPersistentClass specialRootClass = service.newSpecialRootClass(property);
		Assert.assertNotNull(specialRootClass);
		Object target = ((IFacade)specialRootClass).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof RootClass);
		Assert.assertSame(property, specialRootClass.getProperty());
	}
	
	@Test
	public void testNewRootClass() {
		IPersistentClass rootClass = service.newRootClass();
		Assert.assertNotNull(rootClass);
		Object target = ((IFacade)rootClass).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof RootClass);
	}
	
	@Test
	public void testNewPrimaryKey() {
		IPrimaryKey primaryKey = service.newPrimaryKey();
		Assert.assertNotNull(primaryKey);
		Object target = ((IFacade)primaryKey).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof PrimaryKey);
	}
	
	@Test
	public void testCreateTableIdentifier() {
		ITable table = service.newTable("foo");
		ITableIdentifier tableIdentifier = service.createTableIdentifier(table);
		Assert.assertNotNull(tableIdentifier);
		Object target = ((IFacade)tableIdentifier).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof TableIdentifier);
	}
	
	@Test
	public void testNewTableIdentifier() {
		ITableIdentifier tableIdentifier = service.newTableIdentifier(
				"catalog", "schema", "typeName");
		Assert.assertNotNull(tableIdentifier);
		Object target = ((IFacade)tableIdentifier).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof TableIdentifier);
	}
	
	@Test
	public void testIsInitialized() {
		Assert.assertTrue(service.isInitialized(new Object()));
	}
	
	@Test
	public void testGetJPAMappingFilePaths() {
		List<String> result = service.getJPAMappingFilePaths("test", null);
		Assert.assertEquals(0, result.size());
	}
	
	@Test
	public void testGetClassWithoutInitializingProxy() {
		Assert.assertSame(
				Object.class, 
				service.getClassWithoutInitializingProxy(new Object()));
	}
	
	@Test
	public void testGetClassLoader(){
		Assert.assertSame(
				ServiceImpl.class.getClassLoader(), 
				service.getClassLoader());
	}
	
}
