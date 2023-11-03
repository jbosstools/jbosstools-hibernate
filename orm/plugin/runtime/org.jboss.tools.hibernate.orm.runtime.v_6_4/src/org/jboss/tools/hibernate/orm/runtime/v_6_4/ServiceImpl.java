package org.jboss.tools.hibernate.orm.runtime.v_6_4;

import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;

public class ServiceImpl {

	public IConfiguration newDefaultConfiguration() {
		return (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createNativeConfigurationWrapper());
	}

}
