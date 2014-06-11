package org.jboss.tools.hibernate.spi;

public interface IType {

	String toString(Object value);
	String getName();
	Object fromStringValue(String value);
	boolean isEntityType();
	boolean isOneToOne();
	boolean isAnyType();
	boolean isComponentType();
	boolean isCollectionType();
	Class<?> getReturnedClass();
	String getAssociatedEntityName();

}
