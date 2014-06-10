package org.jboss.tools.hibernate.spi;

import org.hibernate.Query;

public interface ISession {

	String getEntityName(Object o);
	ISessionFactory getSessionFactory();
	Query createQuery(String queryString);
	boolean isOpen();
	void close();
	boolean contains(Object object);

}
