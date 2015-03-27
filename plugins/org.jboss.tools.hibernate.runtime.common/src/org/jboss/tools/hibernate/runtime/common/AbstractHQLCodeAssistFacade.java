package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;

public abstract class AbstractHQLCodeAssistFacade 
extends AbstractFacade 
implements IHQLCodeAssist {

	public AbstractHQLCodeAssistFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
