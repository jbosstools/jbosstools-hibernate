package org.jboss.tools.hibernate.proxy;

import java.util.Iterator;

import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.spi.IPersistentClass;
import org.jboss.tools.hibernate.spi.IValue;

public class SpecialRootClassProxy extends PersistentClassProxy {

	private Property property;
	private Property parentProperty;

	public SpecialRootClassProxy(Property property) {
		super(new RootClass());
		this.property = property;
		generate();
	}

	private void generate() {
		if (property == null) {
			return;
		}
		IValue propVal = property.getValue() != null ? new ValueProxy(property.getValue()) : null;
		IValue component = null;
		if (propVal != null && propVal.isCollection()) {
			IValue collection = propVal;
			component = collection.getElement();
		} else if (propVal.isComponent()) {
			component = propVal;
		}
		if (component != null) {
			setClassName(component.getComponentClassName());
			setEntityName(component.getComponentClassName());
			IPersistentClass ownerClass = component.getOwner();
			if (component.getParentProperty() != null) {
				parentProperty = new Property();
				parentProperty.setName(component.getParentProperty());
				parentProperty.setPersistentClass(((PersistentClassProxy)ownerClass).getTarget());
			}
			Iterator<Property> iterator = component.getPropertyIterator();
			while (iterator.hasNext()) {
				Property property = iterator.next();
				if (property != null) {
					addProperty(property);
				}
			}
		}
	}

	public Property getParentProperty() {
		return parentProperty;
	}

	public Property getProperty() {
		return this.property;
	}

	public boolean isInstanceOfSpecialRootClass() {
		return true;
	}
	
}
