package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.util.Map;

import org.hibernate.type.BasicTypeRegistry;
import org.jboss.tools.hibernate.runtime.common.AbstractTypeFactoryFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;

public class TypeFactoryFacadeImpl extends AbstractTypeFactoryFacade {
	
	private BasicTypeRegistry typeRegistry = new BasicTypeRegistry();

	public TypeFactoryFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public IType getNamedType(String typeName) {
		return getFacadeFactory().createType(typeRegistry.getRegisteredType(typeName));
	}

	@Override
	public IType getBasicType(String typeName) {
		return getNamedType(typeName);
	}

	@Override
	public Map<IType, String> getTypeFormats() {
		if (typeFormats == null) {
			initializeTypeFormats();
		}
		return typeFormats;
	}
	
}
