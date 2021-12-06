package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.jboss.tools.hibernate.runtime.common.AbstractCriteriaFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class CriteriaFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	private static final List<?> EMPTY_LIST = new ArrayList<>();
	
	private ICriteria criteriaFacade = null; 
	private CriteriaImpl criteria = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		criteria = new CriteriaImpl("foobar", createFoobarSessionContractImplementor());
		criteriaFacade = new AbstractCriteriaFacade(FACADE_FACTORY, criteria) {};
	}
	
	@Test
	public void testSetMaxResults()  {
		assertNotEquals(Integer.MAX_VALUE, criteria.getMaxResults());
		criteriaFacade.setMaxResults(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, criteria.getMaxResults());
	}
	
	@Test
	public void testList() {
		assertSame(EMPTY_LIST, criteriaFacade.list());
	}
	
	private SessionImplementor createFoobarSessionContractImplementor() {
		return (SessionImplementor)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { SessionImplementor.class }, 
				new InvocationHandler() {				
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if ("list".equals(method.getName())) {
							return EMPTY_LIST;
						}
						return null;
					}
				});
	}
	
}
