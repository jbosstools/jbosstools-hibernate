package org.jboss.tools.hibernate.proxy;

import java.util.ArrayList;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.AbstractClassMetadataFacade;
import org.jboss.tools.hibernate.runtime.spi.HibernateException;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.IType;

public class ClassMetadataProxy extends AbstractClassMetadataFacade {
	
	private IType[] propertyTypes = null;
	private IType identifierType = null;

	public ClassMetadataProxy(
			IFacadeFactory facadeFactory,
			ClassMetadata classMetadata) {
		super(facadeFactory, classMetadata);
	}

	public ClassMetadata getTarget() {
		return (ClassMetadata)super.getTarget();
	}

	@Override
	public String getEntityName() {
		return getTarget().getEntityName();
	}

	@Override
	public String getIdentifierPropertyName() {
		return getTarget().getIdentifierPropertyName();
	}

	@Override
	public String[] getPropertyNames() {
		return getTarget().getPropertyNames();
	}

	@Override
	public IType[] getPropertyTypes() {
		if (propertyTypes == null) {
			initializePropertyTypes();
		}
		return propertyTypes;
	}
	
	private void initializePropertyTypes() {
		Type[] origin = getTarget().getPropertyTypes();
		ArrayList<IType> propertyTypes = new ArrayList<IType>(origin.length);
		for (Type type : origin) {
			propertyTypes.add(new TypeProxy(getFacadeFactory(), type));
		}
		this.propertyTypes = propertyTypes.toArray(new IType[origin.length]);
	}

	@Override
	public Class<?> getMappedClass() {
		return getTarget().getMappedClass();
	}

	@Override
	public IType getIdentifierType() {
		if (identifierType == null) {
			identifierType = new TypeProxy(getFacadeFactory(), getTarget().getIdentifierType());
		}
		return identifierType;
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

	@Override
	public boolean isInstanceOfAbstractEntityPersister() {
		return getTarget() instanceof AbstractEntityPersister;
	}

	@Override
	public IEntityMetamodel getEntityMetamodel() {
		assert getTarget() instanceof AbstractEntityPersister;
		EntityMetamodel emm = ((AbstractEntityPersister)getTarget()).getEntityMetamodel();
		return emm != null ? new EntityMetamodelProxy(emm) : null;
	}

}
