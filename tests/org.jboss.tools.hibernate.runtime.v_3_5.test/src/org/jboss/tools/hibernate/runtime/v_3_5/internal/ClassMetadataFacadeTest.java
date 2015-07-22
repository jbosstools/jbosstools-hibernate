package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import org.hibernate.EntityMode;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.type.ShortType;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.AbstractClassMetadataFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ClassMetadataFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private String methodName = null;
	private Object[] arguments = null;

	private IClassMetadata classMetadata = null; 
	
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
		// now create an instance of AbstractEntityPersister
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
		SingleTableEntityPersister step = new SingleTableEntityPersister(rc, null, sfi, null);
		classMetadata = new AbstractClassMetadataFacade(FACADE_FACTORY, step) {};
		Assert.assertTrue(classMetadata.isInstanceOfAbstractEntityPersister());
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
			} else {
				return null;
			}
		}
		
	}

}
