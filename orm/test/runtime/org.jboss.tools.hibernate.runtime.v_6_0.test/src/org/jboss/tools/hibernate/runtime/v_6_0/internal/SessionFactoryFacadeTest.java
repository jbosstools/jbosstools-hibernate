package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryDelegatingImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.junit.Before;
import org.junit.Test;

public class SessionFactoryFacadeTest {
	
	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private SessionFactory sessionFactoryTarget = null;
	private SessionFactoryFacadeImpl sessionFactoryFacade = null;
	
	@Before
	public void before() {
		sessionFactoryTarget = new TestSessionFactory();
		sessionFactoryFacade = new SessionFactoryFacadeImpl(FACADE_FACTORY, sessionFactoryTarget);
	}
	
	@Test
	public void testClose() {
		assertFalse(sessionFactoryTarget.isClosed());
		sessionFactoryFacade.close();
		assertTrue(sessionFactoryTarget.isClosed());
	}
	
	private static class TestSessionFactory extends SessionFactoryDelegatingImpl {

		private static final long serialVersionUID = 1L;
		
		private static SessionFactoryImplementor createDelegate() {
			return (SessionFactoryImplementor)Proxy.newProxyInstance(
					TestSessionFactory.class.getClassLoader(), 
					new Class[] { SessionFactoryImplementor.class }, 
					new InvocationHandler() {						
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							return null;
						}
					});
		}
		
		private boolean closed = false;
		
		public TestSessionFactory() {
			super(createDelegate());
		}
		
		@Override
		public boolean isClosed() {
			return closed;
		}
		
		@Override
		public void close() {
			closed = true;
		}

	}

}
