package org.jboss.tools.hibernate.proxy;

import org.hibernate.mapping.Property;
import org.jboss.tools.hibernate.runtime.common.AbstractPropertyFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class PropertyProxy extends AbstractPropertyFacade {
	
	private Property target = null;
	private IValue value = null;
	private IType type = null;
	private IPersistentClass persistentClass = null;
	

	public PropertyProxy(
			IFacadeFactory facadeFactory,
			Property property) {
		super(facadeFactory, property);
		target = property;
	}
	
	public Property getTarget() {
		return target;
	}

	@Override
	public IValue getValue() {
		if (value == null && target.getValue() != null) {
			value = new ValueProxy(getFacadeFactory(), target.getValue());
		}
		return value;
	}

	@Override
	public void setName(String name) {
		target.setName(name);
	}

	@Override
	public void setPersistentClass(IPersistentClass persistentClass) {
		assert persistentClass instanceof PersistentClassProxy;
		target.setPersistentClass(((PersistentClassProxy)persistentClass).getTarget());
	}

	@Override
	public IPersistentClass getPersistentClass() {
		if (persistentClass == null && target.getPersistentClass() != null) {
			persistentClass = new PersistentClassProxy(getFacadeFactory(), target.getPersistentClass());
		}
		return persistentClass;
	}

	@Override
	public boolean isComposite() {
		return target.isComposite();
	}

	@Override
	public String getPropertyAccessorName() {
		return target.getPropertyAccessorName();
	}

	@Override
	public String getName() {
		return target.getName();
	}

	@Override
	public boolean classIsPropertyClass() {
		return target.getClass() == Property.class;
	}

	@Override
	public String getNodeName() {
		return target.getNodeName();
	}

	@Override
	public IType getType() {
		if (type == null && target.getType() != null) {
			type = new TypeProxy(target.getType());
		}
		return type;
	}

	@Override
	public void setValue(IValue value) {
		assert value instanceof ValueProxy;
		target.setValue(((ValueProxy)value).getTarget());
		this.value = value;
	}

	@Override
	public void setPropertyAccessorName(String string) {
		target.setPropertyAccessorName(string);
	}

	@Override
	public void setCascade(String string) {
		target.setCascade(string);
	}

	@Override
	public boolean isBackRef() {
		return target.isBackRef();
	}

	@Override
	public boolean isSelectable() {
		return target.isSelectable();
	}

	@Override
	public boolean isInsertable() {
		return target.isInsertable();
	}

	@Override
	public boolean isUpdateable() {
		return target.isUpdateable();
	}

	@Override
	public String getCascade() {
		return target.getCascade();
	}

	@Override
	public boolean isLazy() {
		return target.isLazy();
	}

	@Override
	public boolean isOptional() {
		return target.isOptional();
	}

	@Override
	public boolean isNaturalIdentifier() {
		return target.isNaturalIdentifier();
	}

	@Override
	public boolean isOptimisticLocked() {
		return target.isOptimisticLocked();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PropertyProxy)) return false;
		return getTarget().equals(((PropertyProxy)o).getTarget());
	}

}
