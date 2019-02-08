package org.jboss.tools.hibernate.runtime.v_5_3.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractClassMetadataFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

public class ClassMetadataFacadeImpl extends AbstractClassMetadataFacade {

	public ClassMetadataFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	protected String getSessionImplementorClassName() {
		return "org.hibernate.engine.spi.SharedSessionContractImplementor";
	}

}
