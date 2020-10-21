package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.jboss.tools.hibernate.runtime.common.AbstractCriteriaFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.junit.Before;
import org.junit.Test;

public class CriteriaFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private static final List<Object> RESULT_LIST = Arrays.asList();

	private int maxResults = Integer.MIN_VALUE;
	
	private ICriteria criteriaFacade = null;
	
	@Before
	public void before() {
		criteriaFacade = new AbstractCriteriaFacade(FACADE_FACTORY, createTestQuery()) {};		
	}
	
	@Test
	public void testSetMaxResults() {
		assertEquals(maxResults, Integer.MIN_VALUE);
		criteriaFacade.setMaxResults(Integer.MAX_VALUE);
		assertEquals(maxResults, Integer.MAX_VALUE);
	}
	
	private Query createTestQuery() {
		return (Query)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { Query.class }, 
				new TestInvocationHandler());
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String methodName = method.getName();
			if ("setMaxResults".equals(methodName)) {
				maxResults = (int)args[0];
			} else if ("getResultList".equals(methodName)) {
				return RESULT_LIST;
			}
			return null;
		}	
	}

}
