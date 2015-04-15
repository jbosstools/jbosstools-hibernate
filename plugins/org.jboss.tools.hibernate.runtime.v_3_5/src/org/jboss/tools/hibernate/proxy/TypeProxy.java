package org.jboss.tools.hibernate.proxy;

import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.NullableType;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.AbstractTypeFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class TypeProxy extends AbstractTypeFacade {
	
	private Type target = null;

	public TypeProxy(
			IFacadeFactory facadeFactory,
			Type type) {
		super(facadeFactory, type);
		target = type;
	}	

	@Override
	public String toString(Object value) {
		String result = null;
		if (target instanceof NullableType) {
			result = ((NullableType)target).toString(value);
		}
		return result;
	}

	@Override
	public String getName() {
		return target.getName();
	}

	@Override
	public Object fromStringValue(String value) {
		Object result = null;
		if (target instanceof NullableType) {
			result = ((NullableType)target).fromStringValue(value);
		}
		return result;
	}

	@Override
	public boolean isEntityType() {
		return target.isEntityType();
	}

	@Override
	public boolean isOneToOne() {
		if (target.isEntityType()) {
			return ((EntityType)target).isOneToOne();
		} else {
			return false;
		}
	}

	@Override
	public boolean isAnyType() {
		return target.isAnyType();
	}

	@Override
	public boolean isComponentType() {
		return target.isComponentType();
	}

	@Override
	public boolean isCollectionType() {
		return target.isCollectionType();
	}

	@Override
	public Class<?> getReturnedClass() {
		return target.getReturnedClass();
	}
	
	public Type getTarget() {
		return target;
	}

	@Override
	public String getAssociatedEntityName() {
		if (target.isEntityType()) {
			return ((EntityType)target).getAssociatedEntityName();
		} else {
			return null;
		}
	}

	@Override
	public boolean isIntegerType() {
		return target instanceof IntegerType;
	}

	@Override
	public boolean isArrayType() {
		if (target instanceof CollectionType) {
			return ((CollectionType)target).isArrayType();
		} else {
			return false;
		}
	}

	@Override
	public boolean isInstanceOfPrimitiveType() {
		return target instanceof PrimitiveType;
	}

	@Override
	public Class<?> getPrimitiveClass() {
		return isInstanceOfPrimitiveType() ? 
				((PrimitiveType)target).getPrimitiveClass() :
					null;
	}

	@Override
	public String getRole() {
		assert target instanceof CollectionType;
		return ((CollectionType)target).getRole();
	}

}
