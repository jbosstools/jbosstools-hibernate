package org.jboss.tools.hibernate.runtime.v_6_1.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.export.java.POJOClass;
import org.hibernate.tool.internal.export.query.QueryExporter;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Query;

public class FacadeFactoryTest {

	private FacadeFactoryImpl facadeFactory;

	@BeforeEach
	public void beforeEach() throws Exception {
		facadeFactory = new FacadeFactoryImpl();
	}
	
	@Test
	public void testFacadeFactoryCreation() {
		assertNotNull(facadeFactory);
	}
	
	@Test
	public void testGetClassLoader() {
		assertSame(
				FacadeFactoryImpl.class.getClassLoader(), 
				facadeFactory.getClassLoader());
	}
	
	@Test
	public void testCreateArtifactCollector() {
		ArtifactCollector artifactCollector = new DefaultArtifactCollector();
		IArtifactCollector facade = facadeFactory.createArtifactCollector(artifactCollector);
		assertSame(artifactCollector, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateCfg2HbmTool() {
		Cfg2HbmTool cfg2HbmTool = new Cfg2HbmTool();
		ICfg2HbmTool facade = facadeFactory.createCfg2HbmTool(cfg2HbmTool);
		assertSame(cfg2HbmTool,  ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateNamingStrategy() {
		DefaultNamingStrategy namingStrategy = new DefaultNamingStrategy();
		INamingStrategy facade = facadeFactory.createNamingStrategy(namingStrategy);
		assertSame(namingStrategy, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateReverseEngineeringSettings() {
		RevengSettings res = new RevengSettings(null);
		IReverseEngineeringSettings facade = facadeFactory.createReverseEngineeringSettings(res);
		assertSame(res, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateReverseEngineeringStrategy() {
		RevengStrategy res = (RevengStrategy)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { RevengStrategy.class }, 
				new TestInvocationHandler());
		IReverseEngineeringStrategy facade = facadeFactory.createReverseEngineeringStrategy(res);
		assertTrue(facade instanceof ReverseEngineeringStrategyFacadeImpl);
		assertSame(res, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateOverrideRepository() {
		OverrideRepository overrideRepository = new OverrideRepository();
		IOverrideRepository facade = facadeFactory.createOverrideRepository(overrideRepository);
		assertSame(overrideRepository, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateSchemaExport() {
		SchemaExport schemaExport = new SchemaExport();
		ISchemaExport facade = facadeFactory.createSchemaExport(schemaExport);
		assertTrue(facade instanceof SchemaExportFacadeImpl);
		assertSame(schemaExport, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateGenericExporter() {
		GenericExporter genericExporter = new GenericExporter();
		IGenericExporter facade = facadeFactory.createGenericExporter(genericExporter);
		assertSame(genericExporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateHbm2DDLExporter() {
		DdlExporter ddlExporter = new DdlExporter();
		IHbm2DDLExporter facade = facadeFactory.createHbm2DDLExporter(ddlExporter);
		assertTrue(facade instanceof Hbm2DDLExporterFacadeImpl);
		assertSame(ddlExporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateQueryExporter() {
		QueryExporter queryExporter = new QueryExporter();
		IQueryExporter facade = facadeFactory.createQueryExporter(queryExporter);
		assertTrue(facade instanceof QueryExporterFacadeImpl);
		assertSame(queryExporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateTableFilter() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter facade = facadeFactory.createTableFilter(tableFilter);
		assertSame(tableFilter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateExporter() {
		Exporter exporter = (Exporter)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Exporter.class }, 
				new TestInvocationHandler());
		IExporter facade = facadeFactory.createExporter(exporter);
		assertTrue(facade instanceof ExporterFacadeImpl);
		assertSame(exporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateClassMetadata() {
		ClassMetadata classMetadata = (ClassMetadata)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { ClassMetadata.class }, 
				new TestInvocationHandler());
		IClassMetadata facade = facadeFactory.createClassMetadata(classMetadata);
		assertTrue(facade instanceof ClassMetadataFacadeImpl);
		assertSame(classMetadata, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateCollectionMetadata() {
		CollectionMetadata collectionMetadata = (CollectionMetadata)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { CollectionMetadata.class }, 
				new TestInvocationHandler());
		ICollectionMetadata facade = facadeFactory.createCollectionMetadata(collectionMetadata);
		assertSame(collectionMetadata, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateColumn() {
		Column column = new Column();
		IColumn facade = facadeFactory.createColumn(column);
		assertSame(column, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateConfiguration() {
		Configuration configuration = new Configuration();
		IConfiguration facade = facadeFactory.createConfiguration(configuration);
		assertTrue(facade instanceof ConfigurationFacadeImpl);
		assertSame(configuration, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateCriteria() {
		Query query = (Query)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Query.class }, 
				new TestInvocationHandler());
		ICriteria facade = facadeFactory.createCriteria(query);
		assertTrue(facade instanceof CriteriaFacadeImpl);
		assertSame(query, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateEntityMetamodel() {
		EntityPersister entityPersister = (EntityPersister)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { EntityPersister.class }, 
				new TestInvocationHandler());
		IEntityMetamodel entityMetamodel = facadeFactory.createEntityMetamodel(entityPersister);
		assertTrue(entityMetamodel instanceof EntityMetamodelFacadeImpl);
		assertSame(entityPersister, ((IFacade)entityMetamodel).getTarget());
	}
	

	@Test
	public void testCreateEnvironment() {
		IEnvironment environment = facadeFactory.createEnvironment();
		assertNotNull(environment);
		assertTrue(environment instanceof EnvironmentFacadeImpl);
	}
	
	@Test
	public void testCreateForeignKey() {
		ForeignKey foreignKey = new ForeignKey();
		IForeignKey facade = facadeFactory.createForeignKey(foreignKey);
		assertSame(foreignKey, ((IFacade)facade).getTarget());	
		assertTrue(facade instanceof ForeignKeyFacadeImpl);
	}
	
	@Test
	public void testCreateHibernateMappingExporter() {
		HbmExporter hibernateMappingExporter = new HbmExporter();
		IHibernateMappingExporter facade = facadeFactory.createHibernateMappingExporter(hibernateMappingExporter);
		assertSame(hibernateMappingExporter, ((IFacade)facade).getTarget());	
		assertTrue(facade instanceof HibernateMappingExporterFacadeImpl);
	}
	
	@Test
	public void testCreateHQLCodeAssist() {
		HQLCodeAssist hqlCodeAssist = new HQLCodeAssist(null);
		IHQLCodeAssist facade = facadeFactory.createHQLCodeAssist(hqlCodeAssist);
		assertSame(hqlCodeAssist, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateHQLCompletionProposal() {
		HQLCompletionProposal hqlCompletionProposal = new HQLCompletionProposal(0, 0);
		IHQLCompletionProposal facade = facadeFactory.createHQLCompletionProposal(hqlCompletionProposal);
		assertSame(hqlCompletionProposal, ((IFacade)facade).getTarget());		
	}	
	
	@Test
	public void testCreateJoin() {
		Join join = new Join();
		IJoin facade = facadeFactory.createJoin(join);
		assertSame(join, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreatePersistentClass() {
		PersistentClass persistentClass = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		IPersistentClass facade = facadeFactory.createPersistentClass(persistentClass);
		assertTrue(facade instanceof PersistentClassFacadeImpl);
		assertSame(persistentClass, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreatePOJOClass() {
		POJOClass pojoClass = (POJOClass)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { POJOClass.class }, 
				new TestInvocationHandler());
		IPOJOClass facade = facadeFactory.createPOJOClass(pojoClass);
		assertSame(pojoClass, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreatePrimaryKey() {
		PrimaryKey primaryKey = new PrimaryKey(null);
		IPrimaryKey facade = facadeFactory.createPrimaryKey(primaryKey);
		assertSame(primaryKey, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateProperty() {
		Property property = new Property();
		IProperty facade = facadeFactory.createProperty(property);
		assertSame(property, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateQuery() {
		Query query = (Query)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Query.class }, 
				new TestInvocationHandler());
		IQuery facade = facadeFactory.createQuery(query);
		assertTrue(facade instanceof QueryFacadeImpl);
		assertSame(query, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateSessionFactory() {
		SessionFactory sessionFactory = (SessionFactory)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { SessionFactory.class }, 
				new TestInvocationHandler());
		ISessionFactory facade = facadeFactory.createSessionFactory(sessionFactory);
		assertTrue(facade instanceof SessionFactoryFacadeImpl);
		assertSame(sessionFactory, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateSession() {
		Session session = (Session)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Session.class }, 
				new TestInvocationHandler());
		ISession facade = facadeFactory.createSession(session);
		assertTrue(facade instanceof SessionFacadeImpl);
		assertSame(session, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateTypeFactory() {
		ITypeFactory facade = facadeFactory.createTypeFactory();
		assertNotNull(facade);
		assertTrue(facade instanceof TypeFactoryFacadeImpl);
		assertNull(((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateTable() {
		Table table = new Table();
		ITable facade = facadeFactory.createTable(table);
		assertSame(table, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateType() {
		Type type = (Type)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Type.class }, 
				new TestInvocationHandler());
		IType facade = facadeFactory.createType(type);
		assertTrue(facade instanceof TypeFacadeImpl);
		assertSame(type, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateValue() {
		Value value = (Value)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Value.class }, 
				new TestInvocationHandler());
		IValue facade = facadeFactory.createValue(value);
		assertSame(value, ((IFacade)facade).getTarget());
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}	
	}
	
}
