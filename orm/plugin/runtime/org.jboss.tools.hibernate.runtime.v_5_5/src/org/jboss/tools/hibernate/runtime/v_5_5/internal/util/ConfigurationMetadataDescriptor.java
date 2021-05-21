package org.jboss.tools.hibernate.runtime.v_5_5.internal.util;

import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.jboss.tools.hibernate.runtime.v_5_5.internal.ConfigurationFacadeImpl;

public class ConfigurationMetadataDescriptor implements MetadataDescriptor {
	
	private ConfigurationFacadeImpl configurationFacade;

	public ConfigurationMetadataDescriptor(ConfigurationFacadeImpl configurationFacade) {
		this.configurationFacade = configurationFacade;
	}

	@Override
	public Metadata createMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getProperties() {
		return configurationFacade.getProperties();
	}

}
