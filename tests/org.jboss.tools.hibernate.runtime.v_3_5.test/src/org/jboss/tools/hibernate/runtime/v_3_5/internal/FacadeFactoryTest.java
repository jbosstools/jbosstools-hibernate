package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.Mappings;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.DatabaseCollector;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.query.HQLQueryPlan;
import org.hibernate.hql.QueryTranslator;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;
import org.hibernate.tool.hbm2x.QueryExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.AbstractPropertyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingGlobalSettings;
import org.jboss.tools.hibernate.runtime.spi.IJDBCReader;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.IMapping;
import org.jboss.tools.hibernate.runtime.spi.IMappings;
import org.jboss.tools.hibernate.runtime.spi.IMetaDataDialect;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.jboss.tools.hibernate.runtime.spi.IQueryTranslator;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ISettings;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.ITableIdentifier;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class FacadeFactoryTest {
	
	private FacadeFactoryImpl facadeFactory;

	@Before
	public void setUp() throws Exception {
		facadeFactory = new FacadeFactoryImpl();
	}
	
	@Test
	public void testGetClassLoader() {
		Assert.assertSame(getClass().getClassLoader(), facadeFactory.getClassLoader());
	}
	
	@Test
	public void testCreateArtifactCollector() {
		ArtifactCollector artifactCollector = new ArtifactCollector();
		IArtifactCollector facade = facadeFactory.createArtifactCollector(artifactCollector);
		Assert.assertSame(artifactCollector, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateCfg2HbmTool() {
		Cfg2HbmTool cfg2HbmTool = new Cfg2HbmTool();
		ICfg2HbmTool facade = facadeFactory.createCfg2HbmTool(cfg2HbmTool);
		Assert.assertSame(cfg2HbmTool,  ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateNamingStrategy() {
		NamingStrategy namingStrategy = new DefaultNamingStrategy();
		INamingStrategy facade = facadeFactory.createNamingStrategy(namingStrategy);
		Assert.assertSame(namingStrategy, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateDialect() {
		Dialect dialect = new Dialect() {};
		IDialect facade = facadeFactory.createDialect(dialect);
		Assert.assertSame(dialect, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateMapping() {
		Mapping mapping = (Mapping)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Mapping.class }, 
				new TestInvocationHandler());
		IMapping facade = facadeFactory.createMapping(mapping);
		Assert.assertSame(mapping, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateReverseEngineeringSettings() {
		ReverseEngineeringSettings res = new ReverseEngineeringSettings(null);
		IReverseEngineeringSettings facade = facadeFactory.createReverseEngineeringSettings(res);
		Assert.assertSame(res, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateReverseEngineeringStrategy() {
		ReverseEngineeringStrategy res = (ReverseEngineeringStrategy)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { ReverseEngineeringStrategy.class }, 
				new TestInvocationHandler());
		IReverseEngineeringStrategy facade = facadeFactory.createReverseEngineeringStrategy(res);
		Assert.assertSame(res, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateOverrideRepository() {
		OverrideRepository overrideRepository = new OverrideRepository();
		IOverrideRepository facade = facadeFactory.createOverrideRepository(overrideRepository);
		Assert.assertSame(overrideRepository, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateSchemaExport() {
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		SchemaExport schemaExport = new SchemaExport(configuration);
		ISchemaExport facade = facadeFactory.createSchemaExport(schemaExport);
		Assert.assertSame(schemaExport, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateGenericExporter() {
		GenericExporter genericExporter = new GenericExporter();
		IGenericExporter facade = facadeFactory.createGenericExporter(genericExporter);
		Assert.assertSame(genericExporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateHbm2DDLExporter() {
		Hbm2DDLExporter hbm2ddlExporter = new Hbm2DDLExporter();
		IHbm2DDLExporter facade = facadeFactory.createHbm2DDLExporter(hbm2ddlExporter);
		Assert.assertSame(hbm2ddlExporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateQueryExporter() {
		QueryExporter queryExporter = new QueryExporter();
		IQueryExporter facade = facadeFactory.createQueryExporter(queryExporter);
		Assert.assertSame(queryExporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateTableFilter() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter facade = facadeFactory.createTableFilter(tableFilter);
		Assert.assertSame(tableFilter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateExporter() {
		Exporter exporter = (Exporter)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Exporter.class }, 
				new TestInvocationHandler());
		IExporter facade = facadeFactory.createExporter(exporter);
		Assert.assertSame(exporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateTableIdentifier() {
		TableIdentifier tableIdentifier = new TableIdentifier("foo");
		ITableIdentifier facade = facadeFactory.createTableIdentifier(tableIdentifier);
		Assert.assertSame(tableIdentifier, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateHibernateMappingGlobalSettings() {
		HibernateMappingGlobalSettings hibernateMappingGlobalSettings = new HibernateMappingGlobalSettings();
		IHibernateMappingGlobalSettings facade = facadeFactory.createHibernateMappingGlobalSettings(hibernateMappingGlobalSettings);
		Assert.assertSame(hibernateMappingGlobalSettings, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateMappings() {
		Mappings mappings = (Mappings)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Mappings.class }, 
				new TestInvocationHandler());
		IMappings facade = facadeFactory.createMappings(mappings);
		Assert.assertSame(mappings, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateClassMetadata() {
		ClassMetadata classMetadata = (ClassMetadata)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { ClassMetadata.class }, 
				new TestInvocationHandler());
		IClassMetadata facade = facadeFactory.createClassMetadata(classMetadata);
		Assert.assertSame(classMetadata, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateCollectionMetadata() {
		CollectionMetadata collectionMetadata = (CollectionMetadata)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { CollectionMetadata.class }, 
				new TestInvocationHandler());
		ICollectionMetadata facade = facadeFactory.createCollectionMetadata(collectionMetadata);
		Assert.assertSame(collectionMetadata, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateColumn() {
		Column column = new Column();
		IColumn facade = facadeFactory.createColumn(column);
		Assert.assertSame(column, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateConfiguration() {
		Configuration configuration = new Configuration();
		IConfiguration facade = facadeFactory.createConfiguration(configuration);
		Assert.assertSame(configuration, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateCriteria() {
		Criteria criteria = (Criteria)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Criteria.class }, 
				new TestInvocationHandler());
		ICriteria facade = facadeFactory.createCriteria(criteria);
		Assert.assertSame(criteria, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateDatabaseCollector() {
		DatabaseCollector databaseCollector = (DatabaseCollector)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { DatabaseCollector.class }, 
				new TestInvocationHandler());
		IDatabaseCollector facade = facadeFactory.createDatabaseCollector(databaseCollector);
		Assert.assertSame(databaseCollector, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateEntityMetamodel() {
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		SessionFactoryImplementor sfi = new SessionFactoryImpl(configuration, null, configuration.buildSettings(), null, null);
		RootClass rc = new RootClass();
		SimpleValue sv = new SimpleValue();
		sv.setNullValue("null");
		sv.setTypeName(Integer.class.getName());
		rc.setIdentifier(sv);
		EntityMetamodel entityMetamodel = new EntityMetamodel(rc, sfi);
		IEntityMetamodel facade = facadeFactory.createEntityMetamodel(entityMetamodel);
		Assert.assertSame(entityMetamodel, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateEnvironment() {
		Assert.assertNotNull(facadeFactory.createEnvironment());
	}
	
	@Test
	public void testCreateForeignKey() {
		ForeignKey foreignKey = new ForeignKey();
		IForeignKey facade = facadeFactory.createForeignKey(foreignKey);
		Assert.assertSame(foreignKey, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateHibernateMappingExporter() {
		HibernateMappingExporter hibernateMappingExporter = new HibernateMappingExporter();
		IHibernateMappingExporter facade = facadeFactory.createHibernateMappingExporter(hibernateMappingExporter);
		Assert.assertSame(hibernateMappingExporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateHQLCodeAssist() {
		HQLCodeAssist hqlCodeAssist = new HQLCodeAssist(null);
		IHQLCodeAssist facade = facadeFactory.createHQLCodeAssist(hqlCodeAssist);
		Assert.assertSame(hqlCodeAssist, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateHQLCompletionProposal() {
		HQLCompletionProposal hqlCompletionProposal = new HQLCompletionProposal(0, 0);
		IHQLCompletionProposal facade = facadeFactory.createHQLCompletionProposal(hqlCompletionProposal);
		Assert.assertSame(hqlCompletionProposal, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateHQLQueryPlan() {
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		Mappings mappings = configuration.createMappings();
		Table t = new Table("FOO");
		Column c = new Column("foo");
		t.addColumn(c);
		PrimaryKey key = new PrimaryKey();
		key.addColumn(c);
		t.setPrimaryKey(key);
		SimpleValue sv = new SimpleValue();
		sv.setNullValue("null");
		sv.setTypeName(Integer.class.getName());
		sv.setTable(t);
		sv.addColumn(c);
		RootClass rc  = new RootClass();
		rc.setEntityName("foo");
		rc.setIdentifier(sv);
		rc.setTable(t);
		mappings.addClass(rc);
		SessionFactoryImplementor sfi = new SessionFactoryImpl(configuration, null, configuration.buildSettings(), null, null);
		HQLQueryPlan hqlQueryPlan = new HQLQueryPlan("from foo", false, Collections.emptyMap(), sfi);
		IHQLQueryPlan facade = facadeFactory.createHQLQueryPlan(hqlQueryPlan);
		Assert.assertSame(hqlQueryPlan, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateJDBCReader() {
		JDBCReader jdbcReader = new JDBCReader(null, null, null, null, null, new DefaultReverseEngineeringStrategy());
		IJDBCReader facade = facadeFactory.createJDBCReader(jdbcReader);
		Assert.assertSame(jdbcReader, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateJoin() {
		Join join = new Join();
		IJoin facade = facadeFactory.createJoin(join);
		Assert.assertSame(join, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateMetaDataDialect() {
		MetaDataDialect metaDataDialect = (MetaDataDialect)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { MetaDataDialect.class }, 
				new TestInvocationHandler());
		IMetaDataDialect facade = facadeFactory.createMetaDataDialect(metaDataDialect);
		Assert.assertSame(metaDataDialect, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreatePersistentClass() {
		PersistentClass persistentClass = new RootClass();
		IPersistentClass facade = facadeFactory.createPersistentClass(persistentClass);
		Assert.assertSame(persistentClass, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreatePOJOClass() {
		POJOClass pojoClass = (POJOClass)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { POJOClass.class }, 
				new TestInvocationHandler());
		IPOJOClass facade = facadeFactory.createPOJOClass(pojoClass);
		Assert.assertSame(pojoClass, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreatePrimaryKey() {
		PrimaryKey primaryKey = new PrimaryKey();
		IPrimaryKey facade = facadeFactory.createPrimaryKey(primaryKey);
		Assert.assertSame(primaryKey, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateProperty() {
		Property property = new Property();
		IProperty facade = facadeFactory.createProperty(property);
		Assert.assertSame(property, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateQuery() {
		Query query = (Query)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Query.class }, 
				new TestInvocationHandler());
		IQuery facade = facadeFactory.createQuery(query);
		Assert.assertSame(query, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateQueryTranslator() {
		QueryTranslator queryTranslator = (QueryTranslator)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { QueryTranslator.class }, 
				new TestInvocationHandler());
		IQueryTranslator facade = facadeFactory.createQueryTranslator(queryTranslator);
		Assert.assertSame(queryTranslator, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateSessionFactory() {
		SessionFactory sessionFactory = (SessionFactory)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { SessionFactory.class }, 
				new TestInvocationHandler());
		ISessionFactory facade = facadeFactory.createSessionFactory(sessionFactory);
		Assert.assertSame(sessionFactory, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateSession() {
		Session session = (Session)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Session.class }, 
				new TestInvocationHandler());
		ISession facade = facadeFactory.createSession(session);
		Assert.assertSame(session, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateSettings() {
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		Settings settings = configuration.buildSettings();
		ISettings facade = facadeFactory.createSettings(settings);
		Assert.assertSame(settings, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateSpecialRootClass() {
		Property property = new Property();
		property.setValue(new SimpleValue());
		IProperty propertyFacade = new AbstractPropertyFacade(facadeFactory, property) {};
		IPersistentClass specialRootClass = facadeFactory.createSpecialRootClass(propertyFacade);
		Assert.assertSame(property, ((IFacade)specialRootClass.getProperty()).getTarget());
	}
	
	@Test
	public void testCreateTable() {
		Table table = new Table();
		ITable facade = facadeFactory.createTable(table);
		Assert.assertSame(table, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateTypeFactory() {
		ITypeFactory facade = facadeFactory.createTypeFactory();
		Assert.assertSame(TypeFactoryFacadeImpl.class, facade.getClass());
		Assert.assertNull(((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateType() {
		Type type = (Type)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Type.class }, 
				new TestInvocationHandler());
		IType facade = facadeFactory.createType(type);
		Assert.assertSame(type, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateValue() {
		Value value = (Value)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Value.class }, 
				new TestInvocationHandler());
		IValue facade = facadeFactory.createValue(value);
		Assert.assertSame(value, ((IFacade)facade).getTarget());
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}	
	}
	
}
