package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;

public abstract class AbstractReverseEngineeringSettingsFacade 
extends AbstractFacade 
implements IReverseEngineeringSettings {

	public AbstractReverseEngineeringSettingsFacade(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public IReverseEngineeringSettings setDefaultPackageName(String name) {
		Util.invokeMethod(
				getTarget(), 
				"setDefaultPackageName", 
				new Class[] { String.class }, 
				new Object[] { name } );
		return this;
	}

}
