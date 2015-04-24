package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.EntityMode;
import org.hibernate.metadata.ClassMetadata;
import org.jboss.tools.hibernate.runtime.common.AbstractClassMetadataFacade;
import org.jboss.tools.hibernate.runtime.spi.HibernateException;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class ClassMetadataFacadeImpl extends AbstractClassMetadataFacade {
	
	public ClassMetadataFacadeImpl(
			IFacadeFactory facadeFactory,
			ClassMetadata classMetadata) {
		super(facadeFactory, classMetadata);
	}

	@Override
	public Class<?> getMappedClass() {
		return ((ClassMetadata)getTarget()).getMappedClass(EntityMode.POJO);
	}

	@Override
	public Object getPropertyValue(Object object, String name) throws HibernateException {
		try {
			return ((ClassMetadata)getTarget()).getPropertyValue(
					object, name, EntityMode.POJO);
		} catch (org.hibernate.HibernateException e) {
			throw new HibernateException(e.getMessage(), e.getCause());
		}
	}

	protected String getSessionImplementorClassName() {
		return "org.hibernate.engine.SessionImplementor";
	}

}
