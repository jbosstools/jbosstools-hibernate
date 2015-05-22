package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.SessionFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractSessionFactoryFacade;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

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
