package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public abstract class AbstractPropertyFacade 
extends AbstractFacade 
implements IProperty {

	protected IValue value = null;
	protected IPersistentClass persistentClass = null;

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

	@Override
	public void setPersistentClass(IPersistentClass persistentClass) {
		Object persistentClassTarget = Util.invokeMethod(
				persistentClass, "getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"setPersistentClass", 
				new Class[] { getPersistentClassClass() }, 
				new Object[] { persistentClassTarget });
		this.persistentClass = persistentClass;
	}
	
	protected Class<?> getPersistentClassClass() {
		return Util.getClass(getPersistentClassClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getPersistentClassClassName() {
		return "org.hibernate.mapping.PersistentClass";
	}

}
