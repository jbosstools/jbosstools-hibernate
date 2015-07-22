package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.EntityMode;
import org.hibernate.metadata.ClassMetadata;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
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
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			methodName = method.getName();
			arguments = args;
			return null;
		}
		
	}

}
