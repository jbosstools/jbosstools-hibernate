package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.SessionFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractSessionFactoryFacade;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISession;

public class SessionFactoryFacadeImpl extends AbstractSessionFactoryFacade {
	
	public SessionFactoryFacadeImpl(
			IFacadeFactory facadeFactory, 
			SessionFactory sessionFactory) {
		super(facadeFactory, sessionFactory);
	}

	public SessionFactory getTarget() {
		return (SessionFactory)super.getTarget();
	}

	@Override
	public ISession openSession() {
		return getFacadeFactory().createSession(getTarget().openSession());
	}
	
	@Override
	public IClassMetadata getClassMetadata(Class<?> clazz) {
		if (allClassMetadata == null) {
			initializeAllClassMetadata();
		}
		return allClassMetadata.get(clazz.getName());
	}

	@Override
	public IClassMetadata getClassMetadata(String entityName) {
		if (allClassMetadata == null) {
			initializeAllClassMetadata();
		}
		return allClassMetadata.get(entityName);
	}

	@Override
	public ICollectionMetadata getCollectionMetadata(String string) {
		if (allCollectionMetadata == null) {
			initializeAllCollectionMetadata();
		}
		return allCollectionMetadata.get(string);
	}

}
