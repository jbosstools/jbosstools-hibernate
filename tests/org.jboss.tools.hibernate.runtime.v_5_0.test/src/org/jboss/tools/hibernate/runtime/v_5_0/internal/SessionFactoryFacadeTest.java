package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.v_5_0.internal.test.Foo;
import org.junit.Assert;
import org.junit.Test;

public class SessionFactoryFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testClose() {
		Configuration configuration = new Configuration();
		SessionFactory sessionFactory = 
				configuration.buildSessionFactory(
						new StandardServiceRegistryBuilder().build());
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
				configuration.buildSessionFactory(
						new StandardServiceRegistryBuilder().build());
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Assert.assertTrue(sessionFactoryFacade.getAllClassMetadata().isEmpty());
		sessionFactory.close();
		configuration.addClass(Foo.class);
		sessionFactory = 
				configuration.buildSessionFactory(
						new StandardServiceRegistryBuilder().build());
		sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Map<String, IClassMetadata> allClassMetaData = 
				sessionFactoryFacade.getAllClassMetadata();
		Assert.assertNotNull(
				allClassMetaData.get(
						"org.jboss.tools.hibernate.runtime.v_5_0.internal.test.Foo"));
	}
	
}
