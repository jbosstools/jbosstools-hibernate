package org.jboss.tools.hibernate.runtime.v_5_5.internal.util;

import java.util.Properties;

public class JdbcMetadataConfiguration {

	private Properties properties = new Properties();

	public Object getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Object getProperty(String key) {
		return properties.get(key);
	}

	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	public void addProperties(Properties properties) {
		this.properties.putAll(properties);
	}
		
}