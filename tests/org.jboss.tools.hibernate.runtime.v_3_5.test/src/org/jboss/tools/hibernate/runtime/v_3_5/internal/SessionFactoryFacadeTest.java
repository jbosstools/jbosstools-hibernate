package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.v_3_5.internal.test.Foo;
import org.junit.Assert;
import org.junit.Test;

public class SessionFactoryFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testClose() {
		Configuration configuration = new Configuration();
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		sessionFactory.openSession();
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Assert.assertFalse(sessionFactory.isClosed());
		sessionFactoryFacade.close();
		Assert.assertTrue(sessionFactory.isClosed());
	}
	
	@Test
	public void testGetAllClassMetadata() throws Exception {
		Configuration configuration = new Configuration();
		SessionFactory sessionFactory = 
				configuration.buildSessionFactory();
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Assert.assertTrue(sessionFactoryFacade.getAllClassMetadata().isEmpty());
		sessionFactory.close();
		configuration.addClass(Foo.class);
		sessionFactory = 
				configuration.buildSessionFactory();
		sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Map<String, IClassMetadata> allClassMetaData = 
				sessionFactoryFacade.getAllClassMetadata();
		Assert.assertNotNull(
				allClassMetaData.get(
						"org.jboss.tools.hibernate.runtime.v_3_5.internal.test.Foo"));
	}
	
	@Test
	public void testGetAllCollectionMetadata() {
		Configuration configuration = new Configuration();
		SessionFactory sessionFactory = 
				configuration.buildSessionFactory();
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Assert.assertTrue(sessionFactoryFacade.getAllCollectionMetadata().isEmpty());
		sessionFactory.close();
		configuration.addClass(Foo.class);
		sessionFactory = 
				configuration.buildSessionFactory();
		sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Map<String, ICollectionMetadata> allCollectionMetaData = 
				sessionFactoryFacade.getAllCollectionMetadata();
		Assert.assertNotNull(
				allCollectionMetaData.get(
						"org.jboss.tools.hibernate.runtime.v_3_5.internal.test.Foo.bars"));
	}
	
	@Test
	public void testOpenSession() {
		Configuration configuration = new Configuration();
		SessionFactory sessionFactory = 
				configuration.buildSessionFactory();
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		ISession sessionFacade = sessionFactoryFacade.openSession();
		Session session = (Session)((IFacade)sessionFacade).getTarget();
		Assert.assertSame(sessionFactory, session.getSessionFactory());
	}
	
	@Test
	public void testGetClassMetadata() {
		Configuration configuration = new Configuration();
		configuration.addClass(Foo.class);
		SessionFactory sessionFactory = 
				configuration.buildSessionFactory();
		ClassMetadata classMetadata = sessionFactory.getClassMetadata(Foo.class);
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Assert.assertSame(
				classMetadata, 
				((IFacade)sessionFactoryFacade.getClassMetadata(Foo.class)).getTarget());
		Assert.assertSame(
				classMetadata, 
				((IFacade)sessionFactoryFacade.getClassMetadata(
						"org.jboss.tools.hibernate.runtime.v_3_5.internal.test.Foo"))
					.getTarget());
	}
	
	@Test
	public void testGetCollectionMetadata() {
		Configuration configuration = new Configuration();
		configuration.addClass(Foo.class);
		SessionFactory sessionFactory = 
				configuration.buildSessionFactory();
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		CollectionMetadata collectionMetadata = sessionFactory.getCollectionMetadata(
				"org.jboss.tools.hibernate.runtime.v_3_5.internal.test.Foo.bars");
		Assert.assertSame(
				collectionMetadata, 
				((IFacade)sessionFactoryFacade.getCollectionMetadata(
						"org.jboss.tools.hibernate.runtime.v_3_5.internal.test.Foo.bars"))
					.getTarget());
	}
	
}
