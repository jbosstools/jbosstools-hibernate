package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.common.AbstractOverrideRepositoryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;

public class OverrideRepositoryProxy extends AbstractOverrideRepositoryFacade {
	
	public OverrideRepositoryProxy(
			IFacadeFactory facadeFactory, 
			OverrideRepository overrideRepository) {
		super(facadeFactory, overrideRepository);
	}
	
	public OverrideRepository getTarget() {
		return (OverrideRepository)super.getTarget();
	}

	@Override
	public IReverseEngineeringStrategy getReverseEngineeringStrategy(
			IReverseEngineeringStrategy res) {
		assert res instanceof IFacade;
		return getFacadeFactory().createReverseEngineeringStrategy(
				getTarget().getReverseEngineeringStrategy(
						(ReverseEngineeringStrategy)((IFacade)res).getTarget()));
	}

}
