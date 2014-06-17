package org.jboss.tools.hibernate.proxy;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.spi.IProperty;

public class PropertyProxy implements IProperty {
	
	private Property target = null;
	
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
	public Value getValue() {
		return target.getValue();
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
	public void setValue(Value value) {
		target.setValue(value);
	}
	
	Property getTarget() {
		return target;
	}

}
