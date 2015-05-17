package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public abstract class AbstractPropertyFacade 
extends AbstractFacade 
implements IProperty {

	protected IValue value = null;
	protected IPersistentClass persistentClass = null;
	protected IType type = null;	

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
	public String getNodeName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getNodeName", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public boolean classIsPropertyClass() {
		return getTarget().getClass() == getPropertyClass();
	}

	@Override
	public IType getType() {
		if (type == null) {
			Object targetType = Util.invokeMethod(
					getTarget(), 
					"getType", 
					new Class[] {}, 
					new Object[] {});
			if (targetType != null) {
				type = getFacadeFactory().createType(targetType);
			}
		}
		return type;
	}

	@Override
	public void setValue(IValue value) {
		Object valueTarget = Util.invokeMethod(
				value, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"setValue", 
				new Class[] { getValueClass() }, 
				new Object[] { valueTarget });
		this.value = value;
	}

	@Override
	public void setPropertyAccessorName(String string) {
		Util.invokeMethod(
				getTarget(), 
				"setPropertyAccessorName", 
				new Class[] { String.class }, 
				new Object[] { string });
	}

	@Override
	public void setCascade(String string) {
		Util.invokeMethod(
				getTarget(), 
				"setCascade", 
				new Class[] { String.class }, 
				new Object[] { string });
	}

	@Override
	public boolean isBackRef() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isBackRef", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public boolean isSelectable() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isSelectable", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public boolean isInsertable() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isInsertable", 
				new Class[] {}, 
				new Object[] {});
	}

	protected Class<?> getPersistentClassClass() {
		return Util.getClass(getPersistentClassClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getPropertyClass() {
		return Util.getClass(getPropertyClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getValueClass() {
		return Util.getClass(getValueClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getPersistentClassClassName() {
		return "org.hibernate.mapping.PersistentClass";
	}

	protected String getPropertyClassName() {
		return "org.hibernate.mapping.Property";
	}

	protected String getValueClassName() {
		return "org.hibernate.mapping.Value";
	}

}
