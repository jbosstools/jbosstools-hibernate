package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.type.CollectionType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.AbstractTypeFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class TypeFacadeImpl extends AbstractTypeFacade {
	
	public TypeFacadeImpl(
			IFacadeFactory facadeFactory,
			Type type) {
		super(facadeFactory, type);
	}	

	public Type getTarget() {
		return (Type)super.getTarget();
	}

	@Override
	public boolean isIntegerType() {
		return getTarget() instanceof IntegerType;
	}

	@Override
	public boolean isArrayType() {
		if (getTarget() instanceof CollectionType) {
			return ((CollectionType)getTarget()).isArrayType();
		} else {
			return false;
		}
	}

	@Override
	public boolean isInstanceOfPrimitiveType() {
		return getTarget() instanceof PrimitiveType;
	}

	@Override
	public Class<?> getPrimitiveClass() {
		return isInstanceOfPrimitiveType() ? 
				((PrimitiveType<?>)getTarget()).getPrimitiveClass() :
					null;
	}

	@Override
	public String getRole() {
		assert getTarget() instanceof CollectionType;
		return ((CollectionType)getTarget()).getRole();
	}

}
