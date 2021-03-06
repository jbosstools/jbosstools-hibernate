package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IType;

public abstract class AbstractTypeFacade 
extends AbstractFacade 
implements IType {

	public AbstractTypeFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}
	
	@Override
	public String toString(Object value) {
		String result = null;
		if (getStringRepresentableTypeClass().isAssignableFrom(
				getTarget().getClass())) {
			result = (String)Util.invokeMethod(
					getTarget(), 
					"toString", 
					new Class[] { Object.class }, 
					new Object[] { value });
		}
		return result;
	}
	
	@Override
	public String getName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getName", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public Object fromStringValue(String value) {
		Object result = null;
		if (getStringRepresentableTypeClass().isAssignableFrom(
				getTarget().getClass())) {
			result = Util.invokeMethod(
					getTarget(), 
					"fromStringValue", 
					new Class[] { String.class }, 
					new Object[] { value });
		}
		return result;
	}

	@Override
	public boolean isEntityType() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isEntityType", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public boolean isOneToOne() {
		boolean result = false;
		if (isEntityType()) {
			result = (boolean)Util.invokeMethod(
					getTarget(), 
					"isOneToOne", 
					new Class[] {}, 
					new Object[] {});
		}
		return result;
	}

	@Override
	public boolean isAnyType() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isAnyType", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public boolean isComponentType() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isComponentType", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public boolean isCollectionType() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isCollectionType", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public String getReturnedClassName() {
		Class<?> returnedClass = (Class<?>)Util.invokeMethod(
				getTarget(), 
				"getReturnedClass", 
				new Class[] {}, 
				new Object[] {});
		if (returnedClass != null) {
			return returnedClass.getName();
		} else {
			return null;
		}
	}
	
	@Override
	public String getAssociatedEntityName() {
		String result = null;
		if (isEntityType()) {
			result = (String)Util.invokeMethod(
					getTarget(), 
					"getAssociatedEntityName", 
					new Class[] {}, 
					new Object[] {});
		}
		return result;
	}

	@Override
	public boolean isIntegerType() {
		return getIntegerTypeClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isArrayType() {
		boolean result = false;
		if (isCollectionType()) {
			result = (boolean)Util.invokeMethod(
					getTarget(), 
					"isArrayType", 
					new Class[] {}, 
					new Object[] {});
		}
		return result;
	}

	@Override
	public boolean isInstanceOfPrimitiveType() {
		return getPrimitiveTypeClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public Class<?> getPrimitiveClass() {
		Class<?> result = null;
		if (isInstanceOfPrimitiveType()) {
			result = (Class<?>)Util.invokeMethod(
					getTarget(), "getPrimitiveClass", 
					new Class[] {}, 
					new Object[] {});
		}
		return result;
	}

	@Override
	public String getRole() {
		String result = null;
		if (isCollectionType()) {
			result = (String)Util.invokeMethod(
					getTarget(), 
					"getRole", 
					new Class[] {}, 
					new Object[] {});
		}
		return result;
	}

	protected Class<?> getStringRepresentableTypeClass() {
		return Util.getClass(
				getStringRepresentableTypeClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getIntegerTypeClass() {
		return Util.getClass(
				getIntegerTypeClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getPrimitiveTypeClass() {
		return Util.getClass(
				getPrimitiveTypeClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected String getStringRepresentableTypeClassName() {
		return "org.hibernate.type.StringRepresentableType";
	}
	
	protected String getIntegerTypeClassName() {
		return "org.hibernate.type.IntegerType";
	}
	
	protected String getPrimitiveTypeClassName() {
		return "org.hibernate.type.PrimitiveType";
	}

}
