package org.jboss.tools.hibernate.runtime.spi;

import java.util.Map;

public interface ISessionFactory {

	void close();
	Map<String, IClassMetadata> getAllClassMetadata();
	Map<String, ICollectionMetadata> getAllCollectionMetadata();
	ISession openSession();
	IClassMetadata getClassMetadata(Class<?> clazz);
	IClassMetadata getClassMetadata(String entityName);
	ICollectionMetadata getCollectionMetadata(String string);

}
