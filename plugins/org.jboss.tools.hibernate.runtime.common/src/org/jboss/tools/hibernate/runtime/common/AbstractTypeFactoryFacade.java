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

	@Override
	public IType getCurrencyType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"CURRENCY", 
				null));
	}

	@Override
	public IType getDateType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"DATE", 
				null));
	}

	@Override
	public IType getDoubleType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"DOUBLE", 
				null));
	}

	@Override
	public IType getFloatType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"FLOAT", 
				null));
	}

	@Override
	public IType getLocaleType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"LOCALE", 
				null));
	}

	@Override
	public IType getLongType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"LONG", 
				null));
	}

	@Override
	public IType getStringType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"STRING", 
				null));
	}

	@Override
	public IType getTextType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"TEXT", 
				null));
	}

	@Override
	public IType getTimeType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"TIME", 
				null));
	}

	@Override
	public IType getTimestampType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"TIMESTAMP", 
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
