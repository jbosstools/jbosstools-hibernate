package org.jboss.tools.hibernate.proxy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.hibernate.Hibernate;
import org.hibernate.type.TypeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;

public class TypeFactoryProxy implements ITypeFactory {

	private Map<IType, String> typeFormats = null;

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

	@Override
	public IType getNamedType(String typeName) {
		return new TypeProxy(TypeFactory.heuristicType(typeName));
	}

	@Override
	public IType getBasicType(String type) {
		return new TypeProxy(TypeFactory.basic(type));
	}

	@Override
	public Map<IType, String> getTypeFormats() {
		if (typeFormats == null) {
			initializeTypeFormats();
		}
		return typeFormats;
	}
	
	private void initializeTypeFormats() {
		typeFormats = new HashMap<IType, String>();		
		addTypeFormat(getBooleanType(), Boolean.TRUE);
		addTypeFormat(getByteType(), Byte.valueOf((byte) 42));
		addTypeFormat(getBigIntegerType(), BigInteger.valueOf(42));
		addTypeFormat(getShortType(), Short.valueOf((short) 42));
		addTypeFormat(getCalendarType(), new GregorianCalendar());
		addTypeFormat(getCalendarDateType(), new GregorianCalendar());
		addTypeFormat(getIntegerType(), Integer.valueOf(42));
		addTypeFormat(getBigDecimalType(), new BigDecimal(42.0));
		addTypeFormat(getCharacterType(), Character.valueOf('h'));
		addTypeFormat(getClassType(), ITable.class);
		addTypeFormat(getCurrencyType(), Currency.getInstance(Locale.getDefault()));
		addTypeFormat(getDateType(), new Date());
		addTypeFormat(getDoubleType(), Double.valueOf(42.42));
		addTypeFormat(getFloatType(), Float.valueOf((float)42.42));
		addTypeFormat(getLocaleType(), Locale.getDefault());
		addTypeFormat(getLongType(), Long.valueOf(42));
		addTypeFormat(getStringType(), "a string"); //$NON-NLS-1$
		addTypeFormat(getTextType(), "a text"); //$NON-NLS-1$
		addTypeFormat(getTimeType(), new Date());
		addTypeFormat(getTimestampType(), new Date());
		addTypeFormat(getTimezoneType(), TimeZone.getDefault());
		addTypeFormat(getTrueFalseType(), Boolean.TRUE);
		addTypeFormat(getYesNoType(), Boolean.TRUE);
	}
	
	private void addTypeFormat(IType type, Object value) {
		typeFormats.put(type, type.toString(value));
	}
}
