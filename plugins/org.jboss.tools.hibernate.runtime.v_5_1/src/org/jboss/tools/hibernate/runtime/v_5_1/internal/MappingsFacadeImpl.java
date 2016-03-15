package org.jboss.tools.hibernate.runtime.v_5_1.internal;

import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IMappings;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;

public class MappingsFacadeImpl implements IFacade, IMappings {
	
	ConfigurationFacadeImpl configuration = null;
	
	public MappingsFacadeImpl(IConfiguration configuration) {
		this.configuration = (ConfigurationFacadeImpl)configuration;
	}

	@Override
	public void addClass(IPersistentClass persistentClass) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object getTarget() {
		// From Hibernate 5.0 on the Mappings class has been removed
		return null;
	}

}
