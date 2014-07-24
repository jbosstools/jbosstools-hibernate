package org.jboss.tools.hibernate.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.jboss.tools.hibernate.proxy.ServiceProxy;
import org.jboss.tools.hibernate.spi.ITable;
import org.jboss.tools.hibernate.spi.IType;
import org.jboss.tools.hibernate.spi.ITypeFactory;

public class TypeFormats {

	static final Map<IType, String> typeFormats = new HashMap<IType, String>();
	static {
		
		ITypeFactory typeFactory = 
				new ServiceProxy().newTypeFactory();		
		addTypeFormat(typeFactory.getBooleanType(), Boolean.TRUE);
		addTypeFormat(typeFactory.getByteType(), Byte.valueOf((byte) 42));
		addTypeFormat(typeFactory.getBigIntegerType(), BigInteger.valueOf(42));
		addTypeFormat(typeFactory.getShortType(), Short.valueOf((short) 42));
		addTypeFormat(typeFactory.getCalendarType(), new GregorianCalendar());
		addTypeFormat(typeFactory.getCalendarDateType(), new GregorianCalendar());
		addTypeFormat(typeFactory.getIntegerType(), Integer.valueOf(42));
		addTypeFormat(typeFactory.getBigDecimalType(), new BigDecimal(42.0));
		addTypeFormat(typeFactory.getCharacterType(), Character.valueOf('h'));
		addTypeFormat(typeFactory.getClassType(), ITable.class);
		addTypeFormat(typeFactory.getCurrencyType(), Currency.getInstance(Locale.getDefault()));
		addTypeFormat(typeFactory.getDateType(), new Date());
		addTypeFormat(typeFactory.getDoubleType(), Double.valueOf(42.42));
		addTypeFormat(typeFactory.getFloatType(), Float.valueOf((float)42.42));
		addTypeFormat(typeFactory.getLocaleType(), Locale.getDefault());
		addTypeFormat(typeFactory.getLongType(), Long.valueOf(42));
		addTypeFormat(typeFactory.getStringType(), "a string"); //$NON-NLS-1$
		addTypeFormat(typeFactory.getTextType(), "a text"); //$NON-NLS-1$
		addTypeFormat(typeFactory.getTimeType(), new Date());
		addTypeFormat(typeFactory.getTimestampType(), new Date());
		addTypeFormat(typeFactory.getTimezoneType(), TimeZone.getDefault());
		addTypeFormat(typeFactory.getTrueFalseType(), Boolean.TRUE);
		addTypeFormat(typeFactory.getYesNoType(), Boolean.TRUE);
	}


	private static void addTypeFormat(IType type, Object value) {
		typeFormats.put(type, type.toString(value));
	}
	
	public static Map<IType, String> getTypeFormats() {
		return typeFormats;
	}
	
}
