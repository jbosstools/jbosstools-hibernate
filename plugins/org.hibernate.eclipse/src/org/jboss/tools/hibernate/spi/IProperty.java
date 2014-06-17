package org.jboss.tools.hibernate.spi;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Value;

public interface IProperty {

	String getName();
	PersistentClass getPersistentClass();
	Value getValue();
	boolean isComposite();
	String getNodeName();
	String getPropertyAccessorName();
	void setName(String name);
	void setValue(Value value);

}
