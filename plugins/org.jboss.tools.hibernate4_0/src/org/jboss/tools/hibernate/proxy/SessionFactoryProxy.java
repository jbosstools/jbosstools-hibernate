package org.jboss.tools.hibernate.proxy;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.jboss.tools.hibernate.spi.IClassMetadata;
import org.jboss.tools.hibernate.spi.ISession;
import org.jboss.tools.hibernate.spi.ISessionFactory;

public class SessionFactoryProxy implements ISessionFactory {
	
	private SessionFactory target;
	private Map<String, IClassMetadata> allClassMetadata = null;

	public SessionFactoryProxy(SessionFactory sessionFactory) {
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

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, CollectionMetadata> getAllCollectionMetadata() {
		return target.getAllCollectionMetadata();
	}

	@Override
	public ISession openSession() {
		return new SessionProxy(this, target.openSession());
	}
	
	SessionFactory getTarget() {
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
	public CollectionMetadata getCollectionMetadata(String string) {
		return target.getCollectionMetadata(string);
	}

}
