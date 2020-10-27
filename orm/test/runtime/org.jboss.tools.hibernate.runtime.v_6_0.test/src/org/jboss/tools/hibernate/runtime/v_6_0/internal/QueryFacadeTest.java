package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.engine.spi.SessionDelegatorBaseImpl;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.query.Query;
import org.hibernate.query.internal.ParameterMetadataImpl;
import org.hibernate.query.internal.QueryImpl;
import org.hibernate.query.spi.QueryImplementor;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.junit.Before;
import org.junit.Test;

public class QueryFacadeTest {
	
	private final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private Query<?> queryTarget = null;
	private IQuery queryFacade = null;
	
	@Before
	public void before() {
		queryTarget = new TestQuery<Object>();
		queryFacade = new AbstractQueryFacade(FACADE_FACTORY, queryTarget) {};
	}
	
	@Test
	public void testList() {
		assertSame(((TestQuery<?>)queryTarget).theList, queryFacade.list());
	}
	
	@Test
	public void testSetMaxResults() {
		assertEquals(Integer.MIN_VALUE, ((TestQuery<?>)queryTarget).maxResults);
		queryFacade.setMaxResults(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, ((TestQuery<?>)queryTarget).maxResults);
	}
	
	private class TestQuery<R> extends QueryImpl<R> {
		
		private List<R> theList = new ArrayList<R>();
		int maxResults = Integer.MIN_VALUE;

		public TestQuery() {
			super(
					new TestSharedSessionContractImplementor(), 
					new ParameterMetadataImpl(Collections.emptySet()), 
					null);
		}
		
		@Override
		public List<R> list() {
			return theList;
		}
		
		@Override
		public QueryImplementor<?> setMaxResults(int i) {
			maxResults = i;
			return this;
		}

	}
	
	private class TestSharedSessionContractImplementor extends SessionDelegatorBaseImpl {

		private static final long serialVersionUID = 1L;
		
		public TestSharedSessionContractImplementor() {
			super(createDelegate());
		}

	}
	
	private SessionImplementor createDelegate() {
		return (SessionImplementor)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { SessionImplementor.class },
				new InvocationHandler() {		
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if ("isQueryParametersValidationEnabled".equals(method.getName())) {
							return false;
						}
						return null;
					}
		});
	}
	
}
