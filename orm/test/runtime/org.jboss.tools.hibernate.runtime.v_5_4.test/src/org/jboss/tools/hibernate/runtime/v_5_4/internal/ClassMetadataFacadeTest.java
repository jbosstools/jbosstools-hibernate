package org.jboss.tools.hibernate.runtime.v_5_4.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import org.hibernate.HibernateException;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.ShortType;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.AbstractClassMetadataFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ClassMetadataFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private String methodName = null;
	private Object[] arguments = null;

	private IClassMetadata classMetadata = null; 
	private boolean hasIdentifierProperty = false;
	
	@Before
	public void setUp() {
		ClassMetadata target = (ClassMetadata)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { ClassMetadata.class }, 
				new TestInvocationHandler());
		classMetadata = new ClassMetadataFacadeImpl(FACADE_FACTORY, target);
	}
	
	@Test
	public void testGetMappedClass() {
		Assert.assertNull(classMetadata.getMappedClass());
		Assert.assertEquals("getMappedClass", methodName);
		Assert.assertNull(arguments);
	}
	
	@Test
	public void testGetPropertyValue() {
		Object object = new Object();
		String name = "foobar";
		Assert.assertNull(classMetadata.getPropertyValue(object, name));
		Assert.assertEquals("getPropertyValue", methodName);
		Assert.assertArrayEquals(new Object[] { object, name }, arguments);
	}
	
	@Test
	public void testGetIdentifierPropertyName() {
		Assert.assertNull(classMetadata.getIdentifierPropertyName());
		Assert.assertEquals("getIdentifierPropertyName", methodName);
		Assert.assertNull(arguments);
	}
	
	@Test
	public void testGetEntityName() {
		Assert.assertNull(classMetadata.getEntityName());
		Assert.assertEquals("getEntityName", methodName);
		Assert.assertNull(arguments);
	}
	
	@Test
	public void testGetPropertyNames() {
		Assert.assertNull(classMetadata.getPropertyNames());
		Assert.assertEquals("getPropertyNames", methodName);
		Assert.assertNull(arguments);
	}
	
	@Test
	public void testGetPropertyTypes() {
		IType[] propertyTypes = classMetadata.getPropertyTypes();
		Assert.assertEquals(1, propertyTypes.length);
		Assert.assertEquals("getPropertyTypes", methodName);
		Assert.assertNull(arguments);
		methodName = null;
		Assert.assertSame(propertyTypes, classMetadata.getPropertyTypes());
		Assert.assertNull(methodName);
		Assert.assertNull(arguments);
	}
	
	@Test
	public void testGetIdentifierType() {
		IType identifierType = classMetadata.getIdentifierType();
		Assert.assertNotNull(identifierType);
		Assert.assertEquals("getIdentifierType", methodName);
		Assert.assertNull(arguments);
		methodName = null;
		Assert.assertSame(identifierType, classMetadata.getIdentifierType());
		Assert.assertNull(methodName);
		Assert.assertNull(arguments);
	}
	
	@Test
	public void testIsInstanceOfAbstractEntityPersister() {
		Assert.assertFalse(classMetadata.isInstanceOfAbstractEntityPersister());
		classMetadata = new AbstractClassMetadataFacade(FACADE_FACTORY, createSampleEntityPersister()) {};
		Assert.assertTrue(classMetadata.isInstanceOfAbstractEntityPersister());
	}
	
	@Test 
	public void testGetEntityMetaModel() {
		Assert.assertNull(classMetadata.getEntityMetamodel());
		Assert.assertNull(methodName);
		TestEntityPersister entityPersister = createSampleEntityPersister();
		classMetadata = new AbstractClassMetadataFacade(FACADE_FACTORY, entityPersister) {};
		methodName = null;
		Assert.assertNotNull(classMetadata.getEntityMetamodel());
		Assert.assertEquals("getEntityMetamodel", methodName);
	}
	
	@Test
	public void testGetIdentifier() {
		ClassLoader cl = FACADE_FACTORY.getClassLoader();
		final SessionImplementor sessionTarget = (SessionImplementor)Proxy.newProxyInstance(
				cl, 
				new Class[] { SessionImplementor.class }, 
				new TestInvocationHandler()); 
		ISession session = (ISession)Proxy.newProxyInstance(
				cl, 
				new Class[] { ISession.class,  IFacade.class }, 
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return sessionTarget;
					}					
				});
		Object object = Integer.MAX_VALUE;
		Assert.assertSame(object, classMetadata.getIdentifier(object , session));
		Assert.assertEquals("getIdentifier", methodName);
		Assert.assertArrayEquals(new Object[] { object, sessionTarget },  arguments);
	}
	
	public void testHasIdentifierProperty() {
		hasIdentifierProperty = true;
		Assert.assertTrue(classMetadata.hasIdentifierProperty());
		hasIdentifierProperty = false;
		Assert.assertFalse(hasIdentifierProperty);
		Assert.assertEquals("hasIdentifierProperty", methodName);
		Assert.assertNull(arguments);
	}
	
	private TestEntityPersister createSampleEntityPersister() {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		ServiceRegistry serviceRegistry = builder.build();		
		MetadataSources metadataSources = new MetadataSources(serviceRegistry);
		MetadataImplementor metadata = (MetadataImplementor)metadataSources.buildMetadata();
		SessionFactoryImplementor sfi = (SessionFactoryImplementor)metadata.buildSessionFactory();
		RootClass rc = new RootClass(null);
		Table t = new Table("foobar");
		rc.setTable(t);
		Column c = new Column("foo");
		t.addColumn(c);
		ArrayList<Column> keyList = new ArrayList<>();
		keyList.add(c);
		t.createUniqueKey(keyList);
		SimpleValue sv = new SimpleValue(metadata);
		sv.setNullValue("null");
		sv.setTypeName(Integer.class.getName());
		sv.addColumn(c);
		rc.setEntityName("foobar");
		rc.setIdentifier(sv);
		rc.setOptimisticLockStyle(OptimisticLockStyle.NONE);
		return new TestEntityPersister(rc, sfi, metadata);
	}
	
	private class TestPersisterCreationContext implements PersisterCreationContext {
		final private SessionFactoryImplementor sfi;
		final private MetadataImplementor md;
		private TestPersisterCreationContext(
				SessionFactoryImplementor sfi,
				MetadataImplementor md) {
			this.sfi = sfi;
			this.md = md;
		}
		@Override
		public SessionFactoryImplementor getSessionFactory() {
			return sfi;
		}
		@Override
		public MetadataImplementor getMetadata() {
			return md;
		}		
	}
	
	private class TestEntityPersister extends SingleTableEntityPersister {
		public TestEntityPersister(
				PersistentClass pc, 
				SessionFactoryImplementor sfi,
				MetadataImplementor md) throws HibernateException {
			super(pc, null, null, new TestPersisterCreationContext(sfi, md));
		}
		@Override public EntityMetamodel getEntityMetamodel() {
			methodName = "getEntityMetamodel";
			return super.getEntityMetamodel();
		}
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			methodName = method.getName();
			arguments = args;
			if ("getPropertyTypes".equals(methodName)) {
				return new Type[] { new ShortType() };
			} else if ("getIdentifierType".equals(methodName)) {
				return new ShortType();
			} else if ("getIdentifier".equals(methodName)) {
				return args[0];
			} else if ("hasIdentifierProperty".equals(methodName)) {
				return hasIdentifierProperty;
			} else {
				return null;
			}
		}
		
	}

}
