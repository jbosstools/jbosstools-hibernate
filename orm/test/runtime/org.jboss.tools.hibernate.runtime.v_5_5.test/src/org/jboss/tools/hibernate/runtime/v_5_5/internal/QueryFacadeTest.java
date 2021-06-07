package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QueryFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private static final String[] RETURN_ALIASES = new String[] { "foo", "bar" };
	private static final Type[] RETURN_TYPES = new Type[] {};
	private static final List<Object> LIST = Collections.emptyList();
	
	private String methodName = null;
	private Object[] arguments = null;
	
	private AbstractQueryFacade query = null;
	
	@BeforeEach
	public void beforeEach() {
		Query<?> queryProxy = (Query<?>)Proxy.newProxyInstance(
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
		assertEquals(LIST, query.list());
		assertEquals("list", methodName);
	}
	
	@Test
	public void testSetMaxResults() {
		query.setMaxResults(Integer.MAX_VALUE);
		assertEquals("setMaxResults", methodName);
		assertArrayEquals(new Object[] { Integer.MAX_VALUE }, arguments);
	}
	
}
