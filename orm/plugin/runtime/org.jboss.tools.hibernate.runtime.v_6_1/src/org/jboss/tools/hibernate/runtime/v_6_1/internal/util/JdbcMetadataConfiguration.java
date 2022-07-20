package org.jboss.tools.hibernate.runtime.v_6_1.internal.util;

import java.util.Properties;

import org.hibernate.tool.api.reveng.RevengStrategy;

public class JdbcMetadataConfiguration {

	Properties properties = new Properties();
	RevengStrategy revengStrategy;

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

	public void addProperties(Properties properties) {
		this.properties.putAll(properties);
	}

	public Object getReverseEngineeringStrategy() {
		return revengStrategy;
	}

	public void setReverseEngineeringStrategy(RevengStrategy strategy) {
		this.revengStrategy = strategy;
	}

}
