package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.EntityMode;
import org.hibernate.engine.SessionImplementor;
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
		return getTarget().getMappedClass(EntityMode.POJO);
	}

	@Override
	public Object getPropertyValue(Object object, String name) throws HibernateException {
		try {
			return getTarget().getPropertyValue(object, name, EntityMode.POJO);
		} catch (org.hibernate.HibernateException e) {
			throw new HibernateException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public boolean hasIdentifierProperty() {
		return getTarget().hasIdentifierProperty();
	}

}
