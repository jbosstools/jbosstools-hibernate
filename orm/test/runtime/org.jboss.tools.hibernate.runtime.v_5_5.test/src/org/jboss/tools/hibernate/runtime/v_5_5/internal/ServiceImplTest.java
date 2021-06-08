package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.hbm2x.AbstractExporter;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.jboss.tools.hibernate.runtime.common.IFacade;
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
import org.jboss.tools.hibernate.runtime.v_5_5.internal.util.JdbcMetadataConfiguration;
import org.jboss.tools.hibernate.runtime.v_5_5.internal.util.JpaConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServiceImplTest {

	static class Foo { 
		private int id; 
		public void setId(int id) { }
		public int getId() { return id; }
	}
	
	private static final String TEST_HBM_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_5_5.internal'>" +
			"  <class name='ServiceImplTest$Foo'>" + 
			"    <id name='id'/>" + 
			"  </class>" + 
			"</hibernate-mapping>";

	private ServiceImpl service = null;
	
	@BeforeEach
	public void beforeEach() {
		service = new ServiceImpl();
	}
	
	@Test
	public void testNewAnnotationConfiguration() {
		IConfiguration annotationConfiguration = service.newAnnotationConfiguration();
		assertNotNull(annotationConfiguration);
		assertTrue(((IFacade)annotationConfiguration).getTarget() instanceof Configuration);
	}

	@Test
	public void testNewJpaConfiguration() {
		IConfiguration jpaConfiguration = service.newJpaConfiguration(null, "test", null);
		assertNotNull(jpaConfiguration);
		Object target = ((IFacade)jpaConfiguration).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof JpaConfiguration);
		assertEquals("test", ((JpaConfiguration)target).getPersistenceUnit());
		
	}
	
	@Test
	public void testNewDefaultConfiguration() {
		IConfiguration defaultConfiguration = service.newDefaultConfiguration();
		assertNotNull(defaultConfiguration);
		assertTrue(((IFacade)defaultConfiguration).getTarget() instanceof Configuration);
	}

	@Test
	public void testNewHibernateMappingExporter() {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
		File file = new File("");
		IHibernateMappingExporter hibernateMappingExporter = 
				service.newHibernateMappingExporter(configuration, file);
		HibernateMappingExporterExtension hmee = 
				(HibernateMappingExporterExtension)((IFacade)hibernateMappingExporter).getTarget();
		assertSame(file, hmee.getOutputDirectory());
		assertSame(
				((ConfigurationFacadeImpl)configuration).getMetadata(), 
				hmee.getMetadata());
	}
	
	@Test
	public void testNewSchemaExport() {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
		ISchemaExport schemaExport = service.newSchemaExport(configuration);
		assertNotNull(schemaExport);
	}
	
	@Test
	public void testNewHQLCodeAssist() {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
		IHQLCodeAssist hqlCodeAssist = service.newHQLCodeAssist(configuration);
		assertNotNull(hqlCodeAssist);
	}
	
	@Test
	public void testNewJDBCMetaDataConfiguration() {
		IConfiguration configuration = service.newJDBCMetaDataConfiguration();
		assertNotNull(configuration);
		Object target = ((IFacade)configuration).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof JdbcMetadataConfiguration);
	}
	
	@Test
	public void testCreateExporter() throws Exception {
		IExporter exporter = service.createExporter(POJOExporter.class.getName());
		assertNotNull(exporter);
		Object target = ((IFacade)exporter).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof POJOExporter);
		Field metadataDescriptorField = AbstractExporter.class.getDeclaredField("metadataDescriptor");
		metadataDescriptorField.setAccessible(true);
		MetadataDescriptor metadataDescriptor = (MetadataDescriptor)metadataDescriptorField.get(target);
		assertNotNull(metadataDescriptor.getProperties()); // Normal metadata descriptor
		exporter = service.createExporter(HibernateConfigurationExporter.class.getName());
		assertNotNull(exporter);
		target = ((IFacade)exporter).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof HibernateConfigurationExporter);
		metadataDescriptor = (MetadataDescriptor)metadataDescriptorField.get(target);
		assertNull(metadataDescriptor.getProperties()); // Dummy metadata descriptor
	}
	
	@Test
	public void testNewArtifactCollector() {
		IArtifactCollector artifactCollector = service.newArtifactCollector();
		assertNotNull(artifactCollector);
		Object target = ((IFacade)artifactCollector).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof ArtifactCollector);
	}
	
	@Test
	public void testNewHQLQueryPlan() throws Exception {
		IConfiguration configuration = service.newDefaultConfiguration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		File testFile = File.createTempFile("test", "tmp");
		testFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(testFile);
		fileWriter.write(TEST_HBM_STRING);
		fileWriter.close();
		configuration.addFile(testFile);
		ISessionFactory sfi = configuration.buildSessionFactory();
		IHQLQueryPlan queryPlan = service.newHQLQueryPlan("from ServiceImplTest$Foo", true, sfi);
		assertNotNull(queryPlan);
		Object target = ((IFacade)queryPlan).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof HQLQueryPlan);
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
		assertTrue(target instanceof DefaultNamingStrategy);
		assertNull(service.newNamingStrategy("some unexistant class"));
	}
	
	@Test
	public void testNewOverrideRepository() {
		IOverrideRepository overrideRepository = service.newOverrideRepository();
		assertNotNull(overrideRepository);
		Object target = ((IFacade)overrideRepository).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof OverrideRepository);
	}
	
	@Test
	public void testNewTableFilter() {
		ITableFilter tableFilter = service.newTableFilter();
		assertNotNull(tableFilter);
		Object target = ((IFacade)tableFilter).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof TableFilter);
	}
	
	@Test
	public void testNewDefaultReverseEngineeringStrategy() throws Exception {
		IReverseEngineeringStrategy reverseEngineeringStrategy = 
				service.newDefaultReverseEngineeringStrategy();
		assertNotNull(reverseEngineeringStrategy);
		Object target = ((IFacade)reverseEngineeringStrategy).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof DefaultReverseEngineeringStrategy);
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
		assertTrue(target instanceof ReverseEngineeringSettings);
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
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
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
		assertNotNull(newStrategy);
		Object target = ((IFacade)newStrategy).getTarget();
		assertNotNull(target);
		assertFalse(target instanceof DelegatingReverseEngineeringStrategy);
		newStrategy = service.newReverseEngineeringStrategy(
				"org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy", 
				defaultStrategy);
		assertNotNull(newStrategy);
		target = ((IFacade)newStrategy).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof DelegatingReverseEngineeringStrategy);
	}
	
	@Test
	public void testGetReverseEngineeringStrategyClassName() {
		assertEquals(
				"org.hibernate.cfg.reveng.ReverseEngineeringStrategy", 
				service.getReverseEngineeringStrategyClassName());
	}
	
	@Test
	public void testNewCfg2HbmTool() {
		ICfg2HbmTool cfg2HbmTool = service.newCfg2HbmTool();
		assertNotNull(cfg2HbmTool);
		Object target = ((IFacade)cfg2HbmTool).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Cfg2HbmTool);
	}
	
	@Test
	public void testNewProperty() {
		IProperty property = service.newProperty();
		assertNotNull(property);
		Object target = ((IFacade)property).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Property);
	}
	
	@Test
	public void testNewTable() {
		ITable table = service.newTable("foo");
		assertNotNull(table);
		Object target = ((IFacade)table).getTarget();
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
		assertTrue(target instanceof Column);
		assertEquals("foo", ((Column)target).getName());
	}
	
	@Test
	public void testNewDialect() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:");
		String dialect = service.newDialect(new Properties(), connection);
		assertEquals("org.hibernate.dialect.H2Dialect", dialect);
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
		Object target = ((IFacade)simpleValue).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof SimpleValue);
	}
	
	@Test
	public void testNewPrimitiveArray() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue primitiveArray = service.newPrimitiveArray(persistentClass);
		assertNotNull(primitiveArray);
		Object target = ((IFacade)primitiveArray).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof PrimitiveArray);
	}
	
	@Test
	public void testNewArray() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue array = service.newArray(persistentClass);
		assertNotNull(array);
		Object target = ((IFacade)array).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Array);
	}
	
	@Test
	public void testNewBag() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue bag = service.newBag(persistentClass);
		assertNotNull(bag);
		Object target = ((IFacade)bag).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Bag);
	}
	
	@Test
	public void testNewList() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue list = service.newList(persistentClass);
		assertNotNull(list);
		Object target = ((IFacade)list).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof org.hibernate.mapping.List);
	}
	
	@Test
	public void testNewMap() {
		IPersistentClass persistentClass = service.newRootClass();
		IValue map = service.newMap(persistentClass);
		assertNotNull(map);
		Object target = ((IFacade)map).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Map);
	}
	
	@Test
	public void testNewRootClass() {
		IPersistentClass rootClass = service.newRootClass();
		assertNotNull(rootClass);
		Object target = ((IFacade)rootClass).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof RootClass);
	}
	
	public static class TestDialect extends Dialect {}
	
}
