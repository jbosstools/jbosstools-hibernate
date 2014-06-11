package org.jboss.tools.hibernate.proxy;

import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.StandardBasicTypes;
import org.jboss.tools.hibernate.spi.IType;
import org.jboss.tools.hibernate.spi.ITypeFactory;

public class TypeFactoryProxy implements ITypeFactory {
	
	BasicTypeRegistry typeRegistry = new BasicTypeRegistry();

	@Override
	public IType getBooleanType() {
		return new TypeProxy(StandardBasicTypes.BOOLEAN);
	}

	@Override
	public IType getByteType() {
		return new TypeProxy(StandardBasicTypes.BYTE);
	}

	@Override
	public IType getBigIntegerType() {
		return new TypeProxy(StandardBasicTypes.BIG_INTEGER);
	}

	@Override
	public IType getShortType() {
		return new TypeProxy(StandardBasicTypes.SHORT);
	}

	@Override
	public IType getCalendarType() {
		return new TypeProxy(StandardBasicTypes.CALENDAR);
	}

	@Override
	public IType getCalendarDateType() {
		return new TypeProxy(StandardBasicTypes.CALENDAR_DATE);
	}

	@Override
	public IType getIntegerType() {
		return new TypeProxy(StandardBasicTypes.INTEGER);
	}

	@Override
	public IType getBigDecimalType() {
		return new TypeProxy(StandardBasicTypes.BIG_DECIMAL);
	}

	@Override
	public IType getCharacterType() {
		return new TypeProxy(StandardBasicTypes.CHARACTER);
	}

	@Override
	public IType getClassType() {
		return new TypeProxy(StandardBasicTypes.CLASS);
	}

	@Override
	public IType getCurrencyType() {
		return new TypeProxy(StandardBasicTypes.CURRENCY);
	}

	@Override
	public IType getDateType() {
		return new TypeProxy(StandardBasicTypes.DATE);
	}

	@Override
	public IType getDoubleType() {
		return new TypeProxy(StandardBasicTypes.DOUBLE);
	}

	@Override
	public IType getFloatType() {
		return new TypeProxy(StandardBasicTypes.FLOAT);
	}

	@Override
	public IType getLocaleType() {
		return new TypeProxy(StandardBasicTypes.LOCALE);
	}

	@Override
	public IType getLongType() {
		return new TypeProxy(StandardBasicTypes.LONG);
	}

	@Override
	public IType getStringType() {
		return new TypeProxy(StandardBasicTypes.STRING);
	}

	@Override
	public IType getTextType() {
		return new TypeProxy(StandardBasicTypes.TEXT);
	}

	@Override
	public IType getTimeType() {
		return new TypeProxy(StandardBasicTypes.TIME);
	}

	@Override
	public IType getTimestampType() {
		return new TypeProxy(StandardBasicTypes.TIMESTAMP);
	}

	@Override
	public IType getTimezoneType() {
		return new TypeProxy(StandardBasicTypes.TIMEZONE);
	}

	@Override
	public IType getTrueFalseType() {
		return new TypeProxy(StandardBasicTypes.TRUE_FALSE);
	}

	@Override
	public IType getYesNoType() {
		return new TypeProxy(StandardBasicTypes.YES_NO);
	}

	@Override
	public IType getNamedType(String typeName) {
		return new TypeProxy(typeRegistry.getRegisteredType(typeName));
	}

}
