package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractSpecialRootClassFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public class SpecialRootClassFacadeImpl extends AbstractSpecialRootClassFacade {

	public SpecialRootClassFacadeImpl(IFacadeFactory facadeFactory, IProperty property) {
		super(facadeFactory, property);
	}

}
