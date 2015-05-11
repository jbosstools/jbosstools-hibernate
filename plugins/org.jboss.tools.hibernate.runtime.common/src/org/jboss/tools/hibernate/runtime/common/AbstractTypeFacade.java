package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;

public abstract class AbstractTypeFacade 
extends AbstractFacade 
implements IType {

	public AbstractTypeFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}
	
	@Override
	public String toString(Object value) {
		String result = null;
		if (getStringRepresentableTypeClass().isAssignableFrom(
				getTarget().getClass())) {
			result = (String)Util.invokeMethod(
					getTarget(), 
					"toString", 
					new Class[] { Object.class }, 
					new Object[] { value });
		}
		return result;
	}
	
	@Override
	public String getName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getName", 
				new Class[] {}, 
				new Object[] {});
	}

	protected Class<?> getStringRepresentableTypeClass() {
		return Util.getClass(
				getStringRepresentableTypeClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected String getStringRepresentableTypeClassName() {
		return "org.hibernate.type.StringRepresentableType";
	}

}
