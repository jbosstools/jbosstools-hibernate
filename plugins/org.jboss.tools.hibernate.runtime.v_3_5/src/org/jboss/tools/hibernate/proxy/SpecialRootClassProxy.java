package org.jboss.tools.hibernate.proxy;

import java.util.Iterator;

import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class SpecialRootClassProxy extends PersistentClassProxy {

	private IProperty property;
	private IProperty parentProperty;

	public SpecialRootClassProxy(
			IFacadeFactory facadeFactory, 
			IProperty property) {
		super(new RootClass());
		this.property = property;
		generate();
	}

	private void generate() {
		if (property == null) {
			return;
		}
		IValue propVal = property.getValue();
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
				parentProperty = new PropertyProxy(new Property());
				parentProperty.setName(component.getParentProperty());
				parentProperty.setPersistentClass(ownerClass);
			}
			Iterator<IProperty> iterator = component.getPropertyIterator();
			while (iterator.hasNext()) {
				IProperty property = iterator.next();
				if (property != null) {
					addProperty(property);
				}
			}
		}
	}

	public IProperty getParentProperty() {
		return parentProperty;
	}

	public IProperty getProperty() {
		return this.property;
	}

	public boolean isInstanceOfSpecialRootClass() {
		return true;
	}
	
}
