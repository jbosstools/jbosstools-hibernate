package org.jboss.tools.hibernate.proxy;

import org.hibernate.Session;
import org.jboss.tools.hibernate.runtime.common.AbstractSessionFacade;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;

public class SessionProxy extends AbstractSessionFacade {
	
	private Session target;
	private ISessionFactory targetFactory;

	public SessionProxy(
			IFacadeFactory facadeFactory,
			Session session) {
		super(facadeFactory, session);
		target = session;
	}

	@Override
	public String getEntityName(Object o) {
		return target.getEntityName(o);
	}

	@Override
	public ISessionFactory getSessionFactory() {
		if (targetFactory == null && target.getSessionFactory() != null) {
			targetFactory = new SessionFactoryProxy(getFacadeFactory(), target.getSessionFactory());
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

	public Session getTarget() {
		return (Session)super.getTarget();
	}

	public ICriteria createCriteria(Class<?> persistentClass) {
		return new CriteriaProxy(
				getFacadeFactory(),
				target.createCriteria(persistentClass));
	}
	
}
