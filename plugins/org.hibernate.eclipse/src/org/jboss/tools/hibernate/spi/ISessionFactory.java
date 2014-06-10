package org.jboss.tools.hibernate.spi;

import java.util.Map;

import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;

public interface ISessionFactory {

	void close();
	Map<String, ClassMetadata> getAllClassMetadata();
	Map<String, CollectionMetadata> getAllCollectionMetadata();
	ISession openSession();
	ClassMetadata getClassMetadata(Class<?> clazz);
	ClassMetadata getClassMetadata(String entityName);
	CollectionMetadata getCollectionMetadata(String string);

}
