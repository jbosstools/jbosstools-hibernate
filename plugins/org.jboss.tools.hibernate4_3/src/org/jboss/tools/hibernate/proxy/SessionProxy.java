package org.jboss.tools.hibernate.proxy;

import org.hibernate.Session;
import org.jboss.tools.hibernate.spi.IQuery;
import org.jboss.tools.hibernate.spi.ISession;
import org.jboss.tools.hibernate.spi.ISessionFactory;
import org.jboss.tools.hibernate.spi.ISessionImplementor;

public class SessionProxy implements ISession, ISessionImplementor {
	
	private Session target;
	private ISessionFactory targetFactory;

	public SessionProxy(ISessionFactory sessionFactory, Session session) {
		target = session;
		targetFactory = sessionFactory;
	}

	@Override
	public String getEntityName(Object o) {
		return target.getEntityName(o);
	}

	@Override
	public ISessionFactory getSessionFactory() {
		return targetFactory;
	}

	@Override
	public IQuery createQuery(String queryString) {
		return new QueryProxy(target.createQuery(queryString));
	}

	@Override
	public boolean isOpen() {
		return target.isOpen();
	}

	@Override
	public void close() {
		target.close();
	}

	@Override
	public boolean contains(Object object) {
		return target.contains(object);
	}

	@Override
	public ISessionImplementor getSessionImplementor() {
		return this;
	}
	
	Session getTarget() {
		return target;
	}
}
