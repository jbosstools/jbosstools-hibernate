package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractReverseEngineeringStrategyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

public class ReverseEngineeringStrategyFacadeImpl extends AbstractReverseEngineeringStrategyFacade {

	public ReverseEngineeringStrategyFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	protected String getReverseEngineeringSettingsClassName() {
		return "org.hibernate.tool.api.reveng.RevengSettings";
	}

}
