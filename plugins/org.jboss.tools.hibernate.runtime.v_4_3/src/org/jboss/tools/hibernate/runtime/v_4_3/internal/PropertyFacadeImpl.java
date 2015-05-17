package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.mapping.Property;
import org.jboss.tools.hibernate.runtime.common.AbstractPropertyFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class PropertyFacadeImpl extends AbstractPropertyFacade {
	
	public PropertyFacadeImpl(
			IFacadeFactory facadeFactory,
			Property property) {
		super(facadeFactory, property);
	}
	
}
