package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import org.jboss.tools.hibernate.runtime.common.AbstractTypeFactoryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.BigDecimalType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.BigIntegerType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.BooleanType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.ByteType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.CalendarDateType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.CalendarType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.CharacterType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.ClassType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.CurrencyType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.DateType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.DoubleType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.FloatType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.IntegerType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.LocaleType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.LongType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.ShortType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.StringType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.TextType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.TimeType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.TimeZoneType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.TimestampType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.TrueFalseType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.YesNoType;

public class TypeFactoryFacadeImpl extends AbstractTypeFactoryFacade {

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

	public TypeFactoryFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public IType getBooleanType() {
		if (BOOLEAN_TYPE == null) {
			BOOLEAN_TYPE =  getFacadeFactory().createType(BooleanType.INSTANCE);
		}
		return BOOLEAN_TYPE;
	}
	
	@Override
	public IType getByteType() {
		if (BYTE_TYPE == null) {
			BYTE_TYPE = getFacadeFactory().createType(ByteType.INSTANCE);
		}
		return BYTE_TYPE;
	}

	@Override
	public IType getBigIntegerType() {
		if (BIG_INTEGER_TYPE == null) {
			BIG_INTEGER_TYPE = getFacadeFactory().createType(BigIntegerType.INSTANCE);
		}
		return BIG_INTEGER_TYPE;
	}

	@Override
	public IType getShortType() {
		if (SHORT_TYPE == null) { 
			SHORT_TYPE = getFacadeFactory().createType(ShortType.INSTANCE);
		}
		return SHORT_TYPE;
	}

	@Override
	public IType getCalendarType() {
		if (CALENDAR_TYPE == null) {
			CALENDAR_TYPE = getFacadeFactory().createType(CalendarType.INSTANCE);
		} 
		return CALENDAR_TYPE;
	}

	@Override
	public IType getCalendarDateType() {
		if (CALENDAR_DATE_TYPE == null) {
			CALENDAR_DATE_TYPE = getFacadeFactory().createType(CalendarDateType.INSTANCE);
		}
		return CALENDAR_DATE_TYPE;
	}

	@Override
	public IType getIntegerType() {
		if (INTEGER_TYPE == null) {
			INTEGER_TYPE = getFacadeFactory().createType(IntegerType.INSTANCE);
		}
		return INTEGER_TYPE;
	}

	@Override
	public IType getBigDecimalType() {
		if (BIG_DECIMAL_TYPE == null) {
			BIG_DECIMAL_TYPE = getFacadeFactory().createType(BigDecimalType.INSTANCE);
		}
		return BIG_DECIMAL_TYPE;
	}

	@Override
	public IType getCharacterType() {
		if (CHARACTER_TYPE == null) {
			CHARACTER_TYPE = getFacadeFactory().createType(CharacterType.INSTANCE);
		}
		return CHARACTER_TYPE;
	}

	@Override
	public IType getClassType() {
		if (CLASS_TYPE == null) {
			CLASS_TYPE = getFacadeFactory().createType(ClassType.INSTANCE);
		}
		return CLASS_TYPE;
	}

	@Override
	public IType getCurrencyType() {
		if (CURRENCY_TYPE == null) {
			CURRENCY_TYPE = getFacadeFactory().createType(CurrencyType.INSTANCE);
		}
		return CURRENCY_TYPE;
	}

	@Override
	public IType getDateType() {
		if (DATE_TYPE == null) {
			DATE_TYPE = getFacadeFactory().createType(DateType.INSTANCE);
			}
		return DATE_TYPE;
	}

	@Override
	public IType getDoubleType() {
		if (DOUBLE_TYPE == null) {
			DOUBLE_TYPE = getFacadeFactory().createType(DoubleType.INSTANCE);
			}
		return DOUBLE_TYPE;
	}

	@Override
	public IType getFloatType() {
		if (FLOAT_TYPE == null) {
			FLOAT_TYPE = getFacadeFactory().createType(FloatType.INSTANCE);
		}
		return FLOAT_TYPE;
	}

	@Override
	public IType getLocaleType() {
		if (LOCALE_TYPE == null) {
			LOCALE_TYPE = getFacadeFactory().createType(LocaleType.INSTANCE);
			}
		return LOCALE_TYPE;
	}

	@Override
	public IType getLongType() {
		if (LONG_TYPE == null) {
			LONG_TYPE = getFacadeFactory().createType(LongType.INSTANCE);
			}
		return LONG_TYPE;
	}

	@Override
	public IType getStringType() {
		if (STRING_TYPE == null) {
			STRING_TYPE = getFacadeFactory().createType(StringType.INSTANCE);
			}
		return STRING_TYPE;
	}

	@Override
	public IType getTextType() {
		if (TEXT_TYPE == null) {
			TEXT_TYPE = getFacadeFactory().createType(TextType.INSTANCE);
			}
		return TEXT_TYPE;
	}

	@Override
	public IType getTimeType() {
		if (TIME_TYPE == null) {
			TIME_TYPE = getFacadeFactory().createType(TimeType.INSTANCE);
			}
		return TIME_TYPE;
	}

	@Override
	public IType getTimestampType() {
		if (TIMESTAMP_TYPE == null) {
			TIMESTAMP_TYPE = getFacadeFactory().createType(TimestampType.INSTANCE);
			}
		return TIMESTAMP_TYPE;
	}

	@Override
	public IType getTimezoneType() {
		if (TIMEZONE_TYPE == null) {
			TIMEZONE_TYPE = getFacadeFactory().createType(TimeZoneType.INSTANCE);
			}
		return TIMEZONE_TYPE;
	}

	@Override
	public IType getTrueFalseType() {
		if (TRUE_FALSE_TYPE == null) {
			TRUE_FALSE_TYPE = getFacadeFactory().createType(TrueFalseType.INSTANCE);
			}
		return TRUE_FALSE_TYPE;
	}

	@Override
	public IType getYesNoType() {
		if (YES_NO_TYPE == null) {
			YES_NO_TYPE = getFacadeFactory().createType(YesNoType.INSTANCE);
			}
		return YES_NO_TYPE;
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
//		addTypeFormat(getDateType(), new Date());
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
	
}
