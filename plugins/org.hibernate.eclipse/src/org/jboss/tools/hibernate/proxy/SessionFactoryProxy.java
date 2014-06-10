package org.jboss.tools.hibernate.proxy;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.jboss.tools.hibernate.spi.ISession;
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
	public ISession openSession() {
		return new SessionProxy(this, target.openSession());
	}
	
	SessionFactory getTarget() {
		return target;
	}

	@Override
	public ClassMetadata getClassMetadata(Class<?> clazz) {
		return target.getClassMetadata(clazz);
	}

	@Override
	public ClassMetadata getClassMetadata(String entityName) {
		return target.getClassMetadata(entityName);
	}

	@Override
	public CollectionMetadata getCollectionMetadata(String string) {
		return target.getCollectionMetadata(string);
	}

}
