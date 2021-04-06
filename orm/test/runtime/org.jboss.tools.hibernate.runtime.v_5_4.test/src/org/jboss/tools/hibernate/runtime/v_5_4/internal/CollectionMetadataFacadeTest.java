package org.jboss.tools.hibernate.runtime.v_5_4.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.type.ShortType;
import org.jboss.tools.hibernate.runtime.common.AbstractCollectionMetadataFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CollectionMetadataFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private String methodName = null;
	private Object[] arguments = null;

	private ICollectionMetadata collectionMetadata = null; 
	
	@BeforeEach
	public void beforeEach() {
		CollectionMetadata target = (CollectionMetadata)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { CollectionMetadata.class }, 
				new TestInvocationHandler());
		collectionMetadata = new AbstractCollectionMetadataFacade(FACADE_FACTORY, target) {};
	}
	
	@Test
	public void testGetElementType() {
		assertNotNull(collectionMetadata.getElementType());
		assertEquals("getElementType", methodName);
		assertNull(arguments);
		methodName = null;
		assertNotNull(collectionMetadata.getElementType());
		assertNull(methodName);
		assertNull(arguments);
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
