package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import org.hibernate.type.spi.TypeConfiguration;
import org.jboss.tools.hibernate.runtime.common.AbstractTypeFactoryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

public class TypeFactoryFacadeImpl extends AbstractTypeFactoryFacade {

	public TypeFactoryFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	protected Object getTypeRegistry() {
		if (typeRegistry == null) {
			typeRegistry = new TypeConfiguration().getBasicTypeRegistry();
		}
		return typeRegistry;
	}
	
}
