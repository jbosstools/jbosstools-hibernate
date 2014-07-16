package org.hibernate.console.proxy;

import org.hibernate.SessionFactory;
import org.hibernate.console.spi.HibernateSessionFactory;

public class SessionFactoryProxy implements HibernateSessionFactory {
	
	private SessionFactory sessionFactory = null;
	
	public SessionFactoryProxy(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void close() {
		sessionFactory.close();
	}

}
