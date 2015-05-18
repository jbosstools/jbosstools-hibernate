package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractTypeFactoryFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;

public class TypeFactoryFacadeImpl extends AbstractTypeFactoryFacade {
	
	public TypeFactoryFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public IType getBasicType(String typeName) {
		return getNamedType(typeName);
	}

}
