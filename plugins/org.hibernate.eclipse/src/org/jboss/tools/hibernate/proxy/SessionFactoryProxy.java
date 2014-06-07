package org.jboss.tools.hibernate.proxy;

import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.jboss.tools.hibernate.spi.ISessionFactory;

public class SessionFactoryProxy implements ISessionFactory {
	
	private SessionFactory target;

	public SessionFactoryProxy(SessionFactory sessionFactory) {
		target = sessionFactory;
	}

	@Override
	public void close() {
		target.close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ClassMetadata> getAllClassMetadata() {
		return target.getAllClassMetadata();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, CollectionMetadata> getAllCollectionMetadata() {
		return target.getAllCollectionMetadata();
	}

	@Override
	public Session openSession() {
		return target.openSession();
	}
	
	SessionFactory getTarget() {
		return target;
	}

}
