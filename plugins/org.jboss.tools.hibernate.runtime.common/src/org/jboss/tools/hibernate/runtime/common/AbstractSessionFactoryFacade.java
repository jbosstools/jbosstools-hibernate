package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;

public abstract class AbstractSessionFactoryFacade 
extends AbstractFacade 
implements ISessionFactory {

	public AbstractSessionFactoryFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public void close() {
		Util.invokeMethod(getTarget(), "close", new Class[] {}, new Object[] {});
	}

}
