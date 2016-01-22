package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.IMetaDataDialect;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.IQueryTranslator;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ISettings;
import org.jboss.tools.hibernate.runtime.spi.ITable;
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
	public void testFacadeFactoryCreation() {
		Assert.assertNotNull(facadeFactory);
	}
	
	@Test
	public void testGetClassLoader() {
		Assert.assertSame(
				FacadeFactoryImpl.class.getClassLoader(), 
				facadeFactory.getClassLoader());
	}
	
	@Test
	public void testCreateEnvironment() {
		IEnvironment environment = facadeFactory.createEnvironment();
		Assert.assertNotNull(environment);
		Assert.assertTrue(environment instanceof EnvironmentFacadeImpl);
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
		PersistentClass persistentClass = new RootClass(null);
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
		PrimaryKey primaryKey = new PrimaryKey(null);
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
		ISettings facade = facadeFactory.createSettings(null);
		Assert.assertNotNull(facade);
		Assert.assertEquals(
				Environment.getProperties().getProperty(
						AvailableSettings.DEFAULT_CATALOG), 
				facade.getDefaultCatalogName());
		Assert.assertEquals(
				Environment.getProperties().getProperty(
						AvailableSettings.DEFAULT_SCHEMA), 
				facade.getDefaultSchemaName());
	}
	
	@Test
	public void testCreateSpecialRootClass() {
		Property target = new Property();
		IProperty property = facadeFactory.createProperty(target);
		IPersistentClass specialRootClass = facadeFactory.createSpecialRootClass(property);
		Assert.assertNotNull(specialRootClass);
		Object object = ((IFacade)specialRootClass).getTarget();
		Assert.assertNotNull(object);
		Assert.assertTrue(object instanceof RootClass);
		Assert.assertSame(property, specialRootClass.getProperty());
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
		Assert.assertNotNull(facade);
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
