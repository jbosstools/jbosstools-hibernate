package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.xml.sax.EntityResolver;

public class ConfigurationFacadeImpl extends AbstractConfigurationFacade {
	
	EntityResolver entityResolver = null;

	public ConfigurationFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	public void setEntityResolver(EntityResolver entityResolver) {
		// This method is not supported anymore from Hibernate 5+
		// Only caching the EntityResolver for bookkeeping purposes
		this.entityResolver = entityResolver;
	}
	
	public EntityResolver getEntityResolver() {
		// This method is not supported anymore from Hibernate 5+
		// Returning the cached EntityResolver for bookkeeping purposes
		return this.entityResolver;
	}

}
