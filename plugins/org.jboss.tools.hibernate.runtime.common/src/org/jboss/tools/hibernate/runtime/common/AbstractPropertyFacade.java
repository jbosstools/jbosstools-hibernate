package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public abstract class AbstractPropertyFacade 
extends AbstractFacade 
implements IProperty {

	protected IValue value = null;

	public AbstractPropertyFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public IValue getValue() {
		if (value == null) {
			Object targetValue = Util.invokeMethod(
					getTarget(), 
					"getValue", 
					new Class[] {}, 
					new Object[] {});
			if (targetValue != null) {
				value = getFacadeFactory().createValue(targetValue);
			}
		}
		return value;
	}

	@Override
	public void setName(String name) {
		Util.invokeMethod(
				getTarget(), 
				"setName", 
				new Class[] { String.class }, 
				new Object[] { name });
	}

}
