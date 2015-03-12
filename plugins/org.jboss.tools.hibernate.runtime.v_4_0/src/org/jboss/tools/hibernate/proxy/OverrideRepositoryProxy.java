package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.OverrideRepository;
import org.jboss.tools.hibernate.runtime.common.AbstractOverrideRepositoryFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;

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
		assert res instanceof ReverseEngineeringStrategyProxy;
		
		return new ReverseEngineeringStrategyProxy(
				getTarget().getReverseEngineeringStrategy(
						((ReverseEngineeringStrategyProxy)res).getTarget()));
	}

	@Override
	public void addTableFilter(ITableFilter tf) {
		assert tf instanceof TableFilterProxy;
		getTarget().addTableFilter(((TableFilterProxy)tf).getTarget());
	}

}
