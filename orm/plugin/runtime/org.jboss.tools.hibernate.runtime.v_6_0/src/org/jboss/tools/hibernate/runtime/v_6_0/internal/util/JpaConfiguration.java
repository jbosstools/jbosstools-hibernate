package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;

public class JpaConfiguration extends Configuration {

	Metadata metadata = null;
	SessionFactory sessionFactory;
	
	String persistenceUnit;
	
	public JpaConfiguration(
			String persistenceUnit, 
			Map<Object, Object> properties) {
		this.persistenceUnit = persistenceUnit;
		if (properties != null) {
			getProperties().putAll(properties);
		}
	}
	
	public String getPersistenceUnit() {
		return persistenceUnit;
	}
	
	void initialize() {
		EntityManagerFactoryBuilderImpl entityManagerFactoryBuilder = 
				HibernateToolsPersistenceProvider
					.createEntityManagerFactoryBuilder(
							persistenceUnit, 
							getProperties());
		EntityManagerFactory entityManagerFactory = 
				entityManagerFactoryBuilder.build();
		sessionFactory = (SessionFactory)entityManagerFactory;
		metadata = entityManagerFactoryBuilder.getMetadata();
		getProperties().putAll(entityManagerFactory.getProperties());
	}
}
