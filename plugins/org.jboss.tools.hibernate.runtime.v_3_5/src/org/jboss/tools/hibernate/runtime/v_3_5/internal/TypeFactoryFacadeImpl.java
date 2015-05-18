package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.util.Map;

import org.hibernate.type.TypeFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractTypeFactoryFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;

public class TypeFactoryFacadeImpl extends AbstractTypeFactoryFacade {

	public TypeFactoryFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	protected String getStandardBasicTypesClassName() {
		return "org.hibernate.Hibernate";
	}

	@Override
	public IType getNamedType(String typeName) {
		return getFacadeFactory().createType(TypeFactory.heuristicType(typeName));
	}

	@Override
	public IType getBasicType(String type) {
		return getFacadeFactory().createType(TypeFactory.basic(type));
	}

	@Override
	public Map<IType, String> getTypeFormats() {
		if (typeFormats == null) {
			initializeTypeFormats();
		}
		return typeFormats;
	}
	
}
