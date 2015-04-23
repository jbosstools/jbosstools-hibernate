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
import org.jboss.tools.hibernate.runtime.common.AbstractTypeFactoryFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;

public class TypeFactoryProxy extends AbstractTypeFactoryFacade {

	private Map<IType, String> typeFormats = null;
	
	public TypeFactoryProxy(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public IType getBooleanType() {
		return getFacadeFactory().createType(Hibernate.BOOLEAN);
	}

	@Override
	public IType getByteType() {
		return getFacadeFactory().createType(Hibernate.BYTE);
	}

	@Override
	public IType getBigIntegerType() {
		return getFacadeFactory().createType(Hibernate.BIG_INTEGER);
	}

	@Override
	public IType getShortType() {
		return getFacadeFactory().createType(Hibernate.SHORT);
	}

	@Override
	public IType getCalendarType() {
		return getFacadeFactory().createType(Hibernate.CALENDAR);
	}

	@Override
	public IType getCalendarDateType() {
		return getFacadeFactory().createType(Hibernate.CALENDAR_DATE);
	}

	@Override
	public IType getIntegerType() {
		return getFacadeFactory().createType(Hibernate.INTEGER);
	}

	@Override
	public IType getBigDecimalType() {
		return getFacadeFactory().createType(Hibernate.BIG_DECIMAL);
	}

	@Override
	public IType getCharacterType() {
		return getFacadeFactory().createType(Hibernate.CHARACTER);
	}

	@Override
	public IType getClassType() {
		return getFacadeFactory().createType(Hibernate.CLASS);
	}

	@Override
	public IType getCurrencyType() {
		return getFacadeFactory().createType(Hibernate.CURRENCY);
	}

	@Override
	public IType getDateType() {
		return getFacadeFactory().createType(Hibernate.DATE);
	}

	@Override
	public IType getDoubleType() {
		return getFacadeFactory().createType(Hibernate.DOUBLE);
	}

	@Override
	public IType getFloatType() {
		return getFacadeFactory().createType(Hibernate.FLOAT);
	}

	@Override
	public IType getLocaleType() {
		return getFacadeFactory().createType(Hibernate.LOCALE);
	}

	@Override
	public IType getLongType() {
		return getFacadeFactory().createType(Hibernate.LONG);
	}

	@Override
	public IType getStringType() {
		return getFacadeFactory().createType(Hibernate.STRING);
	}

	@Override
	public IType getTextType() {
		return getFacadeFactory().createType(Hibernate.TEXT);
	}

	@Override
	public IType getTimeType() {
		return getFacadeFactory().createType(Hibernate.TIME);
	}

	@Override
	public IType getTimestampType() {
		return getFacadeFactory().createType(Hibernate.TIMESTAMP);
	}

	@Override
	public IType getTimezoneType() {
		return getFacadeFactory().createType(Hibernate.TIMEZONE);
	}

	@Override
	public IType getTrueFalseType() {
		return getFacadeFactory().createType(Hibernate.TRUE_FALSE);
	}

	@Override
	public IType getYesNoType() {
		return getFacadeFactory().createType(Hibernate.YES_NO);
	}

	@Override
	public IType getNamedType(String typeName) {
		return getFacadeFactory().createType(TypeFactory.heuristicType(typeName));
	}

	@Override
	public IType getBasicType(String type) {
		return getFacadeFactory().createType(TypeFactory.basic(type));
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
