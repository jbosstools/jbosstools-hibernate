package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;

public class ConfigurationMetadataDescriptor implements MetadataDescriptor {

	IConfiguration configurationFacade;
	
	public ConfigurationMetadataDescriptor(IConfiguration configurationFacade) {
		this.configurationFacade = configurationFacade;
	}

	@Override
	public Metadata createMetadata() {
		return MetadataHelper.getMetadata((Configuration)((IFacade)configurationFacade).getTarget());
	}

	@Override
	public Properties getProperties() {
		return configurationFacade.getProperties();
	}

}
