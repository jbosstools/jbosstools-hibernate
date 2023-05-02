package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.export.java.POJOClass;
import org.hibernate.tool.internal.export.query.QueryExporter;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
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
	public void testCreateReverseEngineeringStrategy() {
		try {
			FACADE_FACTORY.createReverseEngineeringStrategy(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());			
		}
	}
	
	@Test
	public void testCreateReverseEngineeringSettings() {
		try {
			FACADE_FACTORY.createReverseEngineeringStrategy(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());			
		}
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
	public void testCreateConfiguration() {
		try {
			FACADE_FACTORY.createConfiguration(new Configuration());
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());			
		}
	}
	
	@Test
	public void testCreateColumn() {
		try {
			FACADE_FACTORY.createColumn(new Column());
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());			
		}
	}
	
	@Test
	public void testCreateSessionFactory() {
		try {
			FACADE_FACTORY.createSessionFactory(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());			
		}
	}
	
	@Test
	public void testCreateClassMetadata() {
		try {
			FACADE_FACTORY.createClassMetadata(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());			
		}
	}
	
	@Test
	public void testCreateCollectionMetadata() {
		try {
			FACADE_FACTORY.createCollectionMetadata(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());			
		}
	}
	
	@Test
	public void testCreateSession() {
		try {
			FACADE_FACTORY.createSession(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}
	
	@Test
	public void testCreateCriteria() {
		try {
			FACADE_FACTORY.createCriteria(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}
	
	@Test
	public void testCreateSpecialRootClass() {
		try {
			FACADE_FACTORY.createSpecialRootClass(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}
	
	@Test
	public void testCreatePersistentClass() {
		try {
			FACADE_FACTORY.createPersistentClass(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}
	
	@Test
	public void testCreateJoin() {
		try {
			FACADE_FACTORY.createJoin(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}
	
	@Test
	public void testCreateHQLCompletionProposal() {
		try {
			FACADE_FACTORY.createHQLCompletionProposal(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}	
	
	@Test
	public void testCreateProperty() {
		try {
			FACADE_FACTORY.createProperty(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}
	
	@Test
	public void testCreateTable() {
		try {
			FACADE_FACTORY.createTable(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}
	
	@Test
	public void testCreateValue() {
		try {
			FACADE_FACTORY.createValue(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}
	
	@Test
	public void testCreateTableFilter() {
		try {
			FACADE_FACTORY.createTableFilter(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}
	
	@Test
	public void testCreateTypeFactory() {
		try {
			FACADE_FACTORY.createTypeFactory();
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}
	
	@Test
	public void testCreateType() {
		try {
			FACADE_FACTORY.createType(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}
	
	@Test
	public void testCreateForeignKey() {
		try {
			FACADE_FACTORY.createForeignKey(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}
	
	@Test
	public void testCreatePrimaryKey() {
		try {
			FACADE_FACTORY.createPrimaryKey(null);
			fail();
		} catch (Throwable t) {
			assertEquals("Should use class 'NewFacadeFactory'", t.getMessage());
		}
	}
	
	@Test
	public void testCreateEnvironment() {
		try {
			FACADE_FACTORY.createEnvironment();
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
	public void testCreatePOJOClass() {
		POJOClass pojoClass = (POJOClass)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { POJOClass.class }, 
				new TestInvocationHandler());
		IPOJOClass facade = FACADE_FACTORY.createPOJOClass(pojoClass);
		assertSame(pojoClass, ((IFacade)facade).getTarget());
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
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}	
	}
	
}
