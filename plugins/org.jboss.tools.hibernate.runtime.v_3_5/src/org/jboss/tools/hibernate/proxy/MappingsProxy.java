package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.Mappings;
import org.jboss.tools.hibernate.runtime.common.AbstractMappingsFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class MappingsProxy extends AbstractMappingsFacade {
	
	private Mappings target = null;

	public MappingsProxy(
			IFacadeFactory facadeFactory,
			Mappings mappings) {
		super(facadeFactory, mappings);
		target = mappings;
	}

}
