package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public abstract class AbstractValueFacade 
extends AbstractFacade 
implements IValue {

	protected IValue collectionElement = null;

	public AbstractValueFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public boolean isSimpleValue() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isSimpleValue", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public boolean isCollection() {
		return getCollectionClass().isAssignableFrom(getTarget().getClass());
	}
	
	@Override
	public IValue getCollectionElement() {
		if (isCollection() && collectionElement == null) {
			Object targetElement = Util.invokeMethod(
					getTarget(), 
					"getElement", 
					new Class[] {}, 
					new Object[] {});
			if (targetElement != null) {
				collectionElement = getFacadeFactory().createValue(targetElement);
			}
		}
		return collectionElement;
	}

	@Override
	public boolean isOneToMany() {
		return getOneToManyClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isManyToOne() {
		return getManyToOneClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isOneToOne() {
		return getOneToOneClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isMap() {
		return getMapClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isComponent() {
		return getComponentClass().isAssignableFrom(getTarget().getClass());
	}

	protected Class<?> getCollectionClass() {
		return Util.getClass(collectionClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getOneToManyClass() {
		return Util.getClass(oneToManyClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getManyToOneClass() {
		return Util.getClass(manyToOneClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getOneToOneClass() {
		return Util.getClass(oneToOneClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getMapClass() {
		return Util.getClass(mapClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getComponentClass() {
		return Util.getClass(componentClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String collectionClassName() {
		return "org.hibernate.mapping.Collection";
	}
	
	protected String oneToManyClassName() {
		return "org.hibernate.mapping.OneToMany";
	}

	protected String manyToOneClassName() {
		return "org.hibernate.mapping.ManyToOne";
	}

	protected String oneToOneClassName() {
		return "org.hibernate.mapping.OneToOne";
	}

	protected String mapClassName() {
		return "org.hibernate.mapping.Map";
	}

	protected String componentClassName() {
		return "org.hibernate.mapping.Component";
	}

}
