package org.jboss.tools.hibernate.runtime.spi;

public interface ISession {

	String getEntityName(Object o);
	ISessionFactory getSessionFactory();
	IQuery createQuery(String queryString);
	boolean isOpen();
	void close();
	boolean contains(Object object);
	ICriteria createCriteria(Class<?> persistentClass);

}
