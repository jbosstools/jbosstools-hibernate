package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class QueryFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private String methodName = null;
	private Object[] arguments = null;
	
	private AbstractQueryFacade query = null;
	
	@Before
	public void setUp() {
		Query queryProxy = (Query)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Query.class }, 
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						methodName = method.getName();
						arguments = args;
						return null;
					}				
				});
		query = new AbstractQueryFacade(FACADE_FACTORY, queryProxy) {};
	}
	
	@Test
	public void testSetParameterList() {
		Type typeProxy = (Type)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Type.class }, 
				new TypeInvocationHandler());
		IType typeFacade = FACADE_FACTORY.createType(typeProxy);
		List<Object> dummyList = Collections.emptyList();
		query.setParameterList("foobar", dummyList, typeFacade);
		Assert.assertEquals("setParameterList", methodName);
		Assert.assertArrayEquals(new Object[] { "foobar", dummyList, typeProxy }, arguments);
	}
	
	private class TypeInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}		
	}
	
}
