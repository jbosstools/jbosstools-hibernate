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
	
	@Override
	public IPersistentClass getPersistentClass() {
		if (persistentClass == null) {
			Object targetPersistentClass = Util.invokeMethod(
					getTarget(), 
					"getPersistentClass", 
					new Class[] {}, 
					new Object[] {});
			if (targetPersistentClass != null) {
				persistentClass = 
						getFacadeFactory().createPersistentClass(
								targetPersistentClass);
			}
		}
		return persistentClass;
	}

	@Override
	public boolean isComposite() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isComposite", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public String getPropertyAccessorName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getPropertyAccessorName", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public String getName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getName", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public boolean classIsPropertyClass() {
		return getTarget().getClass() == getPropertyClass();
	}

	protected Class<?> getPersistentClassClass() {
		return Util.getClass(getPersistentClassClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getPropertyClass() {
		return Util.getClass(getPropertyClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getPersistentClassClassName() {
		return "org.hibernate.mapping.PersistentClass";
	}

	protected String getPropertyClassName() {
		return "org.hibernate.mapping.Property";
	}

}
