package org.jboss.tools.hibernate.spi;

public interface IProperty {

	IValue getValue();
	void setName(String name);
	void setPersistentClass(IPersistentClass persistentClass);
	IPersistentClass getPersistentClass();
	boolean isComposite();
	String getPropertyAccessorName();
	String getName();
	boolean classIsPropertyClass();
	String getNodeName();
	IType getType();
	void setValue(IValue value);
	void setPropertyAccessorName(String string);
	void setCascade(String string);
	boolean isBackRef();
	boolean isSelectable();
	boolean isInsertable();
	boolean isUpdateable();
	String getCascade();
	boolean isLazy();
	boolean isOptional();
	boolean isNaturalIdentifier();
	boolean isOptimisticLocked();

}
