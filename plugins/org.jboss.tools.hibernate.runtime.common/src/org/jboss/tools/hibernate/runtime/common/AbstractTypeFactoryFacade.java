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
	
	private static IType BOOLEAN_TYPE = null;
	private static IType BYTE_TYPE = null;
	private static IType BIG_INTEGER_TYPE = null;
	private static IType SHORT_TYPE = null;
	private static IType CALENDAR_TYPE = null;
	private static IType CALENDAR_DATE_TYPE = null;
	private static IType INTEGER_TYPE = null;
	private static IType BIG_DECIMAL_TYPE = null;
	private static IType CHARACTER_TYPE = null;
	private static IType CLASS_TYPE = null;
	private static IType CURRENCY_TYPE = null;
	private static IType DATE_TYPE = null;
	private static IType DOUBLE_TYPE = null;
	private static IType FLOAT_TYPE = null;
	private static IType LOCALE_TYPE = null;
	private static IType LONG_TYPE = null;
	private static IType STRING_TYPE = null;
	private static IType TEXT_TYPE = null;
	private static IType TIME_TYPE = null;
	private static IType TIMESTAMP_TYPE = null;
	private static IType TIMEZONE_TYPE = null;
	private static IType TRUE_FALSE_TYPE = null;
	private static IType YES_NO_TYPE = null;

	protected Map<IType, String> typeFormats = null;
	protected Object typeRegistry = null;


	public AbstractTypeFactoryFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	
	
	public IType getBooleanType() {
		if (BOOLEAN_TYPE == null) {
			BOOLEAN_TYPE =  getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"BOOLEAN", 
					null));
		}
		return BOOLEAN_TYPE;
	}
	
	@Override
	public IType getByteType() {
		if (BYTE_TYPE == null) {
			BYTE_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"BYTE", 
					null));
		}
		return BYTE_TYPE;
	}

	@Override
	public IType getBigIntegerType() {
		if (BIG_INTEGER_TYPE == null) {
			BIG_INTEGER_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"BIG_INTEGER", 
					null));
		}
		return BIG_INTEGER_TYPE;
	}

	@Override
	public IType getShortType() {
		if (SHORT_TYPE == null) { 
			SHORT_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"SHORT", 
					null));
		}
		return SHORT_TYPE;
	}

	@Override
	public IType getCalendarType() {
		if (CALENDAR_TYPE == null) {
			CALENDAR_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"CALENDAR", 
					null));
		} 
		return CALENDAR_TYPE;
	}

	@Override
	public IType getCalendarDateType() {
		if (CALENDAR_DATE_TYPE == null) {
			CALENDAR_DATE_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"CALENDAR_DATE", 
					null));
		}
		return CALENDAR_DATE_TYPE;
	}

	@Override
	public IType getIntegerType() {
		if (INTEGER_TYPE == null) {
			INTEGER_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"INTEGER", 
					null));
		}
		return INTEGER_TYPE;
	}

	@Override
	public IType getBigDecimalType() {
		if (BIG_DECIMAL_TYPE == null) {
			BIG_DECIMAL_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"BIG_DECIMAL", 
					null));
		}
		return BIG_DECIMAL_TYPE;
	}

	@Override
	public IType getCharacterType() {
		if (CHARACTER_TYPE == null) {
			CHARACTER_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"CHARACTER", 
					null));
		}
		return CHARACTER_TYPE;
	}

	@Override
	public IType getClassType() {
		if (CLASS_TYPE == null) {
			CLASS_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"CLASS", 
					null));
		}
		return CLASS_TYPE;
	}

	@Override
	public IType getCurrencyType() {
		if (CURRENCY_TYPE == null) {
			CURRENCY_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"CURRENCY", 
					null));
		}
		return CURRENCY_TYPE;
	}

	@Override
	public IType getDateType() {
		if (DATE_TYPE == null) {
			DATE_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"DATE", 
					null));
			}
		return DATE_TYPE;
	}

	@Override
	public IType getDoubleType() {
		if (DOUBLE_TYPE == null) {
			DOUBLE_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"DOUBLE", 
					null));
			}
		return DOUBLE_TYPE;
	}

	@Override
	public IType getFloatType() {
		if (FLOAT_TYPE == null) {
			FLOAT_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"FLOAT", 
					null));
		}
		return FLOAT_TYPE;
	}

	@Override
	public IType getLocaleType() {
		if (LOCALE_TYPE == null) {
			LOCALE_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"LOCALE", 
					null));
			}
		return LOCALE_TYPE;
	}

	@Override
	public IType getLongType() {
		if (LONG_TYPE == null) {
			LONG_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"LONG", 
					null));
			}
		return LONG_TYPE;
	}

	@Override
	public IType getStringType() {
		if (STRING_TYPE == null) {
			STRING_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"STRING", 
					null));
			}
		return STRING_TYPE;
	}

	@Override
	public IType getTextType() {
		if (TEXT_TYPE == null) {
			TEXT_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"TEXT", 
					null));
			}
		return TEXT_TYPE;
	}

	@Override
	public IType getTimeType() {
		if (TIME_TYPE == null) {
			TIME_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"TIME", 
					null));
			}
		return TIME_TYPE;
	}

	@Override
	public IType getTimestampType() {
		if (TIMESTAMP_TYPE == null) {
			TIMESTAMP_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"TIMESTAMP", 
					null));
			}
		return TIMESTAMP_TYPE;
	}

	@Override
	public IType getTimezoneType() {
		if (TIMEZONE_TYPE == null) {
			TIMEZONE_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"TIMEZONE", 
					null));
			}
		return TIMEZONE_TYPE;
	}

	@Override
	public IType getTrueFalseType() {
		if (TRUE_FALSE_TYPE == null) {
			TRUE_FALSE_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"TRUE_FALSE", 
					null));
			}
		return TRUE_FALSE_TYPE;
	}

	@Override
	public IType getYesNoType() {
		if (YES_NO_TYPE == null) {
			YES_NO_TYPE = getFacadeFactory().createType(Util.getFieldValue(
					getStandardBasicTypesClass(), 
					"YES_NO", 
					null));
			}
		return YES_NO_TYPE;
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
