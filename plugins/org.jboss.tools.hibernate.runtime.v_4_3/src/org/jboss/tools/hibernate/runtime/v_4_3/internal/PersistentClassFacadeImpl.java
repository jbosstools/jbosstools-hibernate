package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.mapping.PersistentClass;
import org.jboss.tools.hibernate.runtime.common.AbstractPersistentClassFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class PersistentClassFacadeImpl extends AbstractPersistentClassFacade {
	
	public PersistentClassFacadeImpl(
			IFacadeFactory facadeFactory,
			PersistentClass persistentClass) {
		super(facadeFactory, persistentClass);
	}

}
