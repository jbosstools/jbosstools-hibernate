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
	
	@Override
	public IType getByteType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"BYTE", 
				null));
	}

	@Override
	public IType getBigIntegerType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"BIG_INTEGER", 
				null));
	}

	@Override
	public IType getShortType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"SHORT", 
				null));
	}

	@Override
	public IType getCalendarType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"CALENDAR", 
				null));
	}

	@Override
	public IType getCalendarDateType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"CALENDAR_DATE", 
				null));
	}

	@Override
	public IType getIntegerType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"INTEGER", 
				null));
	}

	@Override
	public IType getBigDecimalType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"BIG_DECIMAL", 
				null));
	}

	@Override
	public IType getCharacterType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"CHARACTER", 
				null));
	}

	@Override
	public IType getClassType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"CLASS", 
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
