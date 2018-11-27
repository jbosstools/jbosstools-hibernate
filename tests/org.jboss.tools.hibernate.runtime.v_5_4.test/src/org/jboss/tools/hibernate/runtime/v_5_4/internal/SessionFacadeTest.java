package org.jboss.tools.hibernate.runtime.v_5_4.internal;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SessionFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ISession sessionFacade = null;
	
	@Before
	public void setUp() {
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		Session session = sessionFactory.openSession();
		sessionFacade = new SessionFacadeImpl(FACADE_FACTORY, session);
	}
	
	@Test
	public void testContains() {
		Assert.assertFalse(sessionFacade.contains("foo"));
	}
	
}
