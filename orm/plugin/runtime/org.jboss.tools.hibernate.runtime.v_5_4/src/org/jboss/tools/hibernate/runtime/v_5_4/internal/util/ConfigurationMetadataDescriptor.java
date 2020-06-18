package org.jboss.tools.hibernate.runtime.v_5_4.internal.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.internal.MetadataImpl;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.v_5_4.internal.ConfigurationFacadeImpl;

public class ConfigurationMetadataDescriptor implements MetadataDescriptor {
	
	private IConfiguration configuration;
	
	public ConfigurationMetadataDescriptor(IConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override 
	public Metadata createMetadata() {
		Metadata result = null;
		if (this.configuration instanceof ConfigurationFacadeImpl) {
			result = ((ConfigurationFacadeImpl)configuration).getMetadata();
			if (result != null) {
				result = patch(result);
			}
		}
		return result;
	}

	private Metadata patch(Metadata metadata) {
		try {
			if (metadata instanceof MetadataImpl ) {
				MetadataImpl metadataImpl = (MetadataImpl)metadata;
				Field entityBindingMapField = metadataImpl.getClass().getDeclaredField("entityBindingMap");
				if (entityBindingMapField != null) {
					entityBindingMapField.setAccessible(true);
					Object object = entityBindingMapField.get(metadataImpl);
					if (object instanceof HashMap<?, ?>) {
						@SuppressWarnings("unchecked")
						HashMap<String, PersistentClass> map = (HashMap<String, PersistentClass>)object;
						for (IPersistentClass ipc : ((ConfigurationFacadeImpl)this.configuration).getAddedClasses()) {
							PersistentClass pc = (PersistentClass)((IFacade)ipc).getTarget();
							map.put(pc.getEntityName(), pc);
						}
					}
				}
			}
			return metadata;
		}
		catch (Throwable t) {
			throw new RuntimeException("Problem while creating metadata", t);
		}
	} 
	
	@Override
	public Properties getProperties() {
		return configuration.getProperties();
	}
	
	public IConfiguration getConfiguration() {
		return this.configuration;
	}

}
