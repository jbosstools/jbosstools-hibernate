package org.jboss.tools.hibernate.runtime.v_5_3.internal.util;

import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;

public class JdbcMetadataConfiguration {
	
	private boolean preferBasicCompositeIds = true;
	private ReverseEngineeringStrategy res = null;
	private Properties properties = new Properties();
	private Metadata metadata = null;

	public boolean preferBasicCompositeIds() {
		return preferBasicCompositeIds;
	}
	
	public void setPreferBasicCompositeIds(boolean b) {
		preferBasicCompositeIds = b;
	}

	public ReverseEngineeringStrategy getReverseEngineeringStrategy() {
		return res;
	}
	
	public void setReverseEngineeringStrategy(ReverseEngineeringStrategy res) {
		this.res = res;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void readFromJDBC() {
		metadata = MetadataDescriptorFactory
				.createJdbcDescriptor(res, properties, preferBasicCompositeIds)
				.createMetadata();
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public void addProperties(Properties properties) {
		this.properties.putAll(properties);
	}
	
	public Object getProperty(String key) {
		return this.properties.get(key);
	}
	
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}


}
