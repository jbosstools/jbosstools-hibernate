package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.hibernate.Hibernate;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.junit.jupiter.api.Test;

public class TypeFactoryFacadeTest {
	
	private ITypeFactory typeFactoryFacade = new FacadeFactoryImpl().createTypeFactory();
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetBooleanType() {
		Type typeTarget = Hibernate.BOOLEAN;
		IType typeFacade = typeFactoryFacade.getBooleanType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetByteType() {
		Type typeTarget = Hibernate.BYTE;
		IType typeFacade = typeFactoryFacade.getByteType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetBigIntegerType() {
		Type typeTarget = Hibernate.BIG_INTEGER;
		IType typeFacade = typeFactoryFacade.getBigIntegerType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetShortType() {
		Type typeTarget = Hibernate.SHORT;
		IType typeFacade = typeFactoryFacade.getShortType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetCalendarType() {
		Type typeTarget = Hibernate.CALENDAR;
		IType typeFacade = typeFactoryFacade.getCalendarType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetCalendarDateType() {
		Type typeTarget = Hibernate.CALENDAR_DATE;
		IType typeFacade = typeFactoryFacade.getCalendarDateType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetIntegerType() {
		Type typeTarget = Hibernate.INTEGER;
		IType typeFacade = typeFactoryFacade.getIntegerType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetBigDecimalType() {
		Type typeTarget = Hibernate.BIG_DECIMAL;
		IType typeFacade = typeFactoryFacade.getBigDecimalType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetCharacterType() {
		Type typeTarget = Hibernate.CHARACTER;
		IType typeFacade = typeFactoryFacade.getCharacterType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetClassType() {
		Type typeTarget = Hibernate.CLASS;
		IType typeFacade = typeFactoryFacade.getClassType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetCurrencyType() {
		Type typeTarget = Hibernate.CURRENCY;
		IType typeFacade = typeFactoryFacade.getCurrencyType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetDateType() {
		Type typeTarget = Hibernate.DATE;
		IType typeFacade = typeFactoryFacade.getDateType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetDoubleType() {
		Type typeTarget = Hibernate.DOUBLE;
		IType typeFacade = typeFactoryFacade.getDoubleType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetFloatType() {
		Type typeTarget = Hibernate.FLOAT;
		IType typeFacade = typeFactoryFacade.getFloatType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetLocaleType() {
		Type typeTarget = Hibernate.LOCALE;
		IType typeFacade = typeFactoryFacade.getLocaleType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetLongType() {
		Type typeTarget = Hibernate.LONG;
		IType typeFacade = typeFactoryFacade.getLongType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetStringType() {
		Type typeTarget = Hibernate.STRING;
		IType typeFacade = typeFactoryFacade.getStringType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetTextType() {
		Type typeTarget = Hibernate.TEXT;
		IType typeFacade = typeFactoryFacade.getTextType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetTimeType() {
		Type typeTarget = Hibernate.TIME;
		IType typeFacade = typeFactoryFacade.getTimeType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetTimestampType() {
		Type typeTarget = Hibernate.TIMESTAMP;
		IType typeFacade = typeFactoryFacade.getTimestampType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetTimezoneType() {
		Type typeTarget = Hibernate.TIMEZONE;
		IType typeFacade = typeFactoryFacade.getTimezoneType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetTrueFalseType() {
		Type typeTarget = Hibernate.TRUE_FALSE;
		IType typeFacade = typeFactoryFacade.getTrueFalseType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetYesNoType() {
		Type typeTarget = Hibernate.YES_NO;
		IType typeFacade = typeFactoryFacade.getYesNoType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetNamedType() {
		IType typeFacade = typeFactoryFacade.getNamedType(String.class.getName());
		assertSame(Hibernate.STRING, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetBasicType() {
		IType typeFacade = typeFactoryFacade.getBasicType(String.class.getName());
		assertSame(Hibernate.STRING, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTypeFormats() {
		Map<IType, String> typeFormats = typeFactoryFacade.getTypeFormats();
		assertEquals(23, typeFormats.size());
		assertEquals("true", typeFormats.get(typeFactoryFacade.getBooleanType()));
		assertEquals("42", typeFormats.get(typeFactoryFacade.getByteType()));
		assertEquals("42", typeFormats.get(typeFactoryFacade.getBigIntegerType()));
		assertEquals("42", typeFormats.get(typeFactoryFacade.getShortType()));
		assertEquals(
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 
				typeFormats.get(typeFactoryFacade.getCalendarType()).substring(0, 10));
		assertEquals(
				new SimpleDateFormat("dd MMMM yyyy").format(new Date()), 
				typeFormats.get(typeFactoryFacade.getCalendarDateType()));
		assertEquals("42", typeFormats.get(typeFactoryFacade.getIntegerType()));
		assertEquals("42", typeFormats.get(typeFactoryFacade.getBigDecimalType()));
		assertEquals("h", typeFormats.get(typeFactoryFacade.getCharacterType()));
		assertEquals(
				ITable.class.getName(), 
				typeFormats.get(typeFactoryFacade.getClassType()));
		assertEquals(
				Currency.getInstance(Locale.getDefault()).toString(), 
				typeFormats.get(typeFactoryFacade.getCurrencyType()));
		assertEquals(
				new SimpleDateFormat("dd MMMM yyyy").format(new Date()), 
				typeFormats.get(typeFactoryFacade.getDateType()));
		assertEquals("42.42", typeFormats.get(typeFactoryFacade.getDoubleType()));
		assertEquals("42.42", typeFormats.get(typeFactoryFacade.getFloatType()));
		assertEquals(
				Locale.getDefault().toString(), 
				typeFormats.get(typeFactoryFacade.getLocaleType()));
		assertEquals("42", typeFormats.get(typeFactoryFacade.getLongType()));
		assertEquals("a string", typeFormats.get(typeFactoryFacade.getStringType()));
		assertEquals("a text", typeFormats.get(typeFactoryFacade.getTextType()));
		assertEquals(8, typeFormats.get(typeFactoryFacade.getTimeType()).length());
		assertEquals(
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 
				typeFormats.get(typeFactoryFacade.getTimestampType()).substring(0, 10));
		assertEquals(
				TimeZone.getDefault().getID(), 
				typeFormats.get(typeFactoryFacade.getTimezoneType()));
		assertEquals("true", typeFormats.get(typeFactoryFacade.getTrueFalseType()));
		assertEquals("true", typeFormats.get(typeFactoryFacade.getYesNoType()));
	}
	
}
