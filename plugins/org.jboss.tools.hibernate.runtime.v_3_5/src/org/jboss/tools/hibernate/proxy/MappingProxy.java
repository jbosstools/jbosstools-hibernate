package org.jboss.tools.hibernate.proxy;

import org.hibernate.engine.Mapping;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IMapping;

public class MappingProxy implements IMapping {
	
	private Mapping target = null;
	
	public MappingProxy(IFacadeFactory facadeFactory, Mapping m) {
		target = m;
	}

	public Mapping getTarget() {
		return target;
	}

}
