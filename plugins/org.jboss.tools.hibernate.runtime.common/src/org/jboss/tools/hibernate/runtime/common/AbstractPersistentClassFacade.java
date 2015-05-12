package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;

public abstract class AbstractPersistentClassFacade 
extends AbstractFacade 
implements IPersistentClass {

	public AbstractPersistentClassFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String getClassName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getClassName", 
				new Class[] {}, 
				new Object[] {});
	}

}
