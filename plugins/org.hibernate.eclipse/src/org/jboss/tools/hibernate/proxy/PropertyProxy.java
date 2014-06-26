package org.jboss.tools.hibernate.proxy;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.jboss.tools.hibernate.spi.IProperty;
import org.jboss.tools.hibernate.spi.IValue;

public class PropertyProxy implements IProperty {
	
	private Property target = null;
	private IValue value = null;
	
	public PropertyProxy(Property property) {
		target = property;
	}

	@Override
	public String getName() {
		return target.getName();
	}

	@Override
	public PersistentClass getPersistentClass() {
		return target.getPersistentClass();
	}

	@Override
	public IValue getValue() {
		if (target.getValue() != null && value == null) {
			value = new ValueProxy(target.getValue());
		}
		return value;
	}

	@Override
	public boolean isComposite() {
		return target.isComposite();
	}

	@Override
	public String getNodeName() {
		return target.getNodeName();
	}

	@Override
	public String getPropertyAccessorName() {
		return target.getPropertyAccessorName();
	}

	@Override
	public void setName(String name) {
		target.setName(name);
	}

	@Override
	public void setValue(IValue value) {
		assert value instanceof ValueProxy;
		target.setValue(((ValueProxy)value).getTarget());
	}
	
	public Property getTarget() {
		return target;
	}

}
