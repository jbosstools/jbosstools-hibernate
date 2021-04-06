package org.jboss.tools.hibernate.runtime.v_5_4.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.Criteria;
import org.jboss.tools.hibernate.runtime.common.AbstractCriteriaFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class CriteriaFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ICriteria criteriaFacade = null; 
	private Criteria criteria = null;
	
	private String methodName = null;
	private Object[] arguments = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		criteria = (Criteria)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Criteria.class }, 
				new TestInvocationHandler());
		criteriaFacade = new AbstractCriteriaFacade(FACADE_FACTORY, criteria) {};
	}
	
	@Test
	public void testSetMaxResults()  {
		criteriaFacade.setMaxResults(Integer.MAX_VALUE);
		assertEquals("setMaxResults", methodName);
		assertArrayEquals(new Object[] { Integer.MAX_VALUE }, arguments);
	}
	
	@Test
	public void testList() {
		assertNull(criteriaFacade.list());
		assertEquals("list", methodName);
		assertNull(arguments);
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
