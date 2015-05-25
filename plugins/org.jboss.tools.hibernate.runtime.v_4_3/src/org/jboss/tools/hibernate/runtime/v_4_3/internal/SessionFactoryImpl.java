package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.Session;
import org.jboss.tools.hibernate.runtime.common.AbstractSessionFacade;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class SessionFactoryImpl extends AbstractSessionFacade {
	
	public SessionFactoryImpl(
			IFacadeFactory facadeFactory,
			Session session) {
		super(facadeFactory, session);
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
