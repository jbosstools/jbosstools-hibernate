package org.jboss.tools.hibernate.spi;

public interface IClassMetadata {

	String getEntityName();
	String getIdentifierPropertyName();
	String[] getPropertyNames();
	IType[] getPropertyTypes();
	Class<?> getMappedClass();
	IType getIdentifierType();
	Object getPropertyValue(Object object, String name);
	boolean hasIdentifierProperty();
	Object getIdentifier(Object object);
	Object getIdentifier(Object object, ISessionImplementor implementor);

}
