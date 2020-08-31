package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import java.util.Properties;

public class JdbcMetadataConfiguration {
	
	Properties properties = new Properties();

	public Properties getProperties() {
		return properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public Object getProperty(String key) {
		return this.properties.get(key);
	}
	
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

}
