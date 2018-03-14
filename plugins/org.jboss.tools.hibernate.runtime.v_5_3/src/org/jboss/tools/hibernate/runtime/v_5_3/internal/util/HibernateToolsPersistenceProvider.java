package org.jboss.tools.hibernate.runtime.v_5_3.internal.util;

import java.util.Map;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;


class HibernateToolsPersistenceProvider extends HibernatePersistenceProvider {

	static EntityManagerFactoryBuilderImpl createEntityManagerFactoryBuilder(
			final String persistenceUnit, 
			final Map<Object, Object> properties) {
		return new HibernateToolsPersistenceProvider()
				.getEntityManagerFactoryBuilder(
						persistenceUnit, 
						properties);
	}	

	private EntityManagerFactoryBuilderImpl getEntityManagerFactoryBuilder(
			String persistenceUnit, 
			Map<Object, Object> properties) {
		return (EntityManagerFactoryBuilderImpl)getEntityManagerFactoryBuilderOrNull(
				persistenceUnit, 
				properties);
	}
	
}
