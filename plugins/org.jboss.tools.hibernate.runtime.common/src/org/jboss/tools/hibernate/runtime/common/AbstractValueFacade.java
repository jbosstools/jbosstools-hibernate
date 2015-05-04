package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public abstract class AbstractValueFacade 
extends AbstractFacade 
implements IValue {

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
	
	protected Class<?> getCollectionClass() {
		return Util.getClass(getCollectionClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getCollectionClassName() {
		return "org.hibernate.mapping.Collection";
	}

}
