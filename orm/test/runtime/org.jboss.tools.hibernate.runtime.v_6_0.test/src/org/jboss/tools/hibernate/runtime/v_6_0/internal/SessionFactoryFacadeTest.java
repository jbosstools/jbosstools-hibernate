package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.engine.spi.SessionFactoryDelegatingImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.jboss.tools.hibernate.runtime.common.AbstractSessionFactoryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.junit.Before;
import org.junit.Test;

public class SessionFactoryFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private TestSessionFactory sessionFactoryTarget = null;
	private SessionFactoryFacadeImpl sessionFactoryFacade = null;
		
	@Before
	public void before() {
		sessionFactoryTarget = new TestSessionFactory();
		sessionFactoryFacade = new SessionFactoryFacadeImpl(FACADE_FACTORY, sessionFactoryTarget);
	}
	
	@Test
	public void testClose() {
		assertFalse(sessionFactoryTarget.closed);
		sessionFactoryFacade.close();
		assertTrue(sessionFactoryTarget.closed);
	}
	
	private Map<String, ClassMetadata> allClassMetadataTargets = new HashMap<String, ClassMetadata>();
	
	@Test
	public void testGetAllClassMetadata() throws Exception {
		TestSessionFactory testSessionFactory = (TestSessionFactory)sessionFactoryTarget;
		assertNotNull(testSessionFactory.fooClassMetadataTarget);
		assertNotNull(testSessionFactory.barClassMetadataTarget);
		assertNotNull(testSessionFactory.allClassMetadata);
		ClassMetadata fooClassMetadata = testSessionFactory.allClassMetadata.get("foo");
		assertNotNull(fooClassMetadata);
		ClassMetadata barClassMetadata = testSessionFactory.allClassMetadata.get("bar");
		assertNotNull(barClassMetadata);
		Field field = AbstractSessionFactoryFacade.class.getDeclaredField("allClassMetadata");
		field.setAccessible(true);
		assertNull(field.get(sessionFactoryFacade));
		Map<String, IClassMetadata> allClassMetadata = sessionFactoryFacade.getAllClassMetadata();
		assertNotNull(field.get(sessionFactoryFacade));
		assertEquals(2, allClassMetadata.size());
		IClassMetadata fooFacade = allClassMetadata.get("foo");
		assertNotNull(fooFacade);
	}
	
	private class TestSessionFactory extends SessionFactoryDelegatingImpl {

		private static final long serialVersionUID = 1L;
		
		private boolean closed = false;
		private ClassMetadata fooClassMetadataTarget = null;
		private ClassMetadata barClassMetadataTarget = null;
		private Map<String, ClassMetadata> allClassMetadata = new HashMap<String, ClassMetadata>();
		
		public TestSessionFactory() {
			super(createDelegate());
			allClassMetadata.put("foo", fooClassMetadataTarget = createClassMetadata("foo"));
			allClassMetadata.put("bar", barClassMetadataTarget = createClassMetadata("bar"));
		}
		
		@Override
		public void close() {
			closed = true;
		}
		
		public Map<String, ClassMetadata> getAllClassMetadata() {
			return allClassMetadata;
		}

	}
	
	private ClassMetadata createClassMetadata(final String entityName) {
		ClassMetadata result = (ClassMetadata)Proxy.newProxyInstance(
				SessionFactoryFacadeTest.class.getClassLoader(), 
				new Class[] { ClassMetadata.class },  
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if (method.getName().equals("getEntityName")) {
							return entityName;
						}
						return this;
					}
				});
		return result;
	}
	
	private SessionFactoryImplementor createDelegate() {
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
	
}
