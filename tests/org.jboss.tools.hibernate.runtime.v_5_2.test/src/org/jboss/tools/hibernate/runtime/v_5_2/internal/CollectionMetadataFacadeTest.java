package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.type.ShortType;
import org.jboss.tools.hibernate.runtime.common.AbstractCollectionMetadataFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class CollectionMetadataFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private String methodName = null;
	private Object[] arguments = null;

	private ICollectionMetadata collectionMetadata = null; 
	
	@Before
	public void setUp() {
		CollectionMetadata target = (CollectionMetadata)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { CollectionMetadata.class }, 
				new TestInvocationHandler());
		collectionMetadata = new AbstractCollectionMetadataFacade(FACADE_FACTORY, target) {};
	}
	
	@Test
	public void testGetElementType() {
		Assert.assertNotNull(collectionMetadata.getElementType());
		Assert.assertEquals("getElementType", methodName);
		Assert.assertNull(arguments);
		methodName = null;
		Assert.assertNotNull(collectionMetadata.getElementType());
		Assert.assertNull(methodName);
		Assert.assertNull(arguments);
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			methodName = method.getName();
			arguments = args;
			if ("getElementType".equals(methodName)) {
				return new ShortType();
			} else {
				return null;
			}
		}
		
	}

}
