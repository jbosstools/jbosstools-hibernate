package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.jboss.tools.hibernate.runtime.common.AbstractPOJOClassFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class POJOClassFacadeImpl extends AbstractPOJOClassFacade {
	
	public POJOClassFacadeImpl(IFacadeFactory facadeFactory, POJOClass pojoClass) {
		super(facadeFactory, pojoClass);
	}

}
