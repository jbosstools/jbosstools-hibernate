package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SessionFactoryFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ISessionFactory sessionFactoryFacade = null;
	
	@Before
	public void setUp() {
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		sessionFactoryFacade = new SessionFactoryFacadeImpl(FACADE_FACTORY, sessionFactory);
	}
	
	@Test
	public void testGetAllClassMetadata() {
		Assert.assertNotNull(sessionFactoryFacade.getAllClassMetadata());
	}
	
	@Test
	public void testGetAllCollectionMetadata() {
		Assert.assertNotNull(sessionFactoryFacade.getAllCollectionMetadata());
	}
	
}
