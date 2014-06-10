package org.jboss.tools.hibernate.proxy;

import org.hibernate.Hibernate;
import org.jboss.tools.hibernate.spi.IType;
import org.jboss.tools.hibernate.spi.ITypeFactory;

public class TypeFactoryProxy implements ITypeFactory {

	@Override
	public IType getBooleanType() {
		return new TypeProxy(Hibernate.BOOLEAN);
	}

	@Override
	public IType getByteType() {
		return new TypeProxy(Hibernate.BYTE);
	}

	@Override
	public IType getBigIntegerType() {
		return new TypeProxy(Hibernate.BIG_INTEGER);
	}

	@Override
	public IType getShortType() {
		return new TypeProxy(Hibernate.SHORT);
	}

	@Override
	public IType getCalendarType() {
		return new TypeProxy(Hibernate.CALENDAR);
	}

	@Override
	public IType getCalendarDateType() {
		return new TypeProxy(Hibernate.CALENDAR_DATE);
	}

	@Override
	public IType getIntegerType() {
		return new TypeProxy(Hibernate.INTEGER);
	}

	@Override
	public IType getBigDecimalType() {
		return new TypeProxy(Hibernate.BIG_DECIMAL);
	}

	@Override
	public IType getCharacterType() {
		return new TypeProxy(Hibernate.CHARACTER);
	}

	@Override
	public IType getClassType() {
		return new TypeProxy(Hibernate.CLASS);
	}

	@Override
	public IType getCurrencyType() {
		return new TypeProxy(Hibernate.CURRENCY);
	}

	@Override
	public IType getDateType() {
		return new TypeProxy(Hibernate.DATE);
	}

	@Override
	public IType getDoubleType() {
		return new TypeProxy(Hibernate.DOUBLE);
	}

	@Override
	public IType getFloatType() {
		return new TypeProxy(Hibernate.FLOAT);
	}

	@Override
	public IType getLocaleType() {
		return new TypeProxy(Hibernate.LOCALE);
	}

	@Override
	public IType getLongType() {
		return new TypeProxy(Hibernate.LONG);
	}

	@Override
	public IType getStringType() {
		return new TypeProxy(Hibernate.STRING);
	}

	@Override
	public IType getTextType() {
		return new TypeProxy(Hibernate.TEXT);
	}

	@Override
	public IType getTimeType() {
		return new TypeProxy(Hibernate.TIME);
	}

	@Override
	public IType getTimestampType() {
		return new TypeProxy(Hibernate.TIMESTAMP);
	}

	@Override
	public IType getTimezoneType() {
		return new TypeProxy(Hibernate.TIMEZONE);
	}

	@Override
	public IType getTrueFalseType() {
		return new TypeProxy(Hibernate.TRUE_FALSE);
	}

	@Override
	public IType getYesNoType() {
		return new TypeProxy(Hibernate.YES_NO);
	}

}
