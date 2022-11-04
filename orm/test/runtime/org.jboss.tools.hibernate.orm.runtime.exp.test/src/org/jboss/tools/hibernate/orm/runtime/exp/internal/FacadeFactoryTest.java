package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
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
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.export.java.POJOClass;
import org.hibernate.tool.internal.export.query.QueryExporter;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.IFacade;
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
import org.junit.jupiter.api.Test;

import jakarta.persistence.Query;

public class FacadeFactoryTest {

	private static FacadeFactoryImpl FACADE_FACTORY = new FacadeFactoryImpl();

	@Test
	public void testFacadeFactoryCreation() {
		assertNotNull(FACADE_FACTORY);
	}
	
	@Test
	public void testGetClassLoader() {
		assertSame(
				FacadeFactoryImpl.class.getClassLoader(), 
				FACADE_FACTORY.getClassLoader());
	}
	
	@Test
	public void testCreateArtifactCollector() {
		try {
			FACADE_FACTORY.createArtifactCollector(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}
	
	@Test
	public void testCreateCfg2HbmTool() {
		try {
			FACADE_FACTORY.createCfg2HbmTool(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}
	
	@Test
	public void testCreateNamingStrategy() {
		try {
			FACADE_FACTORY.createNamingStrategy((String)null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());			
		}
	}
	
	@Test
	public void testCreateReverseEngineeringSettings() {
		RevengSettings res = new RevengSettings(null);
		IReverseEngineeringSettings facade = FACADE_FACTORY.createReverseEngineeringSettings(res);
		assertSame(res, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateReverseEngineeringStrategy() {
		RevengStrategy res = (RevengStrategy)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { RevengStrategy.class }, 
				new TestInvocationHandler());
		IReverseEngineeringStrategy facade = FACADE_FACTORY.createReverseEngineeringStrategy(res);
		assertTrue(facade instanceof ReverseEngineeringStrategyFacadeImpl);
		assertSame(res, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateOverrideRepository() {
		try {
			FACADE_FACTORY.createOverrideRepository(new OverrideRepository());
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());			
		}
	}
	
	@Test
	public void testCreateSchemaExport() {
		SchemaExport schemaExport = new SchemaExport();
		ISchemaExport facade = FACADE_FACTORY.createSchemaExport(schemaExport);
		assertTrue(facade instanceof SchemaExportFacadeImpl);
		assertSame(schemaExport, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateGenericExporter() {
		GenericExporter genericExporter = new GenericExporter();
		IGenericExporter facade = FACADE_FACTORY.createGenericExporter(genericExporter);
		assertSame(genericExporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateHbm2DDLExporter() {
		DdlExporter ddlExporter = new DdlExporter();
		IHbm2DDLExporter facade = FACADE_FACTORY.createHbm2DDLExporter(ddlExporter);
		assertTrue(facade instanceof Hbm2DDLExporterFacadeImpl);
		assertSame(ddlExporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateQueryExporter() {
		QueryExporter queryExporter = new QueryExporter();
		IQueryExporter facade = FACADE_FACTORY.createQueryExporter(queryExporter);
		assertTrue(facade instanceof QueryExporterFacadeImpl);
		assertSame(queryExporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateTableFilter() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter facade = FACADE_FACTORY.createTableFilter(tableFilter);
		assertSame(tableFilter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateExporter() {
		Exporter exporter = (Exporter)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Exporter.class }, 
				new TestInvocationHandler());
		IExporter facade = FACADE_FACTORY.createExporter(exporter);
		assertTrue(facade instanceof ExporterFacadeImpl);
		assertSame(exporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateClassMetadata() {
		ClassMetadata classMetadata = (ClassMetadata)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { ClassMetadata.class }, 
				new TestInvocationHandler());
		IClassMetadata facade = FACADE_FACTORY.createClassMetadata(classMetadata);
		assertTrue(facade instanceof ClassMetadataFacadeImpl);
		assertSame(classMetadata, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateCollectionMetadata() {
		CollectionMetadata collectionMetadata = (CollectionMetadata)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { CollectionMetadata.class }, 
				new TestInvocationHandler());
		ICollectionMetadata facade = FACADE_FACTORY.createCollectionMetadata(collectionMetadata);
		assertSame(collectionMetadata, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateColumn() {
		Column column = new Column();
		IColumn facade = FACADE_FACTORY.createColumn(column);
		assertTrue(facade instanceof ColumnFacadeImpl);
		assertSame(column, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateConfiguration() {
		Configuration configuration = new Configuration();
		IConfiguration facade = FACADE_FACTORY.createConfiguration(configuration);
		assertTrue(facade instanceof ConfigurationFacadeImpl);
		assertSame(configuration, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateCriteria() {
		Query query = (Query)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Query.class }, 
				new TestInvocationHandler());
		ICriteria facade = FACADE_FACTORY.createCriteria(query);
		assertTrue(facade instanceof CriteriaFacadeImpl);
		assertSame(query, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateEntityMetamodel() {
		EntityPersister entityPersister = (EntityPersister)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { EntityPersister.class }, 
				new TestInvocationHandler());
		IEntityMetamodel entityMetamodel = FACADE_FACTORY.createEntityMetamodel(entityPersister);
		assertTrue(entityMetamodel instanceof EntityMetamodelFacadeImpl);
		assertSame(entityPersister, ((IFacade)entityMetamodel).getTarget());
	}
	

	@Test
	public void testCreateEnvironment() {
		IEnvironment environment = FACADE_FACTORY.createEnvironment();
		assertNotNull(environment);
		assertTrue(environment instanceof EnvironmentFacadeImpl);
	}
	
	@Test
	public void testCreateForeignKey() {
		ForeignKey foreignKey = new ForeignKey();
		IForeignKey facade = FACADE_FACTORY.createForeignKey(foreignKey);
		assertSame(foreignKey, ((IFacade)facade).getTarget());	
		assertTrue(facade instanceof ForeignKeyFacadeImpl);
	}
	
	@Test
	public void testCreateHibernateMappingExporter() {
		HbmExporter hibernateMappingExporter = new HbmExporter();
		IHibernateMappingExporter facade = FACADE_FACTORY.createHibernateMappingExporter(hibernateMappingExporter);
		assertSame(hibernateMappingExporter, ((IFacade)facade).getTarget());	
		assertTrue(facade instanceof HibernateMappingExporterFacadeImpl);
	}
	
	@Test
	public void testCreateHQLCodeAssist() {
		HQLCodeAssist hqlCodeAssist = new HQLCodeAssist(null);
		IHQLCodeAssist facade = FACADE_FACTORY.createHQLCodeAssist(hqlCodeAssist);
		assertSame(hqlCodeAssist, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateHQLCompletionProposal() {
		HQLCompletionProposal hqlCompletionProposal = new HQLCompletionProposal(0, 0);
		IHQLCompletionProposal facade = FACADE_FACTORY.createHQLCompletionProposal(hqlCompletionProposal);
		assertSame(hqlCompletionProposal, ((IFacade)facade).getTarget());		
	}	
	
	@Test
	public void testCreateJoin() {
		Join join = new Join();
		IJoin facade = FACADE_FACTORY.createJoin(join);
		assertSame(join, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreatePersistentClass() {
		PersistentClass persistentClass = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		IPersistentClass facade = FACADE_FACTORY.createPersistentClass(persistentClass);
		assertTrue(facade instanceof PersistentClassFacadeImpl);
		assertSame(persistentClass, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreatePOJOClass() {
		POJOClass pojoClass = (POJOClass)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { POJOClass.class }, 
				new TestInvocationHandler());
		IPOJOClass facade = FACADE_FACTORY.createPOJOClass(pojoClass);
		assertSame(pojoClass, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreatePrimaryKey() {
		PrimaryKey primaryKey = new PrimaryKey(null);
		IPrimaryKey facade = FACADE_FACTORY.createPrimaryKey(primaryKey);
		assertSame(primaryKey, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateProperty() {
		Property property = new Property();
		IProperty facade = FACADE_FACTORY.createProperty(property);
		assertSame(property, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateQuery() {
		Query query = (Query)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Query.class }, 
				new TestInvocationHandler());
		IQuery facade = FACADE_FACTORY.createQuery(query);
		assertTrue(facade instanceof QueryFacadeImpl);
		assertSame(query, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateSessionFactory() {
		SessionFactory sessionFactory = (SessionFactory)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { SessionFactory.class }, 
				new TestInvocationHandler());
		ISessionFactory facade = FACADE_FACTORY.createSessionFactory(sessionFactory);
		assertTrue(facade instanceof SessionFactoryFacadeImpl);
		assertSame(sessionFactory, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateSession() {
		Session session = (Session)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Session.class }, 
				new TestInvocationHandler());
		ISession facade = FACADE_FACTORY.createSession(session);
		assertTrue(facade instanceof SessionFacadeImpl);
		assertSame(session, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateTypeFactory() {
		ITypeFactory facade = FACADE_FACTORY.createTypeFactory();
		assertNotNull(facade);
		assertTrue(facade instanceof TypeFactoryFacadeImpl);
		assertNull(((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateTable() {
		Table table = new Table();
		ITable facade = FACADE_FACTORY.createTable(table);
		assertSame(table, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateSpecialRootClass() {
		Property target = new Property();
		PersistentClass pc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		target.setPersistentClass(pc);
		IProperty property = FACADE_FACTORY.createProperty(target);
		IPersistentClass specialRootClass = FACADE_FACTORY.createSpecialRootClass(property);
		assertNotNull(specialRootClass);
		Object object = ((IFacade)specialRootClass).getTarget();
		assertTrue(specialRootClass instanceof SpecialRootClassFacadeImpl);
		assertTrue(object instanceof RootClass);
		assertSame(property, specialRootClass.getProperty());
	}
	
	@Test
	public void testCreateType() {
		Type type = (Type)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Type.class }, 
				new TestInvocationHandler());
		IType facade = FACADE_FACTORY.createType(type);
		assertTrue(facade instanceof TypeFacadeImpl);
		assertSame(type, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateValue() {
		Value value = (Value)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Value.class }, 
				new TestInvocationHandler());
		IValue facade = FACADE_FACTORY.createValue(value);
		assertSame(value, ((IFacade)facade).getTarget());
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}	
	}
	
}
