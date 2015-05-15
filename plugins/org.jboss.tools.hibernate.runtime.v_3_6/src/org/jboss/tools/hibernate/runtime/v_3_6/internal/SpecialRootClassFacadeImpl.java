package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.runtime.common.AbstractSpecialRootClassFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public class SpecialRootClassFacadeImpl extends AbstractSpecialRootClassFacade {

	public SpecialRootClassFacadeImpl(
			IFacadeFactory facadeFactory, 
			IProperty property) {
		super(facadeFactory, new RootClass());
		this.property = property;
		generate();
	}

}
