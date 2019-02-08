package org.jboss.tools.hibernate.runtime.v_5_3.internal.util;

import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.v_5_3.internal.ConfigurationFacadeImpl;

public class ConfigurationMetadataDescriptor implements MetadataDescriptor {
	
	private IConfiguration configuration;
	
	public ConfigurationMetadataDescriptor(IConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public Metadata createMetadata() {
		return ((ConfigurationFacadeImpl)configuration).getMetadata();
	}

	@Override
	public Properties getProperties() {
		return configuration.getProperties();
	}
	
	public IConfiguration getConfiguration() {
		return this.configuration;
	}

}
