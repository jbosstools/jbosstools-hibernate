package org.jboss.tools.hibernate.proxy;

import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringRepresentableType;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.spi.IType;

public class TypeProxy implements IType {
	
	private Type target = null;

	public TypeProxy(Type type) {
		target = type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toString(Object value) {
		String result = null;
		if (target instanceof StringRepresentableType) {
			result = ((StringRepresentableType<Object>)target).toString(value);
		}
		return result;
	}

	@Override
	public String getName() {
		return target.getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object fromStringValue(String value) {
		Object result = null;
		if (target instanceof StringRepresentableType) {
			result = ((StringRepresentableType<Object>)target).fromStringValue(value);
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
	
	Type getTarget() {
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

}
