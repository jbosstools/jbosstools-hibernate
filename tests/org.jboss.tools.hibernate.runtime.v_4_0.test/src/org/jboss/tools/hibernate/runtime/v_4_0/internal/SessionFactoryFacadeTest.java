package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.v_4_0.internal.test.Foo;
import org.junit.Assert;
import org.junit.Test;

public class SessionFactoryFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testClose() {
		Configuration configuration = new Configuration();
		SessionFactory sessionFactory = 
				configuration.buildSessionFactory(
						new ServiceRegistryBuilder().buildServiceRegistry());
		sessionFactory.openSession();
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Assert.assertFalse(sessionFactory.isClosed());
		sessionFactoryFacade.close();
		Assert.assertTrue(sessionFactory.isClosed());
	}
	
	@Test
	public void testGetAllClassMetadata() {
		Configuration configuration = new Configuration();
		ServiceRegistry serviceRegistry = 
				new ServiceRegistryBuilder().buildServiceRegistry();
		SessionFactory sessionFactory = 
				configuration.buildSessionFactory(serviceRegistry);
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Assert.assertTrue(sessionFactoryFacade.getAllClassMetadata().isEmpty());
		sessionFactory.close();
		configuration.addClass(Foo.class);
		sessionFactory = 
				configuration.buildSessionFactory(serviceRegistry);
		sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Map<String, IClassMetadata> allClassMetaData = 
				sessionFactoryFacade.getAllClassMetadata();
		Assert.assertNotNull(
				allClassMetaData.get(
						"org.jboss.tools.hibernate.runtime.v_4_0.internal.test.Foo"));
	}
	
	@Test
	public void testGetAllCollectionMetadata() {
		Configuration configuration = new Configuration();
		ServiceRegistry serviceRegistry = 
				new ServiceRegistryBuilder().buildServiceRegistry();
		SessionFactory sessionFactory = 
				configuration.buildSessionFactory(serviceRegistry);
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Assert.assertTrue(sessionFactoryFacade.getAllCollectionMetadata().isEmpty());
		sessionFactory.close();
		configuration.addClass(Foo.class);
		sessionFactory = 
				configuration.buildSessionFactory(serviceRegistry);
		sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Map<String, ICollectionMetadata> allCollectionMetaData = 
				sessionFactoryFacade.getAllCollectionMetadata();
		Assert.assertNotNull(
				allCollectionMetaData.get(
						"org.jboss.tools.hibernate.runtime.v_4_0.internal.test.Foo.bars"));
	}
	
}
