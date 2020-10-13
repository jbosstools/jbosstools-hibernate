package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.metadata.ClassMetadata;
import org.junit.Before;
import org.junit.Test;

public class ClassMetadataFacadeTest {
	
	private static final FacadeFactoryImpl FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ClassMetadata classMetadataTarget;
	private ClassMetadataFacadeImpl classMetadataFacade;
	
	@Before
	public void before() throws Exception {
		classMetadataTarget = (ClassMetadata)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { ClassMetadata.class }, 
				new TestInvocationHandler());
		classMetadataFacade = new ClassMetadataFacadeImpl(FACADE_FACTORY, classMetadataTarget);
	}
	
	@Test
	public void testCreation() {
		
	}

	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}	
	}
	
}
