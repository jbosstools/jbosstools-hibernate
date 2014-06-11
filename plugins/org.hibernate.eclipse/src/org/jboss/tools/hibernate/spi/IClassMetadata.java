package org.jboss.tools.hibernate.spi;

import org.hibernate.type.Type;

public interface IClassMetadata {

	String getEntityName();
	String getIdentifierPropertyName();
	String[] getPropertyNames();
	Type[] getPropertyTypes();
	Class<?> getMappedClass();
	Type getIdentifierType();
	Object getPropertyValue(Object object, String name);
	boolean hasIdentifierProperty();
	Object getIdentifier(Object object);
	Object getIdentifier(Object object, ISessionImplementor implementor);

}
