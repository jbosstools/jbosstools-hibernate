package org.jboss.tools.hibernate.proxy;

import org.hibernate.Session;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;

public class SessionProxy implements ISession {
	
	private Session target;
	private ISessionFactory targetFactory;
	private IFacadeFactory facadeFactory = null;

	public SessionProxy(
			IFacadeFactory facadeFactory,
			Session session) {
		target = session;
		this.facadeFactory = facadeFactory;
	}

	@Override
	public String getEntityName(Object o) {
		return target.getEntityName(o);
	}

	@Override
	public ISessionFactory getSessionFactory() {
		if (targetFactory == null && target.getSessionFactory() != null) {
			targetFactory = new SessionFactoryProxy(facadeFactory, target.getSessionFactory());
		}
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

	Session getTarget() {
		return target;
	}

	public ICriteria createCriteria(Class<?> persistentClass) {
		return new CriteriaProxy(
				facadeFactory,
				target.createCriteria(persistentClass));
	}
	
}
