package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISession;

public abstract class AbstractSessionFacade 
extends AbstractFacade 
implements ISession {

	public AbstractSessionFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String getEntityName(Object o) {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getEntityName", 
				new Class[] { Object.class }, 
				new Object[] { o });
	}

}
