package org.jboss.tools.hibernate.runtime.spi;


public interface IClassMetadata {

	String getEntityName();
	String getIdentifierPropertyName();
	String[] getPropertyNames();
	IType[] getPropertyTypes();
	Class<?> getMappedClass();
	IType getIdentifierType();
	Object getPropertyValue(Object object, String name) throws HibernateException;
	boolean hasIdentifierProperty();
	Object getIdentifier(Object object, ISession implementor);
	boolean isInstanceOfAbstractEntityPersister();
	IEntityMetamodel getEntityMetamodel();

}
