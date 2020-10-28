package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.TemporalType;

import org.hibernate.engine.spi.SessionDelegatorBaseImpl;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metamodel.model.domain.AllowableParameterType;
import org.hibernate.query.Query;
import org.hibernate.query.internal.ParameterMetadataImpl;
import org.hibernate.query.internal.QueryImpl;
import org.hibernate.query.spi.QueryImplementor;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.Before;
import org.junit.Test;

public class QueryFacadeTest {
	
	private final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private Query<?> queryTarget = null;
	private IQuery queryFacade = null;
	
	@Before
	public void before() {
		queryTarget = new TestQuery<Object>();
		queryFacade = new QueryFacadeImpl(FACADE_FACTORY, queryTarget) {};
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
	
	@Test
	public void testSetParameter() {
		Object object = new Object();
		AllowableParameterType<?> allowableParameterTypeTarget = createAllowableParameterType();
		TemporalType temporalType = TemporalType.TIMESTAMP;
		IType allowableParameterTypeFacade = FACADE_FACTORY.createType(allowableParameterTypeTarget);
		IType temporalTypeFacade = FACADE_FACTORY.createType(temporalType);
		IType dummyTypeFacade = FACADE_FACTORY.createType(new Object());
		assertEquals(Integer.MIN_VALUE, ((TestQuery<?>)queryTarget).position);
		assertNull(((TestQuery<?>)queryTarget).name);
		assertNull(((TestQuery<?>)queryTarget).value);
		assertNull(((TestQuery<?>)queryTarget).allowableParameterType);
		assertNull(((TestQuery<?>)queryTarget).temporalType);
		queryFacade.setParameter(Integer.MAX_VALUE, object, allowableParameterTypeFacade);
		assertEquals(Integer.MAX_VALUE, ((TestQuery<?>)queryTarget).position);
		assertNull(((TestQuery<?>)queryTarget).name);
		assertSame(object, ((TestQuery<?>)queryTarget).value);
		assertSame(allowableParameterTypeTarget, ((TestQuery<?>)queryTarget).allowableParameterType);
		assertNull(((TestQuery<?>)queryTarget).temporalType);
		((TestQuery<?>)queryTarget).allowableParameterType = null;
		queryFacade.setParameter(Integer.MIN_VALUE, null, temporalTypeFacade);
		assertEquals(Integer.MIN_VALUE, ((TestQuery<?>)queryTarget).position);
		assertNull(((TestQuery<?>)queryTarget).name);
		assertNull(((TestQuery<?>)queryTarget).value);
		assertSame(temporalType, ((TestQuery<?>)queryTarget).temporalType);
		assertNull(((TestQuery<?>)queryTarget).allowableParameterType);
		((TestQuery<?>)queryTarget).temporalType = null;
		((TestQuery<?>)queryTarget).allowableParameterType = null;
		queryFacade.setParameter(Integer.MAX_VALUE, object, dummyTypeFacade);
		assertEquals(Integer.MAX_VALUE, ((TestQuery<?>)queryTarget).position);
		assertNull(((TestQuery<?>)queryTarget).name);
		assertSame(object, ((TestQuery<?>)queryTarget).value);
		assertNull(((TestQuery<?>)queryTarget).temporalType);
		assertNull(((TestQuery<?>)queryTarget).allowableParameterType);
		((TestQuery<?>)queryTarget).temporalType = null;
		((TestQuery<?>)queryTarget).allowableParameterType = null;
		queryFacade.setParameter("foo", null, allowableParameterTypeFacade);
		assertEquals(Integer.MAX_VALUE, ((TestQuery<?>)queryTarget).position);
		assertEquals("foo", ((TestQuery<?>)queryTarget).name);
		assertNull(((TestQuery<?>)queryTarget).value);
		assertSame(allowableParameterTypeTarget, ((TestQuery<?>)queryTarget).allowableParameterType);
		assertNull(((TestQuery<?>)queryTarget).temporalType);
		((TestQuery<?>)queryTarget).allowableParameterType = null;
		queryFacade.setParameter("bar", object, temporalTypeFacade);
		assertEquals(Integer.MAX_VALUE, ((TestQuery<?>)queryTarget).position);
		assertEquals("bar", ((TestQuery<?>)queryTarget).name);
		assertSame(object, ((TestQuery<?>)queryTarget).value);
		assertSame(temporalType, ((TestQuery<?>)queryTarget).temporalType);
		assertNull(((TestQuery<?>)queryTarget).allowableParameterType);
		((TestQuery<?>)queryTarget).temporalType = null;
		((TestQuery<?>)queryTarget).allowableParameterType = null;
		queryFacade.setParameter("baz", null, dummyTypeFacade);
		assertEquals(Integer.MAX_VALUE, ((TestQuery<?>)queryTarget).position);
		assertEquals("baz", ((TestQuery<?>)queryTarget).name);
		assertNull(((TestQuery<?>)queryTarget).value);
		assertNull(((TestQuery<?>)queryTarget).temporalType);
		assertNull(((TestQuery<?>)queryTarget).allowableParameterType);
	}
	
	private class TestQuery<R> extends QueryImpl<R> {
		
		private List<R> theList = new ArrayList<R>();
		private int maxResults = Integer.MIN_VALUE;
		private int position = Integer.MIN_VALUE;
		private String name = null;
		private Object value = null;
		private AllowableParameterType<?> allowableParameterType = null;
		private TemporalType temporalType = null;

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
		
		@Override
		public QueryImplementor<R> setParameter(int p, Object v) {
			position = p;
			value = v;
			return this;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public QueryImplementor<R> setParameter(int p, Object v, AllowableParameterType t) {
			position = p;
			value = v;
			allowableParameterType = t;
			return this;
		}
		
		@Override
		public QueryImplementor<R> setParameter(int p, Object v, TemporalType t) {
			position = p;
			value = v;
			temporalType = t;
			return this;
		}
		
		@Override
		public QueryImplementor<R> setParameter(String n, Object v) {
			name = n;
			value = v;
			return this;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public QueryImplementor<R> setParameter(String n, Object v, AllowableParameterType t) {
			name = n;
			value = v;
			allowableParameterType = t;
			return this;
		}
		
		@Override
		public QueryImplementor<R> setParameter(String n, Object v, TemporalType t) {
			name = n;
			value = v;
			temporalType = t;
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
	
	private AllowableParameterType<?> createAllowableParameterType() {
		return (AllowableParameterType<?>)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { AllowableParameterType.class }, 
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
		});
	}
	
}
