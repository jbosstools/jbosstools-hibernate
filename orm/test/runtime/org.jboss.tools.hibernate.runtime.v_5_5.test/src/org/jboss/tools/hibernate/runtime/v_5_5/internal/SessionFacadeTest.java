package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionDelegatorBaseImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.query.spi.QueryImplementor;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SessionFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

    private static final String ENTITY_NAME = "entity_name";
	private static final SessionFactoryImplementor SESSION_FACTORY = createSessionFactory();
	private static final QueryImplementor<?> QUERY_IMPLEMENTOR = createQueryImplementor();
	private static final Root<?> ROOT = createRoot();
	
	private Session sessionTarget = null;
	private SessionFacadeImpl sessionFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		sessionTarget = new TestSession();
		sessionFacade = new SessionFacadeImpl(FACADE_FACTORY, sessionTarget);
	}
	
	@Test
	public void testGetEntityName() {
		assertSame(ENTITY_NAME, sessionFacade.getEntityName(new Object()));
	}
	
	@Test
	public void testGetSessionFactory() {
		assertSame(SESSION_FACTORY, ((IFacade)sessionFacade.getSessionFactory()).getTarget());
	}
	
	@Test
	public void testCreateQuery() {
		assertSame(QUERY_IMPLEMENTOR, ((IFacade)sessionFacade.createQuery("foobar")).getTarget());
	}
	
	private static class TestSession extends SessionDelegatorBaseImpl {

		private static final long serialVersionUID = 1L;
		
		private static SessionImplementor createDelegate() {
			return (SessionImplementor)Proxy.newProxyInstance(
					TestSession.class.getClassLoader(),
					new Class[] { SessionImplementor.class }, 
					new InvocationHandler() {						
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							return null;
						}
					});
		}
		
		private boolean isOpen = false;
		private CriteriaBuilder criteriaBuilder = null;
		private CriteriaQuery<?> criteriaQuery = null;
		private Class<?> fromClass = null;
		private Root<?> root = null;
		
		public TestSession() {
			super(createDelegate());
		}
		
		@Override
		public String getEntityName(Object o) {
			return ENTITY_NAME;
		}
		
		@Override
		public SessionFactoryImplementor getSessionFactory() {
			return SESSION_FACTORY;
		}
		
		@Override
		public QueryImplementor<?> createQuery(String queryString) {
			return QUERY_IMPLEMENTOR;
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public QueryImplementor<?> createQuery(CriteriaQuery criteria) {
			criteriaQuery = criteria;
			return QUERY_IMPLEMENTOR;
		}
		
		@Override
		public boolean isOpen() {
			return isOpen;
		}
		
		@Override
		public void close() {
			isOpen = false;
		}
		
		@Override
		public boolean contains(Object object) {
			if (object.equals("foo")) {
				throw new IllegalArgumentException("Not an entity [" + object + "]");
			} else if (object.equals("bar")) {
				throw new IllegalArgumentException("illegal");
			} else if (object.equals("someFakeEntity")) {
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		public CriteriaBuilder getCriteriaBuilder() {
			criteriaBuilder = createCriteriaBuilder();
			return criteriaBuilder;
		}
		
		private CriteriaBuilder createCriteriaBuilder() {
			return (CriteriaBuilder)Proxy.newProxyInstance(
					SessionFacadeTest.class.getClassLoader(), 
					new Class[] { CriteriaBuilder.class }, 
					new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if ("createQuery".equals(method.getName())) {
							return createCriteriaQuery();
						}
						return null;
					}
			});
		}
		
		private CriteriaQuery<?> createCriteriaQuery() {
			return (CriteriaQuery<?>)Proxy.newProxyInstance(
					SessionFacadeTest.class.getClassLoader(), 
					new Class[] { CriteriaQuery.class }, 
					new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if ("from".equals(method.getName())) {
							fromClass = (Class<?>)args[0];
							return ROOT;
						} else if ("select".equals(method.getName())) {
							root = (Root<?>)args[0];							
						}
						return null;
					}
			});
		}
		
	}
	
	private static SessionFactoryImplementor createSessionFactory() {
		return (SessionFactoryImplementor)Proxy.newProxyInstance(
				SessionFacadeTest.class.getClassLoader(), 
				new Class[] { SessionFactoryImplementor.class }, 
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
		});
	}
	
	private static QueryImplementor<?> createQueryImplementor() {
		return (QueryImplementor<?>)Proxy.newProxyInstance(
				SessionFacadeTest.class.getClassLoader(), 
				new Class[] { QueryImplementor.class }, 
				new InvocationHandler() {		
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
				});
	}
	
	private static Root<?> createRoot() {
		return (Root<?>)Proxy.newProxyInstance(
				SessionFacadeTest.class.getClassLoader(), 
				new Class[] { Root.class }, 
				new InvocationHandler() {		
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
				});
	}
	
}
