package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.TemporalType;

import org.hibernate.ScrollMode;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.metamodel.model.domain.AllowableParameterType;
import org.hibernate.query.Query;
import org.hibernate.query.spi.AbstractQuery;
import org.hibernate.query.spi.MutableQueryOptions;
import org.hibernate.query.spi.ParameterMetadataImplementor;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.query.spi.QueryParameterBindings;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QueryFacadeTest {
	
	private final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private Query<Object> queryTarget = null;
	private IQuery queryFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		queryTarget = new TestQuery<Object>(null);
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
	public void testSetParameterList() {
		List<Object> list = Arrays.asList(new Object());
		AllowableParameterType<?> allowableParameterTypeTarget = createAllowableParameterType();
		IType allowableParameterTypeFacade = FACADE_FACTORY.createType(allowableParameterTypeTarget);
		IType dummyTypeFacade = FACADE_FACTORY.createType(new Object());
		assertEquals(Integer.MIN_VALUE, ((TestQuery<?>)queryTarget).position);
		assertNull(((TestQuery<?>)queryTarget).name);
		assertNull(((TestQuery<?>)queryTarget).value);
		assertNull(((TestQuery<?>)queryTarget).allowableParameterType);
		assertNull(((TestQuery<?>)queryTarget).temporalType);
		queryFacade.setParameterList("foo", list, allowableParameterTypeFacade);
		assertEquals(Integer.MIN_VALUE, ((TestQuery<?>)queryTarget).position);
		assertEquals("foo", ((TestQuery<?>)queryTarget).name);
		assertSame(list, ((TestQuery<?>)queryTarget).value);
		assertSame(allowableParameterTypeTarget, ((TestQuery<?>)queryTarget).allowableParameterType);
		assertNull(((TestQuery<?>)queryTarget).temporalType);
		((TestQuery<?>)queryTarget).allowableParameterType = null;
		((TestQuery<?>)queryTarget).value = null;
		queryFacade.setParameter("bar", list, dummyTypeFacade);
		assertEquals(Integer.MIN_VALUE, ((TestQuery<?>)queryTarget).position);
		assertEquals("bar", ((TestQuery<?>)queryTarget).name);
		assertSame(list, ((TestQuery<?>)queryTarget).value);
		assertNull(((TestQuery<?>)queryTarget).allowableParameterType);
		assertNull(((TestQuery<?>)queryTarget).temporalType);
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
	
	@Test
	public void testGetReturnAliases() {
		// TODO JBIDE-27532: Review the Query Page Viewer as the used APIs have completely changed
 		assertEquals(0, queryFacade.getReturnAliases().length);
	}
	
	@Test
	public void testGetReturnTypes() {
		// TODO JBIDE-27532: Review the Query Page Viewer as the used APIs have completely changed
		assertEquals(0, queryFacade.getReturnTypes().length);
	}
	
	private class TestQuery<R> extends AbstractQuery<R> {

		private List<R> theList = new ArrayList<R>();
		private int maxResults = Integer.MIN_VALUE;
		private int position = Integer.MIN_VALUE;
		private String name = null;
		private Object value = null;
		private AllowableParameterType<?> allowableParameterType = null;
		private TemporalType temporalType = null;

		public TestQuery(SharedSessionContractImplementor session) {
			super(session);
		}

		@Override
		protected void beforeQuery(boolean requiresTxn) {}
		
		@Override
		protected void afterQuery(boolean success) {}
		
		@Override
		public QueryParameterBindings getParameterBindings() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ScrollableResultsImplementor<R> scroll(ScrollMode scrollMode) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getQueryString() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Query<R> applyGraph(RootGraph<?> graph, GraphSemantic semantic) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T unwrap(Class<T> arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected QueryParameterBindings getQueryParameterBindings() {
			return null;
		}

		@Override
		public ParameterMetadataImplementor getParameterMetadata() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MutableQueryOptions getQueryOptions() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void applyEntityGraphQueryHint(String hintName, RootGraphImplementor<?> entityGraph) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected List<R> doList() {
			return theList;
		}

		@Override
		protected int doExecuteUpdate() {
			// TODO Auto-generated method stub
			return 0;
		}

		
		@Override
		public QueryImplementor<R> setMaxResults(int i) {
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
		
		@SuppressWarnings("rawtypes")
		@Override
		public QueryImplementor<R> setParameterList(String n, Collection c) {
			name = n;
			value = c;
			return this;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public QueryImplementor<R> setParameterList(String n, Collection c, AllowableParameterType t) {
			name = n;
			value = c;
			allowableParameterType = t;
			return this;
		}
		
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
