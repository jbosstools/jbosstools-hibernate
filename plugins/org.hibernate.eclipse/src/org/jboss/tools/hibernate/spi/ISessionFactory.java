package org.jboss.tools.hibernate.spi;

import java.util.Map;

import org.hibernate.metadata.CollectionMetadata;

public interface ISessionFactory {

	void close();
	Map<String, IClassMetadata> getAllClassMetadata();
	Map<String, CollectionMetadata> getAllCollectionMetadata();
	ISession openSession();
	IClassMetadata getClassMetadata(Class<?> clazz);
	IClassMetadata getClassMetadata(String entityName);
	CollectionMetadata getCollectionMetadata(String string);

}
