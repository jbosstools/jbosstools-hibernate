package org.jboss.tools.hibernate.proxy;

import java.io.File;

import org.hibernate.cfg.reveng.OverrideRepository;
import org.jboss.tools.hibernate.runtime.common.AbstractOverrideRepositoryFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;

public class OverrideRepositoryProxy extends AbstractOverrideRepositoryFacade {
	
	private OverrideRepository target = null;

	public OverrideRepositoryProxy(
			IFacadeFactory facadeFactory, 
			OverrideRepository overrideRepository) {
		super(facadeFactory, overrideRepository);
		target = overrideRepository;
	}
	
	public OverrideRepository getTarget() {
		return target;
	}

	@Override
	public void addFile(File file) {
		target.addFile(file);
	}

	@Override
	public IReverseEngineeringStrategy getReverseEngineeringStrategy(
			IReverseEngineeringStrategy res) {
		assert res instanceof ReverseEngineeringStrategyProxy;
		
		return new ReverseEngineeringStrategyProxy(
				target.getReverseEngineeringStrategy(
						((ReverseEngineeringStrategyProxy)res).getTarget()));
	}

	@Override
	public void addTableFilter(ITableFilter tf) {
		assert tf instanceof TableFilterProxy;
		target.addTableFilter(((TableFilterProxy)tf).getTarget());
	}

}
