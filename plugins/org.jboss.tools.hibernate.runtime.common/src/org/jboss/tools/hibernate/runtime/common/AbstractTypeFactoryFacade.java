package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;

public abstract class AbstractTypeFactoryFacade 
extends AbstractFacade 
implements ITypeFactory {

	public AbstractTypeFactoryFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public IType getBooleanType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"BOOLEAN", 
				null));
	}
	
	protected Class<?> getStandardBasicTypesClass() {
		return Util.getClass(
				getStandardBasicTypesClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected String getStandardBasicTypesClassName() {
		return "org.hibernate.type.StandardBasicTypes";
	}

}
