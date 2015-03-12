package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;

public abstract class AbstractReverseEngineeringSettingsFacade 
extends AbstractFacade 
implements IReverseEngineeringSettings {

	public AbstractReverseEngineeringSettingsFacade(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

}
