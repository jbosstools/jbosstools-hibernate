package org.jboss.tools.hibernate.runtime.v_5_2.internal;

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
	
	private static final String[] RETURN_ALIASES = new String[] { "foo", "bar" };
	private static final Type[] RETURN_TYPES = new Type[] {};
	private static final List<Object> LIST = Collections.emptyList();
	
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
						if ("list".equals(method.getName())) {
							return LIST;
						} else if ("getReturnAliases".equals(method.getName())) {
							return RETURN_ALIASES; 
						} else if ("getReturnTypes".equals(method.getName())) {
							return RETURN_TYPES;
						} else return null;
					}				
				});
		query = new AbstractQueryFacade(FACADE_FACTORY, queryProxy) {};
	}
	
	@Test
	public void testList() {
		Assert.assertEquals(LIST, query.list());
		Assert.assertEquals("list", methodName);
	}
	
	@Test
	public void testSetMaxResults() {
		query.setMaxResults(Integer.MAX_VALUE);
		Assert.assertEquals("setMaxResults", methodName);
		Assert.assertArrayEquals(new Object[] { Integer.MAX_VALUE }, arguments);
	}
	
	@Test
	public void testSetParameter() {
		Type typeProxy = (Type)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Type.class }, 
				new TypeInvocationHandler());
		IType typeFacade = FACADE_FACTORY.createType(typeProxy);
		Object object = new Object();
		query.setParameter(Integer.MAX_VALUE, object, typeFacade);
		Assert.assertEquals("setParameter", methodName);
		Assert.assertArrayEquals(new Object[] { Integer.MAX_VALUE, object, typeProxy } , arguments);
		methodName = null;
		arguments = null;
		query.setParameter("foobar", object, typeFacade);
		Assert.assertEquals("setParameter", methodName);
		Assert.assertArrayEquals(new Object[] { "foobar", object, typeProxy }, arguments);
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
	
	@Test
	public void testGetReturnAliases() {
		Assert.assertArrayEquals(RETURN_ALIASES, query.getReturnAliases());
		Assert.assertEquals("getReturnAliases", methodName);
		Assert.assertNull(arguments);
	}
	
	@Test
	public void testGetReturnTypes() {
		Assert.assertNotNull(query.getReturnTypes());
		Assert.assertEquals("getReturnTypes", methodName);
		Assert.assertNull(arguments);
	}
	
	private class TypeInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}		
	}
	
}
