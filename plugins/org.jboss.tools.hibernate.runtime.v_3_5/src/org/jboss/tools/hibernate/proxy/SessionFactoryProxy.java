package org.jboss.tools.hibernate.proxy;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;

public class SessionFactoryProxy implements ISessionFactory {
	
	private SessionFactory target;
	private Map<String, IClassMetadata> allClassMetadata = null;
	private Map<String, ICollectionMetadata> allCollectionMetadata = null;

	public SessionFactoryProxy(IFacadeFactory facadeFactory, SessionFactory sessionFactory) {
		target = sessionFactory;
	}

	@Override
	public void close() {
		target.close();
	}

	@Override
	public Map<String, IClassMetadata> getAllClassMetadata() {
		if (allClassMetadata == null) {
			initializeAllClassMetadata();
		}
		return allClassMetadata;
	}
	
	@SuppressWarnings("unchecked")
	private void initializeAllClassMetadata() {
		Map<String, ClassMetadata> origin = target.getAllClassMetadata();
		allClassMetadata = new HashMap<String, IClassMetadata>(origin.size());
		for (Map.Entry<String, ClassMetadata> entry : origin.entrySet()) {
			allClassMetadata.put(
					entry.getKey(), 
					new ClassMetadataProxy(entry.getValue()));
		}
	}

	@Override
	public Map<String, ICollectionMetadata> getAllCollectionMetadata() {
		if (allCollectionMetadata == null) {
			initializeAllCollectionMetadata();
		}
		return allCollectionMetadata;
	}
	
	@SuppressWarnings("unchecked")
	private void initializeAllCollectionMetadata() {
		Map<String, CollectionMetadata> origin = target.getAllCollectionMetadata();
		allCollectionMetadata = new HashMap<String, ICollectionMetadata>(origin.size());
		for (Map.Entry<String, CollectionMetadata> entry : origin.entrySet()) {
			String key = entry.getKey();
			CollectionMetadata value = entry.getValue();
			allCollectionMetadata.put(key, new CollectionMetadataProxy(value));
		}
	}

	@Override
	public ISession openSession() {
		return new SessionProxy(this, target.openSession());
	}
	
	public SessionFactory getTarget() {
		return target;
	}

	@Override
	public IClassMetadata getClassMetadata(Class<?> clazz) {
		if (allClassMetadata == null) {
			initializeAllClassMetadata();
		}
		return allClassMetadata.get(clazz.getName());
	}

	@Override
	public IClassMetadata getClassMetadata(String entityName) {
		if (allClassMetadata == null) {
			initializeAllClassMetadata();
		}
		return allClassMetadata.get(entityName);
	}

	@Override
	public ICollectionMetadata getCollectionMetadata(String string) {
		if (allCollectionMetadata == null) {
			initializeAllCollectionMetadata();
		}
		return allCollectionMetadata.get(string);
	}

}
