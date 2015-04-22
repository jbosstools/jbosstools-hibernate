package org.jboss.tools.hibernate.proxy;

import org.hibernate.Session;
import org.jboss.tools.hibernate.runtime.common.AbstractSessionFacade;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;

public class SessionProxy extends AbstractSessionFacade {
	
	private ISessionFactory targetFactory;

	public SessionProxy(
			IFacadeFactory facadeFactory,
			Session session) {
		super(facadeFactory, session);
	}

	@Override
	public String getEntityName(Object o) {
		return getTarget().getEntityName(o);
	}

	@Override
	public ISessionFactory getSessionFactory() {
		if (targetFactory == null && getTarget().getSessionFactory() != null) {
			targetFactory = new SessionFactoryProxy(getFacadeFactory(), getTarget().getSessionFactory());
		}
		return targetFactory;
	}

	@Override
	public IQuery createQuery(String queryString) {
		return getFacadeFactory().createQuery(getTarget().createQuery(queryString));
	}

	@Override
	public boolean isOpen() {
		return getTarget().isOpen();
	}

	@Override
	public void close() {
		getTarget().close();
	}

	@Override
	public boolean contains(Object object) {
		return getTarget().contains(object);
	}

	public Session getTarget() {
		return (Session)super.getTarget();
	}
	
	public ICriteria createCriteria(Class<?> persistentClass) {
		return getFacadeFactory().createCriteria(
				getTarget().createCriteria(persistentClass));
	}
	
}
