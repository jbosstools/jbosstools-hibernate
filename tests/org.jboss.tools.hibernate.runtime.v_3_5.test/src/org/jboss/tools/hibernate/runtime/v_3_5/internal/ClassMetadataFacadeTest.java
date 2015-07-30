package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.ShortType;
import org.hibernate.type.Type;
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
		classMetadata = new ClassMetadataFacadeImpl(FACADE_FACTORY, target) {};
	}
	
	@Test
	public void testGetMappedClass() {
		Assert.assertNull(classMetadata.getMappedClass());
		Assert.assertEquals("getMappedClass", methodName);
		Assert.assertArrayEquals(new Object[] { EntityMode.POJO }, arguments);
	}
	
	@Test
	public void testGetPropertyValue() {
		Object object = new Object();
		String name = "foobar";
		Assert.assertNull(classMetadata.getPropertyValue(object, name));
		Assert.assertEquals("getPropertyValue", methodName);
		Assert.assertArrayEquals(new Object[] { object, name, EntityMode.POJO }, arguments);
	}
	
	@Test
	public void testGetEntityName() {
		Assert.assertNull(classMetadata.getEntityName());
		Assert.assertEquals("getEntityName", methodName);
		Assert.assertNull(arguments);
	}
	
	@Test
	public void testGetIdentifierPropertyName() {
		Assert.assertNull(classMetadata.getIdentifierPropertyName());
		Assert.assertEquals("getIdentifierPropertyName", methodName);
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
		classMetadata = new ClassMetadataFacadeImpl(FACADE_FACTORY, createSampleEntityPersister()) {};
		Assert.assertTrue(classMetadata.isInstanceOfAbstractEntityPersister());
	}
	
	@Test 
	public void testGetEntityMetaModel() {
		Assert.assertNull(classMetadata.getEntityMetamodel());
		Assert.assertNull(methodName);
		TestEntityPersister entityPersister = createSampleEntityPersister();
		classMetadata = new ClassMetadataFacadeImpl(FACADE_FACTORY, entityPersister) {};
		Assert.assertNull(classMetadata.getEntityMetamodel());
		Assert.assertEquals("getEntityMetamodel", methodName);
		entityPersister.initializeEntityMetamodel();
		methodName = null;
		Assert.assertNotNull(classMetadata.getEntityMetamodel());
		Assert.assertEquals("getEntityMetamodel", methodName);
	}
	
	private TestEntityPersister createSampleEntityPersister() {
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		SessionFactoryImplementor sfi = new SessionFactoryImpl(configuration, null, configuration.buildSettings(), null, null);
		RootClass rc = new RootClass();
		Table t = new Table("foobar");
		rc.setTable(t);
		Column c = new Column("foo");
		t.addColumn(c);
		ArrayList<Column> keyList = new ArrayList<>();
		keyList.add(c);
		t.createUniqueKey(keyList);
		SimpleValue sv = new SimpleValue();
		sv.setNullValue("null");
		sv.setTypeName(Integer.class.getName());
		sv.addColumn(c);
		rc.setEntityName("foobar");
		rc.setIdentifier(sv);
		return new TestEntityPersister(rc, sfi);
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
	
	private class TestEntityPersister extends SingleTableEntityPersister {
		private EntityMetamodel entityMetaModel;
		private PersistentClass persistentClass;
		private SessionFactoryImplementor sessionFactoryImplementor;
		public TestEntityPersister(
				PersistentClass pc, 
				SessionFactoryImplementor sfi) throws HibernateException {
			super(pc, null, sfi, null);
			persistentClass = pc;
			sessionFactoryImplementor = sfi;
		}
		void initializeEntityMetamodel() {
			entityMetaModel = new EntityMetamodel(persistentClass, sessionFactoryImplementor);
		}
		@Override public EntityMetamodel getEntityMetamodel() {
			methodName = "getEntityMetamodel";
			return entityMetaModel;
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
