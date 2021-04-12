package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import org.hibernate.HibernateException;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClassMetadataFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private String methodName = null;
	private Object[] arguments = null;

	private IClassMetadata classMetadata = null; 
	private boolean hasIdentifierProperty = false;
	
	@BeforeEach
	public void beforeEach() {
		ClassMetadata target = (ClassMetadata)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { ClassMetadata.class }, 
				new TestInvocationHandler());
		classMetadata = new ClassMetadataFacadeImpl(FACADE_FACTORY, target);
	}
	
	@Test
	public void testGetMappedClass() {
		assertNull(classMetadata.getMappedClass());
		assertEquals("getMappedClass", methodName);
		assertNull(arguments);
	}
	
	@Test
	public void testGetPropertyValue() {
		Object object = new Object();
		String name = "foobar";
		assertNull(classMetadata.getPropertyValue(object, name));
		assertEquals("getPropertyValue", methodName);
		assertArrayEquals(new Object[] { object, name }, arguments);
	}
	
	@Test
	public void testGetIdentifierPropertyName() {
		assertNull(classMetadata.getIdentifierPropertyName());
		assertEquals("getIdentifierPropertyName", methodName);
		assertNull(arguments);
	}
	
	@Test
	public void testGetEntityName() {
		assertNull(classMetadata.getEntityName());
		assertEquals("getEntityName", methodName);
		assertNull(arguments);
	}
	
	@Test
	public void testGetPropertyNames() {
		assertNull(classMetadata.getPropertyNames());
		assertEquals("getPropertyNames", methodName);
		assertNull(arguments);
	}
	
	@Test
	public void testGetPropertyTypes() {
		IType[] propertyTypes = classMetadata.getPropertyTypes();
		assertEquals(1, propertyTypes.length);
		assertEquals("getPropertyTypes", methodName);
		assertNull(arguments);
		methodName = null;
		assertSame(propertyTypes, classMetadata.getPropertyTypes());
		assertNull(methodName);
		assertNull(arguments);
	}
	
	@Test
	public void testGetIdentifierType() {
		IType identifierType = classMetadata.getIdentifierType();
		assertNotNull(identifierType);
		assertEquals("getIdentifierType", methodName);
		assertNull(arguments);
		methodName = null;
		assertSame(identifierType, classMetadata.getIdentifierType());
		assertNull(methodName);
		assertNull(arguments);
	}
	
	@Test
	public void testIsInstanceOfAbstractEntityPersister() {
		assertFalse(classMetadata.isInstanceOfAbstractEntityPersister());
		classMetadata = new AbstractClassMetadataFacade(FACADE_FACTORY, createSampleEntityPersister()) {};
		assertTrue(classMetadata.isInstanceOfAbstractEntityPersister());
	}
	
	@Test 
	public void testGetEntityMetaModel() {
		assertNull(classMetadata.getEntityMetamodel());
		assertNull(methodName);
		TestEntityPersister entityPersister = createSampleEntityPersister();
		classMetadata = new AbstractClassMetadataFacade(FACADE_FACTORY, entityPersister) {};
		methodName = null;
		assertNotNull(classMetadata.getEntityMetamodel());
		assertEquals("getEntityMetamodel", methodName);
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
		assertSame(object, classMetadata.getIdentifier(object , session));
		assertEquals("getIdentifier", methodName);
		assertArrayEquals(new Object[] { object, sessionTarget },  arguments);
	}
	
	public void testHasIdentifierProperty() {
		hasIdentifierProperty = true;
		assertTrue(classMetadata.hasIdentifierProperty());
		hasIdentifierProperty = false;
		assertFalse(hasIdentifierProperty);
		assertEquals("hasIdentifierProperty", methodName);
		assertNull(arguments);
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
