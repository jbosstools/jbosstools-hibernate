package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.runtime.common.AbstractNamingStrategyFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class NamingStrategyProxy extends AbstractNamingStrategyFacade {
	
	public NamingStrategyProxy(
			IFacadeFactory facadeFactory, 
			NamingStrategy namingStrategy) {
		super(facadeFactory, namingStrategy);
	}

}
