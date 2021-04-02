package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryDelegatingImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.jboss.tools.hibernate.runtime.common.AbstractSessionFactoryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SessionFactoryFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private TestSessionFactory sessionFactoryTarget = null;
	private SessionFactoryFacadeImpl sessionFactoryFacade = null;
		
	@BeforeEach
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
		Field field = AbstractSessionFactoryFacade.class.getDeclaredField("allClassMetadata");
		field.setAccessible(true);
		assertNull(field.get(sessionFactoryFacade));
		Map<String, IClassMetadata> allClassMetadata = sessionFactoryFacade.getAllClassMetadata();
		assertNotNull(field.get(sessionFactoryFacade));
		assertEquals(3, allClassMetadata.size());
		assertSame(
				((TestSessionFactory)sessionFactoryTarget).fooClassMetadataTarget, 
				((IFacade)allClassMetadata.get("foo")).getTarget());
		assertSame(
				((TestSessionFactory)sessionFactoryTarget).barClassMetadataTarget, 
				((IFacade)allClassMetadata.get("bar")).getTarget());
	}
	
	@Test
	public void testGetAllCollectionMetadata() throws Exception {
		Field field = AbstractSessionFactoryFacade.class.getDeclaredField("allCollectionMetadata");
		field.setAccessible(true);
		assertNull(field.get(sessionFactoryFacade));
		Map<String, ICollectionMetadata> allCollectionMetadata = sessionFactoryFacade.getAllCollectionMetadata();
		assertNotNull(field.get(sessionFactoryFacade));
		assertEquals(2, allCollectionMetadata.size());
		assertSame(
				((TestSessionFactory)sessionFactoryTarget).childCollectionMetadataTarget, 
				((IFacade)allCollectionMetadata.get("child")).getTarget());
		assertSame(
				((TestSessionFactory)sessionFactoryTarget).parentCollectionMetadataTarget, 
				((IFacade)allCollectionMetadata.get("parent")).getTarget());
	}
	
	@Test
	public void testOpenSession() throws Exception {
		assertNull(sessionFactoryTarget.session);
		ISession sessionFacade = sessionFactoryFacade.openSession();
		assertNotNull(sessionFactoryTarget.session);
		assertSame(sessionFactoryTarget.session, ((IFacade)sessionFacade).getTarget());
	}
	
	@Test
	public void testGetClassMetadata() throws Exception {
		Field field = AbstractSessionFactoryFacade.class.getDeclaredField("allClassMetadata");
		field.setAccessible(true);
		assertNull(field.get(sessionFactoryFacade));
		assertSame(
				sessionFactoryTarget.objectClassMetadataTarget,
				((IFacade)sessionFactoryFacade.getClassMetadata(Object.class)).getTarget());
		assertNotNull(field.get(sessionFactoryFacade));
		field.set(sessionFactoryFacade, null);
		assertSame(
				sessionFactoryTarget.fooClassMetadataTarget,
				((IFacade)sessionFactoryFacade.getClassMetadata("foo")).getTarget());
		assertNotNull(field.get(sessionFactoryFacade));
	}
	
	@Test
	public void testGetCollectionMetadata() throws Exception {
		Field field = AbstractSessionFactoryFacade.class.getDeclaredField("allCollectionMetadata");
		field.setAccessible(true);
		assertNull(field.get(sessionFactoryFacade));
		assertSame(
				sessionFactoryTarget.childCollectionMetadataTarget,
				((IFacade)sessionFactoryFacade.getCollectionMetadata("child")).getTarget());
		assertNotNull(field.get(sessionFactoryFacade));
	}	
	
	private class TestSessionFactory extends SessionFactoryDelegatingImpl {

		private static final long serialVersionUID = 1L;
		
		private boolean closed = false;
		private Session session = null;
		private ClassMetadata fooClassMetadataTarget = null;
		private ClassMetadata barClassMetadataTarget = null;
		private ClassMetadata objectClassMetadataTarget = null;
		private Map<String, ClassMetadata> allClassMetadata = new HashMap<String, ClassMetadata>();
		private CollectionMetadata childCollectionMetadataTarget = null;
		private CollectionMetadata parentCollectionMetadataTarget = null;
		private Map<String, CollectionMetadata> allCollectionMetadata = new HashMap<String, CollectionMetadata>();
		
		public TestSessionFactory() {
			super(createDelegate());
			allClassMetadata.put("foo", fooClassMetadataTarget = createClassMetadata("foo"));
			allClassMetadata.put("bar", barClassMetadataTarget = createClassMetadata("bar"));
			allClassMetadata.put(Object.class.getName(), objectClassMetadataTarget = createClassMetadata(Object.class.getName()));
			allCollectionMetadata.put("child", childCollectionMetadataTarget = createCollectionMetadata("child"));
			allCollectionMetadata.put("parent", parentCollectionMetadataTarget = createCollectionMetadata("parent"));
		}
		
		@Override
		public void close() {
			closed = true;
		}
		
		@Override
		public Map<String, ClassMetadata> getAllClassMetadata() {
			return allClassMetadata;
		}
		
		@Override
		public Map<String, CollectionMetadata> getAllCollectionMetadata() {
			return allCollectionMetadata;
		}
		
		@Override
		public Session openSession() {
			return session = createSession();
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
	
	private CollectionMetadata createCollectionMetadata(final String role) {
		CollectionMetadata result = (CollectionMetadata)Proxy.newProxyInstance(
				SessionFactoryFacadeTest.class.getClassLoader(), 
				new Class[] { CollectionMetadata.class },  
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if (method.getName().equals("getRole")) {
							return role;
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
	
	private Session createSession() {
		return (Session)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { Session.class }, 
				new InvocationHandler() {				
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
				});
	}
	
}
