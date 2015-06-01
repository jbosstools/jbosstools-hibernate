package org.jboss.tools.hibernate.runtime.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;

public abstract class AbstractTypeFactoryFacade 
extends AbstractFacade 
implements ITypeFactory {

	protected Map<IType, String> typeFormats = null;
	protected Object typeRegistry = null;


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

	@Override
	public IType getTimezoneType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"TIMEZONE", 
				null));
	}

	@Override
	public IType getTrueFalseType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"TRUE_FALSE", 
				null));
	}

	@Override
	public IType getYesNoType() {
		return getFacadeFactory().createType(Util.getFieldValue(
				getStandardBasicTypesClass(), 
				"YES_NO", 
				null));
	}

	@Override
	public Map<IType, String> getTypeFormats() {
		if (typeFormats == null) {
			initializeTypeFormats();
		}
		return typeFormats;
	}
	
	@Override
	public IType getNamedType(String typeName) {
		Object typeTarget = Util.invokeMethod(
				getTypeRegistry(), 
				"getRegisteredType", 
				new Class[] { String.class }, 
				new Object[] { typeName });
		return getFacadeFactory().createType(typeTarget);
	}
	
	@Override
	public IType getBasicType(String typeName) {
		return getNamedType(typeName);
	}
	
	protected Class<?> getStandardBasicTypesClass() {
		return Util.getClass(
				getStandardBasicTypesClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected String getStandardBasicTypesClassName() {
		return "org.hibernate.type.StandardBasicTypes";
	}

	protected void initializeTypeFormats() {
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
	
	protected void addTypeFormat(IType type, Object value) {
		typeFormats.put(type, type.toString(value));
	}
	
	protected Object getTypeRegistry() {
		if (typeRegistry == null) {
			typeRegistry = Util.getInstance(
					getTypeRegistryClassName(), 
					getFacadeFactoryClassLoader());
		}
		return typeRegistry;
	}
	
	protected String getTypeRegistryClassName() {
		return "org.hibernate.type.BasicTypeRegistry";
	}

}
