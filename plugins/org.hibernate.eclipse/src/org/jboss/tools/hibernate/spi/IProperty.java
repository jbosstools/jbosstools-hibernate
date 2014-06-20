package org.jboss.tools.hibernate.spi;

import org.hibernate.mapping.PersistentClass;

public interface IProperty {

	String getName();
	PersistentClass getPersistentClass();
	IValue getValue();
	boolean isComposite();
	String getNodeName();
	String getPropertyAccessorName();
	void setName(String name);
	void setValue(IValue value);

}
