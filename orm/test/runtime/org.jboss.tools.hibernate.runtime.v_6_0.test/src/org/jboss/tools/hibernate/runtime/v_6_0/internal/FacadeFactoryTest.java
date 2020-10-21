package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.export.query.QueryExporter;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.IType;
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
		Assert.assertTrue(facade instanceof SchemaExportFacadeImpl);
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
		assertSame(ddlExporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateQueryExporter() {
		QueryExporter queryExporter = new QueryExporter();
		IQueryExporter facade = facadeFactory.createQueryExporter(queryExporter);
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
		Assert.assertTrue(facade instanceof ExporterFacadeImpl);
		Assert.assertSame(exporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateEntityMetamodel() {
		EntityPersister entityPersister = (EntityPersister)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { EntityPersister.class }, 
				new TestInvocationHandler());
		IEntityMetamodel entityMetamodel = facadeFactory.createEntityMetamodel(entityPersister);
		assertSame(entityPersister, ((IFacade)entityMetamodel).getTarget());
	}
	

	@Test
	public void testCreateSessionFactory() {
		SessionFactory sessionFactory = (SessionFactory)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { SessionFactory.class }, 
				new TestInvocationHandler());
		ISessionFactory facade = facadeFactory.createSessionFactory(sessionFactory);
		assertSame(sessionFactory, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreatePersistentClass() {
		PersistentClass persistentClass = new RootClass(null);
		IPersistentClass facade = facadeFactory.createPersistentClass(persistentClass);
		assertSame(persistentClass, ((IFacade)facade).getTarget());
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
		Assert.assertSame(type, ((IFacade)facade).getTarget());
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}	
	}
	
}
