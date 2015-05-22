package org.jboss.tools.hibernate.runtime.common;

import java.util.Map;

import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;

public abstract class AbstractSessionFactoryFacade 
extends AbstractFacade 
implements ISessionFactory {

	protected Map<String, IClassMetadata> allClassMetadata = null;

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
