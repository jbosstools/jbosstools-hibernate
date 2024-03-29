package org.jboss.tools.hibernate.runtime.common;

import java.util.ArrayList;

import org.jboss.tools.hibernate.runtime.spi.HibernateException;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.IType;

public abstract class AbstractClassMetadataFacade 
extends AbstractFacade 
implements IClassMetadata {

	protected IType[] propertyTypes = null;
	protected IType identifierType = null;

	public AbstractClassMetadataFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public Class<?> getMappedClass() {
		return (Class<?>)Util.invokeMethod(
				getTarget(), 
				"getMappedClass", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public Object getPropertyValue(Object object, String name) throws HibernateException {
		try {
			return Util.invokeMethod(
					getTarget(), 
					"getPropertyValue", 
					new Class[] { Object.class, String.class }, 
					new Object[] { object, name });
		} catch (Throwable t) {
			throw new HibernateException(t.getMessage(), t.getCause());
		}
	}

	@Override
	public String getEntityName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getEntityName", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public String getIdentifierPropertyName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getIdentifierPropertyName", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public String[] getPropertyNames() {
		return (String[])Util.invokeMethod(
				getTarget(), 
				"getPropertyNames", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public IType[] getPropertyTypes() {
		if (propertyTypes == null) {
			initializePropertyTypes();
		}
		return propertyTypes;
	}
	
	@Override
	public IType getIdentifierType() {
		if (identifierType == null) {
			identifierType = getFacadeFactory().createType(
					Util.invokeMethod(
							getTarget(), 
							"getIdentifierType", 
							new Class[] {}, 
							new Object[] {}));
		}
		return identifierType;
	}

	@Override
	public boolean isInstanceOfAbstractEntityPersister() {
		return getAbstractEntityPersisterClass().isAssignableFrom(getTarget().getClass());
	}
	
	@Override
	public Object getIdentifier(Object object, ISession session) {
		Object sessionImplementor = Util.invokeMethod(
				session, 
				"getTarget",
				new Class[] {}, 
				new Object[] {});
		return Util.invokeMethod(
				getTarget(), 
				"getIdentifier", 
				new Class[] { Object.class, getSessionImplementorClass() }, 
				new Object[] { object, sessionImplementor });
	}
	
	@Override
	public boolean hasIdentifierProperty() {
		return (Boolean)Util.invokeMethod(
				getTarget(), 
				"hasIdentifierProperty", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public Integer getPropertyIndexOrNull(String id) {
		Integer result = null;
		Object entityMetamodel = null;
		if (isInstanceOfAbstractEntityPersister()) {
			entityMetamodel = Util.invokeMethod(
				getTarget(), 
				"getEntityMetamodel", 
				new Class[] {}, 
				new Object[] {});
		}
		if (entityMetamodel != null) {
			result = (Integer)Util.invokeMethod(
					entityMetamodel, 
					"getPropertyIndexOrNull", 
					new Class[] { String.class }, 
					new Object[] { id });
		} 
		return result;
	}
	
	@Override
	public Object getTuplizerPropertyValue(Object entity, int i) {
		Object result = null;
		Object entityMetamodel = null;
		if (isInstanceOfAbstractEntityPersister()) {
			entityMetamodel = Util.invokeMethod(
				getTarget(), 
				"getEntityMetamodel", 
				new Class[] {}, 
				new Object[] {});
		}
		if (entityMetamodel != null) {
			Object targetTuplizer = Util.invokeMethod(
					entityMetamodel, 
					"getTuplizer", 
					new Class[] {}, 
					new Object[] {});
			if (targetTuplizer != null) {
				result = Util.invokeMethod(
						targetTuplizer, 
						"getPropertyValue", 
						new Class[] { Object.class,  int.class }, 
						new Object[] { entity, i });
			}
		}
		return result;
	}

	protected Class<?> getSessionImplementorClass() {
		return Util.getClass(getSessionImplementorClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getSessionImplementorClassName() {
		return "org.hibernate.engine.spi.SessionImplementor";
	}

	protected Class<?> getAbstractEntityPersisterClass() {
		return Util.getClass(getAbstractEntityPersisterClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getAbstractEntityPersisterClassName() {
		return "org.hibernate.persister.entity.AbstractEntityPersister";
	}

	protected void initializePropertyTypes() {
		Object[] originTypes = (Object[])Util.invokeMethod(
				getTarget(), 
				"getPropertyTypes", 
				new Class[] {}, 
				new Object[] {});
		ArrayList<IType> propertyTypes = new ArrayList<IType>(originTypes.length);
		for (Object type : originTypes) {
			propertyTypes.add(getFacadeFactory().createType(type));
		}
		this.propertyTypes = propertyTypes.toArray(new IType[originTypes.length]);
	}

}
