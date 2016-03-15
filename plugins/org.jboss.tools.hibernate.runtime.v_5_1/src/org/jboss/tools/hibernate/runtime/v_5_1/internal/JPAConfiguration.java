package org.jboss.tools.hibernate.runtime.v_5_1.internal;

import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;

public class JPAConfiguration extends Configuration {
	
	private Metadata metadata = null;
	
	private String persistenceUnit;
	
	public JPAConfiguration(
			String persistenceUnit, 
			Map<Object, Object> properties) {
		this.persistenceUnit = persistenceUnit;
		if (properties != null) {
			getProperties().putAll(properties);
		}
	}
	
	public Metadata getMetadata() {
		if (metadata == null) {
			EntityManagerFactoryBuilderImpl entityManagerFactoryBuilder = 
					HibernateToolsPersistenceProvider
						.createEntityManagerFactoryBuilder(
								persistenceUnit, 
								getProperties());
			EntityManagerFactory entityManagerFactory = 
					entityManagerFactoryBuilder.build();
			metadata = entityManagerFactoryBuilder.getMetadata();
			getProperties().putAll(entityManagerFactory.getProperties());
		}
		return metadata;
	}
	
	@Override
	public SessionFactory buildSessionFactory() {
		return getMetadata().buildSessionFactory();
	}
	
	@Override
	public Configuration setProperties(Properties properties) {
		super.setProperties(properties);
		metadata = null;
		return this;
	}
	
	@Override
	public Configuration addProperties(Properties properties) {
		super.addProperties(properties);
		metadata = null;
		return this;
	}
	
}
