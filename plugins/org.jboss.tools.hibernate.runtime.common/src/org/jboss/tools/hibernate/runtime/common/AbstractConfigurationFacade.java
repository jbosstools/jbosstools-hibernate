package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public abstract class AbstractConfigurationFacade 
extends AbstractFacade 
implements IConfiguration {

	public AbstractConfigurationFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}
	

}
