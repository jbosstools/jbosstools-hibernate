package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractTypeFactoryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.Util;
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
		Object typeTarget = Util.invokeMethod(
				getTypeFactoryClass(), 
				"heuristicType", 
				new Class[] { String.class }, 
				new Object[] { typeName });
		return getFacadeFactory().createType(typeTarget);
	}

	@Override
	public IType getBasicType(String type) {
		Object typeTarget = Util.invokeMethod(
				getTypeFactoryClass(), 
				"basic", 
				new Class[] { String.class }, 
				new Object[] { type });
		return getFacadeFactory().createType(typeTarget);
	}
	
	private Class<?> getTypeFactoryClass() {
		return Util.getClass(
				getTypeFactoryClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	private String getTypeFactoryClassName() {
		return "org.hibernate.type.TypeFactory";
	}

}
