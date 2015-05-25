package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.Session;
import org.jboss.tools.hibernate.runtime.common.AbstractSessionFacade;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQuery;

public class SessionFacadeImpl extends AbstractSessionFacade {
	
	public SessionFacadeImpl(
			IFacadeFactory facadeFactory,
			Session session) {
		super(facadeFactory, session);
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
