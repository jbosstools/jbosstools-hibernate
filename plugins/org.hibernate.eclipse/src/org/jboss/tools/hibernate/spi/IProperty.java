package org.jboss.tools.hibernate.spi;


public interface IProperty {

	String getName();
	IPersistentClass getPersistentClass();
	IValue getValue();
	boolean isComposite();
	String getNodeName();
	String getPropertyAccessorName();
	void setName(String name);
	void setValue(IValue value);

}
