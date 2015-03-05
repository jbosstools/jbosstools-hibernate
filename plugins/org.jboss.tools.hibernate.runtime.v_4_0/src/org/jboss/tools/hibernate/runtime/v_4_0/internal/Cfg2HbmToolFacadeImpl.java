package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractCfg2HbmToolFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class Cfg2HbmToolFacadeImpl extends AbstractCfg2HbmToolFacade {
	
	public Cfg2HbmToolFacadeImpl(IFacadeFactory facadeFactory) {
		this.facadeFactory = facadeFactory;
	}
	
}
