package org.jboss.tools.hibernate.runtime.common;

import java.util.Iterator;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public abstract class AbstractSpecialRootClassFacade 
extends AbstractPersistentClassFacade {

	protected IProperty property;
	protected IProperty parentProperty;

	public AbstractSpecialRootClassFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}
	
	protected void generate() {
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
				Object newProperty = Util.getInstance(
						getPropertyClassName(), 
						getFacadeFactoryClassLoader());
				parentProperty = getFacadeFactory().createProperty(newProperty);
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

	protected String getPropertyClassName() {
		return "org.hibernate.mapping.Property";
	}

}
