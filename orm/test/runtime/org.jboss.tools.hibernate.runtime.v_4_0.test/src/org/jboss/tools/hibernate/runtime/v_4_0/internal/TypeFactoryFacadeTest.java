package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.junit.Assert;
import org.junit.Test;

public class TypeFactoryFacadeTest {
	
	private ITypeFactory typeFactoryFacade = new FacadeFactoryImpl().createTypeFactory();
	
	@Test
	public void testGetBooleanType() {
		Type typeTarget = StandardBasicTypes.BOOLEAN;
		IType typeFacade = typeFactoryFacade.getBooleanType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetByteType() {
		Type typeTarget = StandardBasicTypes.BYTE;
		IType typeFacade = typeFactoryFacade.getByteType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetBigIntegerType() {
		Type typeTarget = StandardBasicTypes.BIG_INTEGER;
		IType typeFacade = typeFactoryFacade.getBigIntegerType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetShortType() {
		Type typeTarget = StandardBasicTypes.SHORT;
		IType typeFacade = typeFactoryFacade.getShortType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCalendarType() {
		Type typeTarget = StandardBasicTypes.CALENDAR;
		IType typeFacade = typeFactoryFacade.getCalendarType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCalendarDateType() {
		Type typeTarget = StandardBasicTypes.CALENDAR_DATE;
		IType typeFacade = typeFactoryFacade.getCalendarDateType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetIntegerType() {
		Type typeTarget = StandardBasicTypes.INTEGER;
		IType typeFacade = typeFactoryFacade.getIntegerType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetBigDecimalType() {
		Type typeTarget = StandardBasicTypes.BIG_DECIMAL;
		IType typeFacade = typeFactoryFacade.getBigDecimalType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCharacterType() {
		Type typeTarget = StandardBasicTypes.CHARACTER;
		IType typeFacade = typeFactoryFacade.getCharacterType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetClassType() {
		Type typeTarget = StandardBasicTypes.CLASS;
		IType typeFacade = typeFactoryFacade.getClassType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCurrencyType() {
		Type typeTarget = StandardBasicTypes.CURRENCY;
		IType typeFacade = typeFactoryFacade.getCurrencyType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetDateType() {
		Type typeTarget = StandardBasicTypes.DATE;
		IType typeFacade = typeFactoryFacade.getDateType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetDoubleType() {
		Type typeTarget = StandardBasicTypes.DOUBLE;
		IType typeFacade = typeFactoryFacade.getDoubleType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetFloatType() {
		Type typeTarget = StandardBasicTypes.FLOAT;
		IType typeFacade = typeFactoryFacade.getFloatType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetLocaleType() {
		Type typeTarget = StandardBasicTypes.LOCALE;
		IType typeFacade = typeFactoryFacade.getLocaleType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetLongType() {
		Type typeTarget = StandardBasicTypes.LONG;
		IType typeFacade = typeFactoryFacade.getLongType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetStringType() {
		Type typeTarget = StandardBasicTypes.STRING;
		IType typeFacade = typeFactoryFacade.getStringType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTextType() {
		Type typeTarget = StandardBasicTypes.TEXT;
		IType typeFacade = typeFactoryFacade.getTextType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTimeType() {
		Type typeTarget = StandardBasicTypes.TIME;
		IType typeFacade = typeFactoryFacade.getTimeType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTimestampType() {
		Type typeTarget = StandardBasicTypes.TIMESTAMP;
		IType typeFacade = typeFactoryFacade.getTimestampType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTimezoneType() {
		Type typeTarget = StandardBasicTypes.TIMEZONE;
		IType typeFacade = typeFactoryFacade.getTimezoneType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTrueFalseType() {
		Type typeTarget = StandardBasicTypes.TRUE_FALSE;
		IType typeFacade = typeFactoryFacade.getTrueFalseType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetYesNoType() {
		Type typeTarget = StandardBasicTypes.YES_NO;
		IType typeFacade = typeFactoryFacade.getYesNoType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetNamedType() {
		IType typeFacade = typeFactoryFacade.getNamedType(String.class.getName());
		Assert.assertSame(StandardBasicTypes.STRING, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetBasicType() {
		IType typeFacade = typeFactoryFacade.getBasicType(String.class.getName());
		Assert.assertSame(StandardBasicTypes.STRING, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTypeFormats() {
		Map<IType, String> typeFormats = typeFactoryFacade.getTypeFormats();
		Assert.assertEquals(23, typeFormats.size());
		Assert.assertEquals("true", typeFormats.get(typeFactoryFacade.getBooleanType()));
		Assert.assertEquals("42", typeFormats.get(typeFactoryFacade.getByteType()));
		Assert.assertEquals("42", typeFormats.get(typeFactoryFacade.getBigIntegerType()));
		Assert.assertEquals("42", typeFormats.get(typeFactoryFacade.getShortType()));
		Assert.assertEquals(
				new SimpleDateFormat("dd MMMM yyyy").format(new Date()), 
				typeFormats.get(typeFactoryFacade.getCalendarType()));
		Assert.assertEquals(
				new SimpleDateFormat("dd MMMM yyyy").format(new Date()), 
				typeFormats.get(typeFactoryFacade.getCalendarDateType()));
		Assert.assertEquals("42", typeFormats.get(typeFactoryFacade.getIntegerType()));
		Assert.assertEquals("42", typeFormats.get(typeFactoryFacade.getBigDecimalType()));
		Assert.assertEquals("h", typeFormats.get(typeFactoryFacade.getCharacterType()));
		Assert.assertEquals(
				ITable.class.getName(), 
				typeFormats.get(typeFactoryFacade.getClassType()));
		Assert.assertEquals(
				Currency.getInstance(Locale.getDefault()).toString(), 
				typeFormats.get(typeFactoryFacade.getCurrencyType()));
		Assert.assertEquals(
				new SimpleDateFormat("dd MMMM yyyy").format(new Date()), 
				typeFormats.get(typeFactoryFacade.getDateType()));
		Assert.assertEquals("42.42", typeFormats.get(typeFactoryFacade.getDoubleType()));
		Assert.assertEquals("42.42", typeFormats.get(typeFactoryFacade.getFloatType()));
		Assert.assertEquals(
				Locale.getDefault().toString(), 
				typeFormats.get(typeFactoryFacade.getLocaleType()));
		Assert.assertEquals("42", typeFormats.get(typeFactoryFacade.getLongType()));
		Assert.assertEquals("a string", typeFormats.get(typeFactoryFacade.getStringType()));
		Assert.assertEquals("a text", typeFormats.get(typeFactoryFacade.getTextType()));
		Assert.assertEquals(8, typeFormats.get(typeFactoryFacade.getTimeType()).length());
		Assert.assertEquals(
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 
				typeFormats.get(typeFactoryFacade.getTimestampType()).substring(0, 10));
		Assert.assertEquals(
				TimeZone.getDefault().getID(), 
				typeFormats.get(typeFactoryFacade.getTimezoneType()));
		Assert.assertEquals("true", typeFormats.get(typeFactoryFacade.getTrueFalseType()));
		Assert.assertEquals("true", typeFormats.get(typeFactoryFacade.getYesNoType()));
	}
	
}
