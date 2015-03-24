package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingGlobalSettings;

public abstract class AbstractHibernateMappingGlobalSettingsFacade 
extends AbstractFacade 
implements IHibernateMappingGlobalSettings {

	public AbstractHibernateMappingGlobalSettingsFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
