package org.jboss.tools.hibernate.runtime.v_4_0.internal;

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
import org.hibernate.engine.spi.SessionFactoryImplementor;
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
	
	private TestInvocationHandler invocationHandler = new TestInvocationHandler();
	
	@BeforeEach
	public void beforeEach() {
		sessionTarget = createTestSession();
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
		invocationHandler.isOpen = true;
		assertTrue(sessionFacade.isOpen());		
	}
	
	@Test
	public void testClose() {
		invocationHandler.isOpen = true;
		sessionFacade.close();
		assertFalse(invocationHandler.isOpen);
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
		assertNull(invocationHandler.fromClass);
		ICriteria criteria = sessionFacade.createCriteria(Object.class);
		assertSame(Object.class, invocationHandler.fromClass);
		assertSame(CRITERIA, ((IFacade)criteria).getTarget());
	}
	
	
	private Session createTestSession() {
		return (Session)Proxy.newProxyInstance(
					getClass().getClassLoader(), 
					new Class[] { Session.class }, 
					invocationHandler);
	}
	
	private class TestInvocationHandler implements InvocationHandler {
			
		private boolean isOpen = false;
		private Class<?> fromClass = null;

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			final String methodName = method.getName();
			if ("getEntityName".equals(methodName)) {
				return ENTITY_NAME;
			} else if ("getSessionFactory".equals(methodName)) {
				return SESSION_FACTORY;
			} else if ("createQuery".equals(methodName)) {
				return QUERY;
			} else if ("isOpen".equals(methodName)) {
				return isOpen;
			} else if ("close".equals(methodName)) {
				isOpen = false;
			} else if ("contains".equals(methodName)) {
				return contains(args[0]);
			} else if ("createCriteria".equals(methodName)) {
				fromClass = (Class<?>)args[0];
				return CRITERIA;
			}
			return null;
		}
		
		private boolean contains(Object object) {
			if (object.equals("bar")) {
				throw new IllegalArgumentException("illegal");
			} else if (object.equals("someFakeEntity")) {
				return true;
			} else {
				return false;
			}
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
