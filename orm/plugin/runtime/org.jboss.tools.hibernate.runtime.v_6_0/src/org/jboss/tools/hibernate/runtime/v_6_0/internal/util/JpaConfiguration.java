package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;

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
	
}
