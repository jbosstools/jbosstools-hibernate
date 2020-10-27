package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionDelegatorBaseImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.junit.Before;
import org.junit.Test;

public class SessionFacadeTest {
	
	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private static final String ENTITY_NAME = "entity_name";
	private static final SessionFactoryImplementor SESSION_FACTORY = createSessionFactory();
	
	private Session sessionTarget = null;
	private SessionFacadeImpl sessionFacade = null;
	
	@Before
	public void before() {
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

	}
	
	private static SessionFactoryImplementor createSessionFactory() {
		return (SessionFactoryImplementor)Proxy.newProxyInstance(
				SessionFactoryFacadeTest.class.getClassLoader(), 
				new Class[] { SessionFactoryImplementor.class }, 
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						// TODO Auto-generated method stub
						return null;
					}
		});
	}
	
}
