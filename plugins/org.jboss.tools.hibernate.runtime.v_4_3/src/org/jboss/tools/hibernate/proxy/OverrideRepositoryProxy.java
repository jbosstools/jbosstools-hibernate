package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.OverrideRepository;
import org.jboss.tools.hibernate.runtime.common.AbstractOverrideRepositoryFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class OverrideRepositoryProxy extends AbstractOverrideRepositoryFacade {
	
	public OverrideRepositoryProxy(
			IFacadeFactory facadeFactory, 
			OverrideRepository overrideRepository) {
		super(facadeFactory, overrideRepository);
	}
	
	public OverrideRepository getTarget() {
		return (OverrideRepository)super.getTarget();
	}

}
