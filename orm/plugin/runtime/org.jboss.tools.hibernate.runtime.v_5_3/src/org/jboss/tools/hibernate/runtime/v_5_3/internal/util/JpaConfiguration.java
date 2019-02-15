package org.jboss.tools.hibernate.runtime.v_5_3.internal.util;

import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;

public class JpaConfiguration extends Configuration {
	
	private Metadata metadata = null;
	
	private String persistenceUnit;
	
	public JpaConfiguration(
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
	
	public String getPersistenceUnit() {
		return persistenceUnit;
	}
	
}
