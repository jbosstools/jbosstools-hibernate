package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CriteriaFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ICriteria criteriaFacade = null;
	
	private static final List<Object> RESULT_LIST = Arrays.asList();

	private int maxResults = Integer.MIN_VALUE;

	@BeforeEach
	public void beforeEach() {
		criteriaFacade = new CriteriaFacadeImpl(FACADE_FACTORY, createTestQuery());		
	}
	
	@Test
	public void testSetMaxResults() {
		assertEquals(maxResults, Integer.MIN_VALUE);
		criteriaFacade.setMaxResults(Integer.MAX_VALUE);
		assertEquals(maxResults, Integer.MAX_VALUE);
	}
	
	@Test 
	public void testList() {
		assertSame(RESULT_LIST, criteriaFacade.list());
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
