package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.mapping.PersistentClass;
import org.jboss.tools.hibernate.runtime.common.AbstractPersistentClassFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class PersistentClassFacadeImpl extends AbstractPersistentClassFacade {
	
	public PersistentClassFacadeImpl(
			IFacadeFactory facadeFactory,
			PersistentClass persistentClass) {
		super(facadeFactory, persistentClass);
	}

	public PersistentClass getTarget() {
		return (PersistentClass)super.getTarget();
	}

	@Override
	public int getOptimisticLockMode() {
		return getTarget().getOptimisticLockMode();
	}

}
