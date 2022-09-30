package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.jboss.tools.hibernate.runtime.common.AbstractOverrideRepositoryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;

public class OverrideRepositoryFacadeImpl extends AbstractOverrideRepositoryFacade {

	public OverrideRepositoryFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	@Override
	public IReverseEngineeringStrategy getReverseEngineeringStrategy(
			IReverseEngineeringStrategy res) {
		assert res instanceof IFacade;
		RevengStrategy resTarget = (RevengStrategy)((IFacade)res).getTarget();
		return getFacadeFactory().createReverseEngineeringStrategy(
				((OverrideRepository)getTarget()).getReverseEngineeringStrategy(resTarget));
	}
	
	@Override
	protected String getTableFilterClassName() {
		return "org.hibernate.tool.internal.reveng.strategy.TableFilter";
	}
	
}
