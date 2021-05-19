package org.jboss.tools.hibernate.runtime.v_5_5.internal.util;

import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;

public class JdbcMetadataConfiguration {

	private Properties properties = new Properties();
	private ReverseEngineeringStrategy revengStrategy = null;
	private boolean preferBasicCompositeIds = true;
	private Metadata metadata = null;

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

	public Object getReverseEngineeringStrategy() {
		return revengStrategy;
	}

	public void setReverseEngineeringStrategy(ReverseEngineeringStrategy strategy) {
		this.revengStrategy = strategy;
	}

	public boolean preferBasicCompositeIds() {
		return preferBasicCompositeIds;
	}

	public void setPreferBasicCompositeIds(boolean b) {
		preferBasicCompositeIds = b;
	}

	public Object getMetadata() {
		return metadata;
	}

	public void readFromJDBC() {
		metadata = MetadataDescriptorFactory
				.createJdbcDescriptor(revengStrategy, properties, preferBasicCompositeIds)
				.createMetadata();
	}
	
}
