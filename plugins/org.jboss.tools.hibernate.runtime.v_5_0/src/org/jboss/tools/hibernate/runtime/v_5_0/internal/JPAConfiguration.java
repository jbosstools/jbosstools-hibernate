package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.jboss.tools.hibernate.runtime.spi.HibernateException;

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
			if(entityManagerFactoryBuilder == null){
				return metadata;
			}
			EntityManagerFactory entityManagerFactory = null;
			try{
				entityManagerFactory = entityManagerFactoryBuilder.build();
			} catch (Throwable t){
				throw new HibernateException(t);
			}
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
