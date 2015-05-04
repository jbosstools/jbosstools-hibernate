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

	protected Class<?> getCollectionClass() {
		return Util.getClass(getCollectionClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getOneToManyClass() {
		return Util.getClass(getOneToManyClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getCollectionClassName() {
		return "org.hibernate.mapping.Collection";
	}
	
	protected String getOneToManyClassName() {
		return "org.hibernate.mapping.OneToMany";
	}

}
