package org.jboss.tools.hibernate.proxy;

import org.hibernate.engine.spi.Mapping;
import org.jboss.tools.hibernate.runtime.common.AbstractMappingFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class MappingProxy extends AbstractMappingFacade {
	
	private Mapping target = null;
	
	public MappingProxy(
			IFacadeFactory facadeFactory, 
			Mapping m) {
		super(facadeFactory, m);
		target = m;
	}

	public Object getTarget() {
		return target;
	}

}
