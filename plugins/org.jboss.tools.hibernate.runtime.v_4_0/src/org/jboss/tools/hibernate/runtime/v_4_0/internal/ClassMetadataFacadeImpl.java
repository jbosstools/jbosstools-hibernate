package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.jboss.tools.hibernate.proxy.SessionProxy;
import org.jboss.tools.hibernate.runtime.common.AbstractClassMetadataFacade;
import org.jboss.tools.hibernate.runtime.spi.HibernateException;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISession;

public class ClassMetadataFacadeImpl extends AbstractClassMetadataFacade {
	
	public ClassMetadataFacadeImpl(
			IFacadeFactory facadeFactory,
			ClassMetadata classMetadata) {
		super(facadeFactory, classMetadata);
	}

	public ClassMetadata getTarget() {
		return (ClassMetadata)super.getTarget();
	}

	@Override
	public Class<?> getMappedClass() {
		return getTarget().getMappedClass();
	}

	@Override
	public Object getPropertyValue(Object object, String name) throws HibernateException {
		try {
			return getTarget().getPropertyValue(object, name);
		} catch (org.hibernate.HibernateException e) {
			throw new HibernateException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public boolean hasIdentifierProperty() {
		return getTarget().hasIdentifierProperty();
	}

	@Override
	public Object getIdentifier(Object object, ISession session) {
		Object result = null;
		if (session instanceof SessionProxy) {
			SessionImplementor impl = (SessionImplementor)((SessionProxy)session).getTarget();
			result = getTarget().getIdentifier(object, impl);
		}
		return result;
	}

}
