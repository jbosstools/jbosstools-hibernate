package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
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

	protected String getStringRepresentableTypeClassName() {
		return "org.hibernate.type.NullableType";
	}

	@Override
	public boolean isEntityType() {
		return getTarget().isEntityType();
	}

	@Override
	public boolean isOneToOne() {
		if (getTarget().isEntityType()) {
			return ((EntityType)getTarget()).isOneToOne();
		} else {
			return false;
		}
	}

	@Override
	public boolean isAnyType() {
		return getTarget().isAnyType();
	}

	@Override
	public boolean isComponentType() {
		return getTarget().isComponentType();
	}

	@Override
	public boolean isCollectionType() {
		return getTarget().isCollectionType();
	}

	@Override
	public Class<?> getReturnedClass() {
		return getTarget().getReturnedClass();
	}
	
	@Override
	public String getAssociatedEntityName() {
		if (getTarget().isEntityType()) {
			return ((EntityType)getTarget()).getAssociatedEntityName();
		} else {
			return null;
		}
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
				((PrimitiveType)getTarget()).getPrimitiveClass() :
					null;
	}

	@Override
	public String getRole() {
		assert getTarget() instanceof CollectionType;
		return ((CollectionType)getTarget()).getRole();
	}

}
