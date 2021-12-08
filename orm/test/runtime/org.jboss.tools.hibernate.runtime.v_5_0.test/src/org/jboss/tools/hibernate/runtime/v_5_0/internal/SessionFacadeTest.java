package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionDelegatorBaseImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.jboss.tools.hibernate.runtime.common.AbstractSessionFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SessionFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

    private static final String ENTITY_NAME = "entity_name";
	private static final SessionFactoryImplementor SESSION_FACTORY = createSessionFactory();
	private static final Query QUERY = createQuery();
	private static final Criteria CRITERIA = createCriteria();
	
	private Session sessionTarget = null;
	private AbstractSessionFacade sessionFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		sessionTarget = new TestSession();
		sessionFacade = new AbstractSessionFacade(FACADE_FACTORY, sessionTarget) {};
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
		assertSame(QUERY, ((IFacade)sessionFacade.createQuery("foobar")).getTarget());
	}
	
	@Test
	public void testIsOpen() {
		assertFalse(sessionFacade.isOpen());
		((TestSession)sessionTarget).isOpen = true;
		assertTrue(sessionFacade.isOpen());		
	}
	
	@Test
	public void testClose() {
		((TestSession)sessionTarget).isOpen = true;
		sessionFacade.close();
		assertFalse(((TestSession)sessionTarget).isOpen);
	}
	
	@Test
	public void testContains() {
		assertTrue(sessionFacade.contains("someFakeEntity"));
		assertFalse(sessionFacade.contains("anotherFakeEntity"));
		try { 
			sessionFacade.contains("bar");
		} catch (IllegalArgumentException e) {
			assertEquals("illegal", e.getMessage());
		}
	}
	
	@Test
	public void testCreateCriteria() {
		assertNull(((TestSession)sessionTarget).fromClass);
		ICriteria criteria = sessionFacade.createCriteria(Object.class);
		assertSame(Object.class, ((TestSession)sessionTarget).fromClass);
		assertSame(CRITERIA, ((IFacade)criteria).getTarget());
	}
	
	private static class TestSession extends SessionDelegatorBaseImpl {

		private static final long serialVersionUID = 1L;
		private static final Object delegate = createDelegate();
		
		private static SessionImplementor createDelegate() {
			return (SessionImplementor)Proxy.newProxyInstance(
					TestSession.class.getClassLoader(),
					new Class[] { SessionImplementor.class, Session.class }, 
					new InvocationHandler() {						
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							return null;
						}
					});
		}
		
		private boolean isOpen = false;
		private Class<?> fromClass = null;
		
		public TestSession() {
			super((SessionImplementor)delegate, (Session)delegate);
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
		public Query createQuery(String queryString) {
			return QUERY;
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
			if (object.equals("bar")) {
				throw new IllegalArgumentException("illegal");
			} else if (object.equals("someFakeEntity")) {
				return true;
			} else {
				return false;
			}
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public Criteria createCriteria(Class persistentClass) {
			fromClass = persistentClass;
			return CRITERIA;
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
	
	private static Query createQuery() {
		return (Query)Proxy.newProxyInstance(
				SessionFacadeTest.class.getClassLoader(), 
				new Class[] { Query.class }, 
				new InvocationHandler() {		
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
				});
	}
	
	private static Criteria createCriteria() {
		return (Criteria)Proxy.newProxyInstance(
				SessionFacadeTest.class.getClassLoader(), 
				new Class[] { Criteria.class }, 
				new InvocationHandler() {		
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
				});
	}
	
}
